import sbt._

object Dependencies {

  val gatlingVersion = "3.7.6"

  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core"           % gatlingVersion % "provided",
    "io.gatling" % "gatling-test-framework" % gatlingVersion % "provided",
  )

  lazy val hikari = "com.zaxxer"     % "HikariCP" % "5.0.1"
  lazy val h2jdbc = "com.h2database" % "h2"       % "2.1.214" % Test

}
