package ru.tinkoff.load.jdbc

import io.gatling.core.protocol.Protocol
import io.gatling.core.session.Expression
import ru.tinkoff.load.jdbc.actions.DBBaseAction

import ru.tinkoff.load.jdbc.check.JdbcCheckSupport
import ru.tinkoff.load.jdbc.protocol.{JdbcProtocolBuilder, JdbcProtocolBuilderBase}

trait JdbcDsl extends JdbcCheckSupport{
  def DB: JdbcProtocolBuilderBase.type             = JdbcProtocolBuilderBase
  def jdbc(name: Expression[String]): DBBaseAction = DBBaseAction(name)

  implicit def jdbcProtocolBuilder2jdbcProtocol(builder: JdbcProtocolBuilder): Protocol = builder.build
}
