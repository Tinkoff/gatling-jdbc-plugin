package ru.tinkoff.load.jdbc.protocol

import io.gatling.core.protocol.Protocol


case object JdbcProtocolBuilderBase {

  def url(url: String): JdbcProtocolBuilderUsernameStep = JdbcProtocolBuilderUsernameStep(url)

}

case class JdbcProtocolBuilderUsernameStep(url: String) {

  def username(name: String): JdbcProtocolBuilderPasswordStep = JdbcProtocolBuilderPasswordStep(url, name)

}

case class JdbcProtocolBuilderPasswordStep(url: String, username: String) {

  def password(pwd: String): JdbcProtocolBuilder = JdbcProtocolBuilder(url, username, pwd)

}


case class JdbcProtocolBuilder(url: String, username: String, pwd: String) {

  def build: Protocol = JdbcProtocol(username, pwd, url)

}