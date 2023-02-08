package ru.tinkoff.load.jdbc.test.scenarios;

import io.gatling.javaapi.core.ScenarioBuilder;
import ru.tinkoff.load.jdbc.test.cases.JdbcActions;


import static io.gatling.javaapi.core.CoreDsl.scenario;

public class JdbcBasicSimulation {
    public static ScenarioBuilder scn = scenario("JDBC scenario")
            .exec(JdbcActions.createTable());
}
