package ru.tinkoff.load.jdbc.db

import java.sql._

import scala.util.Try
case class ManagedConnection(connection: Connection) {
  private def withStatement[T, S](acquire: => Try[S], release: S => Try[Unit])(use: S => Try[T]): Try[T] =
    for {
      stmt <- acquire
      r    <- use(stmt)
      _    <- release(stmt)
    } yield r

  private def putToCtx(ctx: Map[String, List[Int]], name: String, number: Int) = {
    val numbers = ctx.getOrElse(name, List.empty[Int])
    ctx ++ Map((name, number :: numbers))
  }

  private def prepareStatement(sqlCall: String,
                               params: Map[String, ParamVal],
                               outParams: Map[String, Int],
                               getStmt: (String, Connection) => PreparedStatement) = Try {
    val (queryString, _, _, _, ctx) = sqlCall.foldLeft(("", "", false, 0, Map.empty[String, List[Int]])) {
      case ((r, curName, false, n, ctx), '{') => (r, curName, true, n, ctx)
      case ((r, curName, true, n, ctx), '}')  => (s"$r ?", "", false, n + 1, putToCtx(ctx, curName, n + 1))
      case ((r, curName, true, n, ctx), c)    => (r, curName + c, true, n, ctx)

      case ((r, curName, false, n, ctx), c) => (s"$r$c", curName, false, n, ctx)
    }
    val stmt = getStmt(queryString, connection)
    ctx.foreach {
      case (name, l) if outParams.contains(name) =>
        l.foreach(i => stmt.asInstanceOf[CallableStatement].registerOutParameter(i, outParams(name)))
      case (name, l) =>
        params(name) match {
          case IntParam(v)    => l.foreach(stmt.setInt(_, v))
          case DoubleParam(v) => l.foreach(stmt.setDouble(_, v))
          case StrParam(v)    => l.foreach(stmt.setString(_, v))
          case LongParam(v)   => l.foreach(stmt.setLong(_, v))
          case NullParam      => l.foreach(stmt.setObject(_, null))
          case DateParam(v)   => l.foreach(stmt.setTimestamp(_, Timestamp.valueOf(v)))
        }
    }
    stmt
  }

  def execute(sqlQuery: String, params: Seq[(String, ParamVal)] = Seq.empty): Try[Int] =
    withStatement[Int, PreparedStatement](
      prepareStatement(sqlQuery, params.toMap, Map.empty, (sql, c) => c.prepareStatement(sql)),
      s => Try(s.close())
    ) { stmt =>
      Try(stmt.executeUpdate())

    }

  def call(sqlCall: String, params: Seq[(String, ParamVal)] = Seq.empty, outParams: Seq[(String, Int)] = Seq.empty): Try[Int] =
    withStatement[Int, PreparedStatement](
      prepareStatement(sqlCall, params.toMap, outParams.toMap, (sql, c) => c.prepareCall(s"{$sql}")),
      s => Try(s.close())
    ) { stmt =>
      Try(stmt.executeUpdate())
    }

  def execRaw(query: String): Try[Unit] =
    withStatement[Unit, Statement](Try(connection.createStatement()), s => Try(s.close())) { stmt =>
      Try(stmt.execute(query))
    }

  def execSelect(sql: String, params: Seq[(String, ParamVal)] = Seq.empty): Try[List[Map[String, Any]]] =
    withStatement[List[Map[String, Any]], PreparedStatement](
      prepareStatement(sql, params.toMap, Map.empty, (sql, c) => c.prepareStatement(sql)),
      s => Try(s.close())
    )(stmt => Try(stmt.executeQuery().iterator.toList))
}
