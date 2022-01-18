import sbt._

object Dependencies {
  val gatlingVersion              = "3.7.3"
  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core"           % gatlingVersion % "provided",
    "io.gatling" % "gatling-test-framework" % gatlingVersion % "provided",
  )

  lazy val hikari = "com.zaxxer"     % "HikariCP" % "5.0.1"
  // https://mvnrepository.com/artifact/com.h2database/h2
  lazy val h2jdbc = "com.h2database" % "h2"       % "2.1.210" % Test

}
