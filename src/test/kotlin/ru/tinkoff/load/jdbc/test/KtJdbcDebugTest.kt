package ru.tinkoff.load.jdbc.test

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.*
import ru.tinkoff.load.jdbc.test.scenarios.KtJdbcBasicSimulation.scn

class KtJdbcDebugTest : Simulation()  {
    init{
            setUp(
                    scn.injectOpen(atOnceUsers(1))
            ).protocols(KtJdbcProtocol.dataBase)
    }
}
