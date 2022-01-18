package ru.tinkoff.load.jdbc.db

import com.zaxxer.hikari.HikariDataSource
import ru.tinkoff.load.jdbc.db.JDBCClient.Interpolator
import ru.tinkoff.load.jdbc.db.statements._

import java.util.concurrent.ExecutorService
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object JDBCClient {
  object Interpolator {
    type ParamToIndexesMap = Map[String, List[Int]]
    case class InterpolatorCtx(
        queryString: String,
        paramName: String,
        paramIndex: Int,
        inCurlyBraces: Boolean,
        m: ParamToIndexesMap,
    )
    private val emptyCtx = InterpolatorCtx("", "", 0, inCurlyBraces = false, Map.empty)

    private[this] def putToCtx(ctx: Map[String, List[Int]], name: String, number: Int) = {
      val numbers = ctx.getOrElse(name, List.empty[Int])
      ctx ++ Map((name, number :: numbers))
    }

    def interpolate(sql: String): InterpolatorCtx = sql.foldLeft(emptyCtx) {
      case (ic @ InterpolatorCtx(_, _, _, false, _), '{')    => ic.copy(inCurlyBraces = true)
      case (InterpolatorCtx(r, curName, n, true, ctx), '}')  =>
        InterpolatorCtx(s"$r ?", "", n + 1, inCurlyBraces = false, putToCtx(ctx, curName, n + 1))
      case (ic @ InterpolatorCtx(_, curName, _, true, _), c) => ic.copy(paramName = s"$curName$c")

      case (ic @ InterpolatorCtx(r, _, _, false, _), c) => ic.copy(queryString = s"$r$c")
    }
  }

  def apply(pool: HikariDataSource, blockingPool: ExecutorService): JDBCClient = new JDBCClient(pool, blockingPool)
}

class JDBCClient(pool: HikariDataSource, blockingPool: ExecutorService) {
  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(blockingPool)

  private def connectionResource: ResourceFut[ConnectionWrapper[Future]] =
    ResourceFut.make(Future(ConnectionWrapper.Impl(pool.getConnection, ec)))(_.close)

  private def statementForBatchResource =
    for {
      conn       <- connectionResource
      autoCommit <- ResourceFut.liftFuture(conn.getAutoCommit)
      _          <- ResourceFut.liftFuture(conn.setAutoCommit(false))
      stmt       <- ResourceFut.make(conn.createStatement.map(statement(_, ec)))(s =>
                      for {
                        _ <- conn.commit
                        _ <- conn.setAutoCommit(autoCommit)
                        _ <- s.close
                      } yield (),
                    )

    } yield stmt

  private def statementResource =
    for {
      conn <- connectionResource
      stmt <- ResourceFut.make(conn.createStatement.map(statement(_, ec)))(_.close)
    } yield stmt

  private def preparedStatementResource(sql: String, params: Map[String, ParamVal]) =
    for {
      conn            <- connectionResource
      interpolatedCtx <- ResourceFut.liftFuture(Future(Interpolator.interpolate(sql)))
      stmt            <- ResourceFut.make(
                           conn
                             .prepareStatement(interpolatedCtx.queryString)
                             .map(preparedStatement(_, ec)),
                         )(_.close)
      _               <- ResourceFut.liftFuture(stmt.setParams(interpolatedCtx, params))
    } yield stmt

  private def callableStatementResource(sql: String, inParams: Map[String, ParamVal], outParams: Map[String, Int]) =
    for {
      conn            <- connectionResource
      interpolatedCtx <- ResourceFut.liftFuture(Future(Interpolator.interpolate(sql)))
      stmt            <- ResourceFut.make(
                           conn
                             .prepareCall(s"{${interpolatedCtx.queryString}}")
                             .map(callableStatement(_, ec)),
                         )(_.close)
      _               <- ResourceFut.liftFuture(stmt.setParams(interpolatedCtx, inParams, outParams))
    } yield stmt

  private def withCompletion[T, U](fut: Future[T])(s: T => U, f: Throwable => U): Unit = fut.onComplete {
    case Success(value)     => s(value)
    case Failure(exception) => f(exception)
  }

  def executeRaw[U](sql: String)(s: Boolean => U, f: Throwable => U): Unit =
    withCompletion(statementResource.use(_.execute(sql)))(s, f)

  def executeSelect[U](sql: String, params: Seq[(String, ParamVal)])(s: List[Map[String, Any]] => U, f: Throwable => U): Unit =
    withCompletion(preparedStatementResource(sql, params.toMap).use(_.executeQuery.map(_.iterator.toList)))(s, f)

  def executeUpdate[U](sqlQuery: String, params: Seq[(String, ParamVal)])(s: Int => U, f: Throwable => U): Unit =
    withCompletion(preparedStatementResource(sqlQuery, params.toMap).use(_.executeUpdate))(s, f)

  def call[U](sqlCall: String, params: Seq[(String, ParamVal)], outParams: Seq[(String, Int)])(
      s: Int => U,
      f: Throwable => U,
  ): Unit =
    withCompletion(callableStatementResource(sqlCall, params.toMap, outParams.toMap).use(_.executeUpdate))(s, f)

  def batch[U](queries: Seq[SqlWithParam], batchSize: Int = 1000)(s: Array[Int] => U, f: Throwable => U): Unit =
    withCompletion(
      statementForBatchResource.use(stmt =>
        queries
          .map(_.substituteParams)
          .grouped(batchSize)
          .map(batch =>
            batch
              .map(query => stmt.addBatch(query))
              .reduce((f1, f2) => f1.flatMap(_ => f2))
              .flatMap(_ => stmt.executeBatch),
          )
          .reduce((f1, f2) => f1.flatMap(a1 => f2.map(a2 => a1 ++ a2))),
      ),
    )(s, f)

  def close(): Unit = {
    pool.close()
    blockingPool.shutdown()
  }
}
