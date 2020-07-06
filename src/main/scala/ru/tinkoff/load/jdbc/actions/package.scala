package ru.tinkoff.load.jdbc

import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext

package object actions {
  case class DBBaseAction(requestName: Expression[String]) {
    def insertInto(tableName: Expression[String], columns: Columns): DBInsertActionValuesStep =
      DBInsertActionValuesStep(requestName, tableName, columns)
    def call(procedureName: Expression[String]): DBCallActionParamsStep = DBCallActionParamsStep(requestName, procedureName)

    def rawSql(queryString: Expression[String]): RawSqlActionBuilder = RawSqlActionBuilder(requestName, queryString)

    def queryP(sql: Expression[String]): QueryActionParamsStep = QueryActionParamsStep(requestName, sql)
    def query(sql: Expression[String]): QueryActionBuilder = QueryActionBuilder(requestName, sql, params = Seq.empty)
  }

  case class QueryActionParamsStep(requestName: Expression[String], sql: Expression[String]) {
    def params(ps: (String, Expression[Any])*): QueryActionBuilder = QueryActionBuilder(requestName, sql, ps)
  }

  case class QueryActionBuilder(requestName: Expression[String],
                                sql: Expression[String],
                                params: Seq[(String, Expression[Any])],
                                checks: Seq[JdbcCheck] = Seq.empty)
      extends ActionBuilder {
    def check(newChecks: JdbcCheck*): QueryActionBuilder = this.copy(checks = newChecks)

    override def build(ctx: ScenarioContext, next: Action): Action = DBQueryAction(
      requestName,
      sql,
      params,
      checks,
      next,
      ctx
    )
  }

  case class RawSqlActionBuilder(requestName: Expression[String], query: Expression[String]) extends ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action = DBRawQueryAction(requestName, query, ctx, next)
  }

  case class Columns(names: String*)

  case class DBCallActionParamsStep(requestName: Expression[String], procedureName: Expression[String]) {
    def params(ps: (String, Expression[Any])*): DBCallActionBuilder = DBCallActionBuilder(requestName, procedureName, ps)
  }

  case class DBCallActionBuilder(requestName: Expression[String],
                                 procedureName: Expression[String],
                                 sessionParams: Seq[(String, Expression[Any])],
                                 outParams: Seq[(String, Int)] = Seq.empty)
      extends ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action =
      DBCallAction(requestName, procedureName, next, ctx, sessionParams, outParams)

    def outParams(ps: (String, Int)*): DBCallActionBuilder = this.copy(outParams = ps)
  }

  case class DBInsertActionValuesStep(requestName: Expression[String], tableName: Expression[String], columns: Columns) {
    def values(values: (String, Expression[Any])*): DBInsertActionBuilder =
      DBInsertActionBuilder(requestName, tableName, columns, values)
  }

  case class DBInsertActionBuilder(requestName: Expression[String],
                                   tableName: Expression[String],
                                   columns: Columns,
                                   sessionValues: Seq[(String, Expression[Any])] = Seq.empty)
      extends ActionBuilder {
    override def build(ctx: ScenarioContext, next: Action): Action =
      DBInsertAction(requestName, tableName, columns.names, next, ctx, sessionValues)
  }

}
