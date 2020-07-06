package ru.tinkoff.load.jdbc.protocol

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}

object JdbcProtocol {
  val jdbcProtocolKey: ProtocolKey[JdbcProtocol, JdbcComponents] = new ProtocolKey[JdbcProtocol, JdbcComponents] {
    override def protocolClass: Class[Protocol] = classOf[JdbcProtocol].asInstanceOf[Class[Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): JdbcProtocol =
      throw new IllegalStateException("Can't provide a default value for JdbcProtocol")

    override def newComponents(coreComponents: CoreComponents): JdbcProtocol => JdbcComponents =
      protocol => {
        val hikariConfig = new HikariConfig()
        hikariConfig.setUsername(protocol.username)
        hikariConfig.setPassword(protocol.password)
        hikariConfig.setJdbcUrl(protocol.url)

        JdbcComponents(new HikariDataSource(hikariConfig))
      }
  }

}

case class JdbcProtocol(username: String, password: String, url: String) extends Protocol {
  type Components = JdbcComponents
}