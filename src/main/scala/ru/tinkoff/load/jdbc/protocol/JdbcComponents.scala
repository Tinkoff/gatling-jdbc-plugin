package ru.tinkoff.load.jdbc.protocol

import com.zaxxer.hikari.HikariDataSource
import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class JdbcComponents(pool: HikariDataSource) extends ProtocolComponents{
  override def onStart: Session => Session = Session.Identity

  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit
}
