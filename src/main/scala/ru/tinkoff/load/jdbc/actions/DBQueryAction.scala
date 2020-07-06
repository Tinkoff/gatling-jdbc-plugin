package ru.tinkoff.load.jdbc.actions

import java.util.{HashMap => JHashMap}

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.validation._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.check.Check
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen
import ru.tinkoff.load.jdbc.JdbcCheck
import ru.tinkoff.load.jdbc.db.SQL

case class DBQueryAction(
    requestName: Expression[String],
    sql: Expression[String],
    params: Seq[(String, Expression[Any])],
    checks: Seq[JdbcCheck],
    next: Action,
    ctx: ScenarioContext
) extends ChainableAction with NameGen with ActionBase {

  override def name: String = genName("jdbcQueryAction")

  private def resolveParams(session: Session) =
    params
      .foldLeft(Map[String, Any]().success) {
        case (r, (k, v)) => r.flatMap(m => v(session).map(rv => m + (k -> rv)))
      }

  override def execute(session: Session): Unit =
    (for {
      resolvedName    <- requestName(session)
      resolvedQuery   <- sql(session)
      resolvedParams  <- resolveParams(session)
      parametrisedSql <- SQL(resolvedQuery).withParamsMap(resolvedParams).success
      startTime       <- ctx.coreComponents.clock.nowMillis.success

    } yield
      db.executeQuery(implicit c => parametrisedSql.executeQuery)
        .fold(
          e => {
            executeNext(session,
                        startTime,
                        ctx.coreComponents.clock.nowMillis,
                        KO,
                        next,
                        resolvedName,
                        Some("ERROR"),
                        Some(e.getMessage))
          },
          r => {
            val received            = ctx.coreComponents.clock.nowMillis
            val (newSession, error) = Check.check(r, session, checks.toList, new JHashMap[Any, Any]())

            error match {
              case Some(Failure(errorMessage)) =>
                executeNext(newSession.markAsFailed,
                            startTime,
                            received,
                            KO,
                            next,
                            resolvedName,
                            Some("Check ERROR"),
                            Some(errorMessage))
              case _ => executeNext(newSession, startTime, received, OK, next, resolvedName, None, None)
            }
          }
        ))
      .onFailure(m =>
        requestName(session).map { rn =>
          ctx.coreComponents.statsEngine.logCrash(session, rn, m)
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
