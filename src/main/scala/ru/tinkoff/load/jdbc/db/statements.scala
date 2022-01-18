package ru.tinkoff.load.jdbc.db

import ru.tinkoff.load.jdbc.db.JDBCClient.Interpolator.InterpolatorCtx

import java.sql.{CallableStatement, PreparedStatement, ResultSet, Statement, Timestamp}
import scala.concurrent.{ExecutionContext, Future}

object statements {

  trait StatementWrapper[F[_]] {
    def execute(sql: String): F[Boolean]
    def close: F[Unit]
    def addBatch(sql: String): F[Unit]
    def executeBatch: F[Array[Int]]
  }

  trait PreparedStatementWrapper[F[_]] {
    def executeQuery: F[ResultSet]
    def close: F[Unit]
    def executeUpdate: F[Int]
    def setInt(index: Int, value: Int): F[Unit]
    def setDouble(index: Int, value: Double): F[Unit]
    def setString(index: Int, value: String): F[Unit]
    def setLong(index: Int, value: Long): F[Unit]
    def setObject(index: Int, value: Object): F[Unit]
    def setTimestamp(index: Int, value: java.sql.Timestamp): F[Unit]
    def setParams(interpolated: InterpolatorCtx, params: Map[String, ParamVal]): F[Unit]
  }

  trait CallableStatementWrapper[F[_]] {
    def close: F[Unit]
    def executeUpdate: F[Int]
    def setInt(index: Int, value: Int): F[Unit]
    def setDouble(index: Int, value: Double): F[Unit]
    def setString(index: Int, value: String): F[Unit]
    def setLong(index: Int, value: Long): F[Unit]
    def setObject(index: Int, value: Object): F[Unit]
    def setTimestamp(index: Int, value: java.sql.Timestamp): F[Unit]
    def registerOutParameter(index: Int, sqlType: Int): F[Unit]
    def setParams(interpolated: InterpolatorCtx, inParams: Map[String, ParamVal], outParams: Map[String, Int]): Future[Unit]
  }

  private final class StatementWrapperImpl(stmt: Statement)(implicit ec: ExecutionContext) extends StatementWrapper[Future] {
    override def execute(sql: String): Future[Boolean] = Future(stmt.execute(sql))

    override def close: Future[Unit] = Future(stmt.close())

    override def addBatch(sql: String): Future[Unit] = Future(stmt.addBatch(sql))

    override def executeBatch: Future[Array[Int]] = Future(stmt.executeBatch())
  }

  private final class PreparedStatementWrapperImpl(stmt: PreparedStatement)(implicit ec: ExecutionContext)
      extends PreparedStatementWrapper[Future] {
    override def executeQuery: Future[ResultSet] = Future(stmt.executeQuery())

    override def close: Future[Unit] = Future(stmt.close())

    override def executeUpdate: Future[Int] = Future(stmt.executeUpdate())

    override def setDouble(index: Int, value: Double): Future[Unit] = Future(stmt.setDouble(index, value))

    override def setString(index: Int, value: String): Future[Unit] = Future(stmt.setString(index, value))

    override def setLong(index: Int, value: Long): Future[Unit] = Future(stmt.setLong(index, value))

    override def setObject(index: Int, value: Object): Future[Unit] = Future(stmt.setObject(index, value))

    override def setTimestamp(index: Int, value: Timestamp): Future[Unit] = Future(stmt.setTimestamp(index, value))

    override def setInt(index: Int, value: Int): Future[Unit] = Future(stmt.setInt(index, value))

    override def setParams(interpolated: InterpolatorCtx, params: Map[String, ParamVal]): Future[Unit] = {
      if (params.isEmpty)
        Future.successful(())
      else
        interpolated.m.flatMap { case (name, indexes) =>
          params(name) match {
            case IntParam(v)    => indexes.map(this.setInt(_, v))
            case DoubleParam(v) => indexes.map(this.setDouble(_, v))
            case StrParam(v)    => indexes.map(this.setString(_, v))
            case LongParam(v)   => indexes.map(this.setLong(_, v))
            case NullParam      => indexes.map(this.setObject(_, null))
            case DateParam(v)   => indexes.map(this.setTimestamp(_, Timestamp.valueOf(v)))
          }
        }.reduce((f1, f2) => f1.flatMap(_ => f2))
    }
  }

  private final class CallableStatementWrapperImpl(stmt: CallableStatement)(implicit ec: ExecutionContext)
      extends CallableStatementWrapper[Future] {
    override def close: Future[Unit] = Future(stmt.close())

    override def executeUpdate: Future[Int] = Future(stmt.executeUpdate())

    override def setDouble(index: Int, value: Double): Future[Unit] = Future(stmt.setDouble(index, value))

    override def setString(index: Int, value: String): Future[Unit] = Future(stmt.setString(index, value))

    override def setLong(index: Int, value: Long): Future[Unit] = Future(stmt.setLong(index, value))

    override def setObject(index: Int, value: Object): Future[Unit] = Future(stmt.setObject(index, value))

    override def setTimestamp(index: Int, value: Timestamp): Future[Unit] = Future(stmt.setTimestamp(index, value))

    override def setInt(index: Int, value: Int): Future[Unit] = Future(stmt.setInt(index, value))

    override def registerOutParameter(index: Int, sqlType: Int): Future[Unit] =
      Future(stmt.registerOutParameter(index, sqlType))

    override def setParams(
        interpolated: InterpolatorCtx,
        inParams: Map[String, ParamVal],
        outParams: Map[String, Int],
    ): Future[Unit] = {
      if (inParams.isEmpty && outParams.isEmpty)
        Future.successful(())
      else
        interpolated.m.flatMap {
          case (name, indexes) if outParams.contains(name) =>
            indexes.map(this.registerOutParameter(_, outParams(name)))
          case (name, indexes)                             =>
            inParams(name) match {
              case IntParam(v)    => indexes.map(this.setInt(_, v))
              case DoubleParam(v) => indexes.map(this.setDouble(_, v))
              case StrParam(v)    => indexes.map(this.setString(_, v))
              case LongParam(v)   => indexes.map(this.setLong(_, v))
              case NullParam      => indexes.map(this.setObject(_, null))
              case DateParam(v)   => indexes.map(this.setTimestamp(_, Timestamp.valueOf(v)))
            }
        }.reduce((f1, f2) => f1.flatMap(_ => f2))
    }
  }

  def statement(statement: Statement, ec: ExecutionContext): StatementWrapper[Future] = new StatementWrapperImpl(statement)(ec)

  def preparedStatement(preparedStatement: PreparedStatement, ec: ExecutionContext): PreparedStatementWrapper[Future] =
    new PreparedStatementWrapperImpl(preparedStatement)(ec)

  def callableStatement(callableStatement: CallableStatement, ec: ExecutionContext): CallableStatementWrapper[Future] =
    new CallableStatementWrapperImpl(callableStatement)(ec)
}
