import sbt._

object Dependencies {

  val gatlingVersion = "3.9.0"

  lazy val gatlingCore: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core"      % gatlingVersion % "provided",
    "io.gatling" % "gatling-core-java" % gatlingVersion % "provided",
  )

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling.highcharts" % "gatling-charts-highcharts",
    "io.gatling"            % "gatling-test-framework",
  ).map(_ % gatlingVersion)

  lazy val hikari = "com.zaxxer"     % "HikariCP" % "5.0.1" exclude ("org.slf4j", "slf4j-api")
  lazy val h2jdbc = "com.h2database" % "h2"       % "2.1.214" % Test

}
