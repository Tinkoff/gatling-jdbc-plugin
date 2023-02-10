package ru.tinkoff.load.jdbc.test.scenarios;

import io.gatling.javaapi.core.ScenarioBuilder;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static ru.tinkoff.load.jdbc.test.cases.JdbcActions.createTable;

public class JdbcBasicSimulation {
    public static ScenarioBuilder scn = scenario("JDBC scenario")
            .exec(createTable());
}
