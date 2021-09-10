package ru.tinkoff.load.jdbc.actions

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.validation._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen
import ru.tinkoff.load.jdbc.db.SQL

case class DBCallAction(
    requestName: Expression[String],
    procedureName: Expression[String],
    next: Action,
    ctx: ScenarioContext,
    sessionParams: Seq[(String, Expression[Any])],
    outParams: Seq[(String, Int)]
) extends ChainableAction with NameGen with ActionBase {

  override def name: String = genName("jdbcCallAction")

  private def makeCallString(procedureName: String, inParams: Map[String, Any], outParams: Map[String, Int]) =
    if (outParams.isEmpty) {
      s"CALL $procedureName (${inParams.keys.map(s => s"{$s}").mkString(",")})"
    } else {
      s"CALL $procedureName (${inParams.keys.map(s => s"{$s}").mkString(",")}, ${outParams.keys.map(s => s"$s =>{$s}").mkString(",")})"
    }

  override def execute(session: Session): Unit =
    (for {
      rn    <- requestName(session)
      pName <- procedureName(session)
      pParams <- sessionParams
                  .foldLeft(Map[String, Any]().success) {
                    case (r, (k, v)) =>
                      r.flatMap(m => v(session).map(rv => m + (k -> rv)))
                  }
      sql <- SQL(makeCallString(pName, pParams, outParams.toMap))
              .withParamsMap(pParams)
              .withOutParams(outParams)
              .success
      startTime <- ctx.coreComponents.clock.nowMillis.success

    } yield
      dbClient
        .call(sql.sql, sql.params, sql.outParams)(
          _ => executeNext(session, startTime, ctx.coreComponents.clock.nowMillis, OK, next, rn, None, None),
          e =>
            executeNext(session, startTime, ctx.coreComponents.clock.nowMillis, KO, next, rn, Some("ERROR"), Some(e.getMessage))
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
