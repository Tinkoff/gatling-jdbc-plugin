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
        JdbcComponents(new HikariDataSource(protocol.hikariConfig))
      }
  }

}

case class JdbcProtocol(hikariConfig: HikariConfig) extends Protocol {
  type Components = JdbcComponents
}
