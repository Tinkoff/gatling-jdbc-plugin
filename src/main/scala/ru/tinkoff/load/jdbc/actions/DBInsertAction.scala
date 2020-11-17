package ru.tinkoff.load.jdbc.actions

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.validation._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen
import ru.tinkoff.load.jdbc.db.SQL

case class DBInsertAction(
    requestName: Expression[String],
    tableName: Expression[String],
    columns: Seq[String],
    next: Action,
    ctx: ScenarioContext,
    sessionValues: Seq[(String, Expression[Any])]
) extends ChainableAction with NameGen with ActionBase {
  override def name: String = genName("jdbcInsertAction")

  override def execute(session: Session): Unit =
    (for {
      rn    <- requestName(session)
      tName <- tableName(session)
      iParams <- sessionValues
                  .foldLeft(Map[String, Any]().success) {
                    case (r, (k, v)) => r.flatMap(m => v(session).map(rv => m + (k -> rv)))
                  }
      sql <- SQL(s"INSERT INTO $tName (${columns.mkString(",")}) VALUES(${columns.map(s => s"{$s}").mkString(",")})")
              .withParamsMap(iParams)
              .success
      startTime <- ctx.coreComponents.clock.nowMillis.success

    } yield
      db.executeUpdate(implicit c => sql.executeInsert)
        .fold(
          e => {
            println(s"ERROR: ${e.getMessage}")
            executeNext(session, startTime, ctx.coreComponents.clock.nowMillis, KO, next, rn, Some("ERROR"), Some(e.getMessage))
          },
          _ => executeNext(session, startTime, ctx.coreComponents.clock.nowMillis, OK, next, rn, None, None)
        )).onFailure(m =>
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
