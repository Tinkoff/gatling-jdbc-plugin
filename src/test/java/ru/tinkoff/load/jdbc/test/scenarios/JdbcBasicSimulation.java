package ru.tinkoff.load.jdbc.test.scenarios;

import io.gatling.javaapi.core.ScenarioBuilder;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static ru.tinkoff.load.jdbc.test.cases.JdbcActions.*;

public class JdbcBasicSimulation {
    public static ScenarioBuilder scn = scenario("JDBC scenario")
            .exec(createTable())
            .exec(createprocedure())
            .exec(insertTest())
            .exec(callTest())
            .exec(batchTest())
            .exec(selectTT())
            .exec(selectTest())
            .exec(selectAfterBatch())
            .exec(checkTestTableAfterBatch())
            ;
}
