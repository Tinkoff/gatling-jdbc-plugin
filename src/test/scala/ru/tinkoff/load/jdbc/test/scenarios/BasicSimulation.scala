package ru.tinkoff.load.jdbc.test.scenarios

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import ru.tinkoff.load.jdbc.test.cases.Actions

object BasicSimulation {
  def apply(): ScenarioBuilder = new BasicSimulation().scn

}

class BasicSimulation {

  val scn: ScenarioBuilder = scenario("Basic")
    .exec(Actions.createTable())
    .exec(Actions.createProcedure())
    .exec(Actions.insertTest())
    .exec(Actions.callTest())
    .exec(Actions.selectTest)
    .exec { s =>
      print(s("R").as[List[Map[String, Any]]])
      s
    }

}
