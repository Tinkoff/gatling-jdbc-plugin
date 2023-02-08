package ru.tinkoff.load.jdbc.test.scenarios

import ru.tinkoff.load.jdbc.test.cases.JdbcActions

object JdbcBasicSimulation {
    var scn: io.gatling.javaapi.core.ScenarioBuilder = io.gatling.javaapi.core.CoreDsl.scenario("JDBC scenario")
            .exec(JdbcActions.createTable())
}