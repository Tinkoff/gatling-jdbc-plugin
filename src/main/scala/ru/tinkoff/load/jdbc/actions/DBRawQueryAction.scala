package ru.tinkoff.load.jdbc.actions

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.validation._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen
import ru.tinkoff.load.jdbc.db.SQL

case class DBRawQueryAction(requestName: Expression[String], query: Expression[String], ctx: ScenarioContext, next: Action)
    extends ChainableAction with NameGen with ActionBase {
  override def name: String = genName("jdbcInsertAction")

  override def execute(session: Session): Unit =
    (for {
      resolvedName  <- requestName(session)
      resolvedQuery <- query(session)
      sql           <- SQL(resolvedQuery).success
      startTime     <- ctx.coreComponents.clock.nowMillis.success

    } yield
      db.executeRaw(implicit c => sql.executeRaw)
        .fold(
          e => {
            println(s"ERROR: ${e.getMessage}")
            executeNext(session,
                        startTime,
                        ctx.coreComponents.clock.nowMillis,
                        KO,
                        next,
                        resolvedName,
                        Some("ERROR"),
                        Some(e.getMessage))
          },
          _ => executeNext(session, startTime, ctx.coreComponents.clock.nowMillis, OK, next, resolvedName, None, None)
        ))
      .onFailure(m =>
        requestName(session).map { rn =>
          ctx.coreComponents.statsEngine.logCrash(session.scenario, session.groups, rn, m)
          executeNext(session,
                      ctx.coreComponents.clock.nowMillis,
                      ctx.coreComponents.clock.nowMillis,
                      KO,
                      next,
                      rn,
                      Some("ERROR"),
                      Some(m))
      })
}
