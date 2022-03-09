package ru.tinkoff.load.jdbc.test.cases

import io.gatling.core.Predef.{find2Final, stringToExpression, value2Expression}
import ru.tinkoff.load.jdbc.Predef._
import ru.tinkoff.load.jdbc.actions
import ru.tinkoff.load.jdbc.actions.Columns

import java.time.LocalDateTime
import java.util.UUID

object Actions {

  def createTable(): actions.RawSqlActionBuilder =
    jdbc("Create Table")
      .rawSql(
        "CREATE TABLE TEST_TABLE (ID INT PRIMARY KEY, NAME VARCHAR(64), CREATED_AT DATE DEFAULT now(), flag BOOLEAN DEFAULT false);" +
          "CREATE TABLE TT (ID UUID, NAME VARCHAR(64));",
      )

  def createProcedure(): actions.RawSqlActionBuilder =
    jdbc("Procedure create")
      .rawSql("""CREATE ALIAS TEST_PROCEDURE AS $$
              |String testProcedure(String p1, Long p2) {
              |    String suf = p1 + "test";
              |    return p2.toString() + suf;
              |}
              |$$;""".stripMargin)

  def insertTest(): actions.DBInsertActionBuilder =
    jdbc("INSERT TEST")
      .insertInto("TEST_TABLE", Columns("ID", "NAME"))
      .values("ID" -> "${i}", "NAME" -> "Test3")

  def callTest(): actions.DBCallActionBuilder =
    jdbc("CALL PROCEDURE TEST")
      .call("TEST_PROCEDURE")
      .params("p1" -> "value1", "p2" -> 24L)

  def batchTest: actions.BatchActionBuilder = jdbc("Batch records").batch(
    insertInto("TEST_TABLE", Columns("ID", "NAME", "FLAG")).values("ID" -> 20, "NAME"                -> "Test 12", "FLAG"          -> true),
    insertInto("TEST_TABLE", Columns("ID", "NAME")).values("ID"         -> 40, "NAME"                -> "Test 34"),
    insertInto("TEST_TABLE", Columns("ID", "NAME", "CREATED_AT"))
      .values("ID"                                                      -> 30, "NAME"                -> "Test  ${i}", "CREATED_AT" -> LocalDateTime.now().minusMonths(6)),
    update("TEST_TABLE").set("NAME" -> "TEST 5").where("ID = 2"),
    insertInto("TT", Columns("ID", "NAME")).values("ID"                 -> UUID.randomUUID(), "NAME" -> "OOO342ff"),
//    update("TEST_TABLE").set("NAME" -> "bird").all
  )

  def selectTT: actions.QueryActionBuilder =
    jdbc("select tt").query("SELECT * FROM TT").check(simpleCheck(_.nonEmpty), allResults.saveAs("ttr"))

  def selectTest: actions.QueryActionBuilder =
    jdbc("SELECT TEST")
      .queryP("SELECT * FROM TEST_TABLE WHERE ID = {id}")
      .params("id" -> 1)
      .check(
        simpleCheck(x => x.nonEmpty),
        allResults.saveAs("R"),
      )

  def selectAfterBatch: actions.QueryActionBuilder =
    jdbc("SELECT SOME")
      .query("SELECT * FROM TEST_TABLE")
      .check(
        allResults.saveAs("RR"),
      )

}
