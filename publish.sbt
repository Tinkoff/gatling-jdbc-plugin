ThisBuild / organization := "ru.tinkoff"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/TinkoffCreditSystems/gatling-jdbc-plugin"),
    "git@github.com:TinkoffCreditSystems/gatling-jdbc-plugin.git",
  ),
)

ThisBuild / developers := List(
  Developer(
    id = "red-bashmak",
    name = "Vyacheslav Kalyokin",
    email = "v.kalyokin@tinkoff.ru",
    url = url("https://github.com/red-bashmak"),
  ),
)

ThisBuild / description := "Simple gatling JDBC Plugin"
ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage    := Some(url("https://github.com/TinkoffCreditSystems/gatling-jdbc-plugin"))
