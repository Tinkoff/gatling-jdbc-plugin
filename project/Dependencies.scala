import sbt._

object Dependencies {
  lazy val gatling: Seq[ModuleID] = Seq(
    "io.gatling" % "gatling-core"           % "3.4.1" % "provided",
    "io.gatling" % "gatling-test-framework" % "3.4.1" % "provided"
  )

  lazy val hikari = "com.zaxxer" % "HikariCP" % "3.4.5"
  // https://mvnrepository.com/artifact/com.h2database/h2
  lazy val h2jdbc = "com.h2database" % "h2" % "1.4.200" % Test

}
