package ru.tinkoff.load.jdbc.test.cases

import io.gatling.core.Predef._
import ru.tinkoff.load.jdbc.Predef._
import ru.tinkoff.load.jdbc.actions
import ru.tinkoff.load.jdbc.actions.Columns

object Actions {

  def createTable(): actions.RawSqlActionBuilder = jdbc("Create Table")
    .rawSql("CREATE TABLE TEST_TABLE (ID INT PRIMARY KEY, NAME VARCHAR(64));")

  def createProcedure(): actions.RawSqlActionBuilder = jdbc("Procedure create")
    .rawSql("""CREATE ALIAS TEST_PROCEDURE AS $$
              |String testProcedure(String p1, Long p2) {
              |    String suf = p1 + "test";
              |    return p2.toString() + suf;
              |}
              |$$;""".stripMargin)

  def insertTest(): actions.DBInsertActionBuilder =
    jdbc("INSERT TEST")
      .insertInto("TEST_TABLE", Columns("ID", "NAME"))
      .values("ID" -> 1, "NAME" -> "Test3")

  def callTest(): actions.DBCallActionBuilder =
    jdbc("CALL PROCEDURE TEST")
      .call("TEST_PROCEDURE")
      .params("p1" -> "value1", "p2" -> 24L)

  def selectTest: actions.QueryActionBuilder = jdbc("SELECT TEST")
    .queryP("SELECT * FROM TEST_TABLE WHERE ID = {id}")
    .params("id" -> 1)
    .check(
    allRecordsCheck{
      r =>
        r.isEmpty
    },
    allResults.is(List(
      Map("ID"-> 1, "NAME" -> "Test3")
    )),
    allResults.saveAs("R")
  )

}
