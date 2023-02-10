package ru.tinkoff.load.jdbc

import io.gatling.core.protocol.Protocol
import io.gatling.core.session.Expression
import ru.tinkoff.load.jdbc.actions.actions.{BatchInsertBaseAction, BatchUpdateBaseAction, Columns, DBBaseAction}
import ru.tinkoff.load.jdbc.check.JdbcCheckSupport
import ru.tinkoff.load.jdbc.protocol.{JdbcProtocolBuilder, JdbcProtocolBuilderBase, JdbcProtocolBuilderConnectionSettingsStep}

trait JdbcDsl extends JdbcCheckSupport {
  def DB: JdbcProtocolBuilderBase.type                                                   = JdbcProtocolBuilderBase
  def jdbc(name: Expression[String]): DBBaseAction                                       = DBBaseAction(name)
  def insertInto(tableName: Expression[String], columns: Columns): BatchInsertBaseAction =
    BatchInsertBaseAction(tableName, columns)
  def update(tableName: Expression[String]): BatchUpdateBaseAction                       = BatchUpdateBaseAction(tableName)

  implicit def configStepToProtocolBuilder(step: JdbcProtocolBuilderConnectionSettingsStep): JdbcProtocolBuilder =
    step.protocolBuilder
  implicit def jdbcProtocolBuilder2jdbcProtocol(builder: JdbcProtocolBuilder): Protocol                          = builder.build
}
