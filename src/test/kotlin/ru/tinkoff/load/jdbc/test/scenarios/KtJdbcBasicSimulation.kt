package ru.tinkoff.load.jdbc.test.scenarios

import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.ScenarioBuilder
import ru.tinkoff.load.jdbc.test.cases.KtJdbcActions



object KtJdbcBasicSimulation {
    var scn: ScenarioBuilder = scenario("JDBC scenario")
        .exec(KtJdbcActions.createTable())
        .exec(KtJdbcActions.createprocedure())
        .exec(KtJdbcActions.insertTest())
        .exec(KtJdbcActions.callTest())
        .exec(KtJdbcActions.batchTest())
        .exec(KtJdbcActions.selectTest())
        .exec(KtJdbcActions.selectTT())
        .exec(KtJdbcActions.selectAfterBatch())
        .exec(KtJdbcActions.checkTestTableAfterBatch())

}