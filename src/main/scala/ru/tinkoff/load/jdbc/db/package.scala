package ru.tinkoff.load.jdbc

import java.sql.ResultSet
import java.time.{LocalDateTime, OffsetDateTime}
import scala.util.Try

package object db {

  sealed trait ParamVal

  case class IntParam(v: Int)            extends ParamVal
  case class LongParam(v: Long)          extends ParamVal
  case object NullParam                  extends ParamVal
  case class DoubleParam(v: Double)      extends ParamVal
  case class StrParam(v: String)         extends ParamVal
  case class DateParam(v: LocalDateTime) extends ParamVal

  case class SQL(q: String) {
    def withParams(params: (String, ParamVal)*): SqlWithParam = SqlWithParam(q, params)

    def withParamsMap(m: Map[String, Any]): SqlWithParam =
      withParams(m.map {
        case (k, v: Int)           => (k, IntParam(v))
        case (k, v: Long)          => (k, LongParam(v))
        case (k, v: Double)        => (k, DoubleParam(v))
        case (k, "NULL")           => (k, NullParam)
        case (k, v: String)        => (k, StrParam(v))
        case (k, v: LocalDateTime) => (k, DateParam(v))
        case (k, v)                => (k, StrParam(v.toString))
      }.toSeq: _*)

    def executeInsert(implicit managedConnection: ManagedConnection): Try[Int] = managedConnection.execute(q)

    def call(implicit managedConnection: ManagedConnection): Try[Int] = managedConnection.call(q)

    def executeRaw(implicit managedConnection: ManagedConnection): Try[Unit] = managedConnection.execRaw(q)
  }

  case class SqlWithParam(sql: String, params: Seq[(String, ParamVal)], outParams: Seq[(String, Int)] = Seq.empty) {
    private val paramsMap = params.toMap
    private def paramValueToSql(name: String) =
      paramsMap.get(name) match {
        case Some(IntParam(v))    => s"$v"
        case Some(DoubleParam(v)) => s"$v"
        case Some(StrParam(v))    => s"'$v'"
        case Some(LongParam(v))   => s"$v"
        case Some(NullParam)      => "NULL"
        case Some(DateParam(v))   => s"CAST('$v' AS TIMESTAMP)"
        case None                 => ""
      }

    def substituteParams: String = {
      sql
        .foldLeft(("", "", false)) {
          case ((r, curName, false), '{') => (r, curName, true)
          case ((r, curName, true), '}')  => (s"$r ${paramValueToSql(curName.trim)}", "", false)
          case ((r, curName, true), c)    => (r, s"$curName$c", true)
          case ((r, curName, false), c)   => (s"$r$c", curName, false)
        }
        ._1
    }

    def withOutParams(ps: Seq[(String, Int)]): SqlWithParam = SqlWithParam(sql, params, ps)

    def executeInsert(implicit managedConnection: ManagedConnection): Try[Int] = managedConnection.execute(sql, params)

    def call(implicit managedConnection: ManagedConnection): Try[Int] = managedConnection.call(sql, params, outParams)

    def executeQuery(implicit managedConnection: ManagedConnection): Try[List[Map[String, Any]]] =
      managedConnection.execSelect(sql, params)
  }

  private def record(resultSet: ResultSet): Map[String, Any] = {
    val md      = resultSet.getMetaData
    val columns = md.getColumnCount
    (1 to columns).foldLeft(Map.empty[String, Any]) { (m, i) =>
      m + (md.getColumnName(i) -> resultSet.getObject(i))
    }
  }

  implicit class ResultSetOps(val rs: ResultSet) extends AnyVal {

    def iterator: Iterator[Map[String, Any]] =
      new Iterator[Map[String, Any]] {
        override def hasNext: Boolean = rs.next()

        override def next(): Map[String, Any] = record(rs)
      }

  }

}
