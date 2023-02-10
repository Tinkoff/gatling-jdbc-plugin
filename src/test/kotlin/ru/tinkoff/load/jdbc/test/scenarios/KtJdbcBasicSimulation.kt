package ru.tinkoff.load.jdbc.test.scenarios

import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.ScenarioBuilder
import ru.tinkoff.load.jdbc.test.cases.KtJdbcActions

object KtJdbcBasicSimulation {
    var scn: ScenarioBuilder = scenario("JDBC scenario")
            .exec(KtJdbcActions.createTable())
}