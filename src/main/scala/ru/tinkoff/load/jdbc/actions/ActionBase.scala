package ru.tinkoff.load.jdbc.actions

import io.gatling.commons.stats.Status
import io.gatling.core.action.Action
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioContext
import ru.tinkoff.load.jdbc.db.ConnectedDB
import ru.tinkoff.load.jdbc.protocol.JdbcProtocol

trait ActionBase {
  val ctx: ScenarioContext

  private val jdbcComponents    = ctx.protocolComponentsRegistry.components(JdbcProtocol.jdbcProtocolKey)
  protected val db: ConnectedDB = ConnectedDB(jdbcComponents.pool)

  protected def executeNext(
      session: Session,
      sent: Long,
      received: Long,
      status: Status,
      next: Action,
      requestName: String,
      responseCode: Option[String],
      message: Option[String]
  ): Unit = {
    ctx.coreComponents.statsEngine.logResponse(session.scenario,
                                               session.groups,
                                               requestName,
                                               sent,
                                               received,
                                               status,
                                               responseCode,
                                               message)
    next ! session.logGroupRequestTimings(sent, received)
  }
}
