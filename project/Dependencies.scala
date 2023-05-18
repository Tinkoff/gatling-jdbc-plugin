import sbt._

object Dependencies {

  val gatlingVersion = "3.9.5"

  lazy val gatlingCore: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core",
    "io.gatling" % "gatling-core-java",
  ).map(_ % gatlingVersion % "provided")

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling"            % "gatling-test-framework",
    "io.gatling.highcharts" % "gatling-charts-highcharts",
  ).map(_ % gatlingVersion % "it,test")

  lazy val hikari = "com.zaxxer"     % "HikariCP" % "5.0.1" exclude ("org.slf4j", "slf4j-api")
  lazy val h2jdbc = "com.h2database" % "h2"       % "2.1.214" % Test

}
