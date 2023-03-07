import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder
import ru.tinkoff.load.jdbc.test.JdbcDebugTest;

object GatlingRunner {

  def main(args: Array[String]): Unit = {

    // this is where you specify the class you want to run
    val simulationClass = classOf[JdbcDebugTest].getName

    val props = new GatlingPropertiesBuilder
    props.simulationClass(simulationClass)

    Gatling.fromMap(props.build)
  }

}
