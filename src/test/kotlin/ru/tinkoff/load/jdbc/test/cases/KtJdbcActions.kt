package ru.tinkoff.load.jdbc.test.cases

import ru.tinkoff.load.javaapi.JdbcDsl.*
import ru.tinkoff.load.javaapi.actions.*
import ru.tinkoff.load.javaapi.check.simpleCheckType
import java.util.*
import java.util.Map

object KtJdbcActions {
    fun createTable(): RawSqlActionBuilder {
        return jdbc("Create table")
                .rawSql("CREATE TABLE TEST_TABLE (ID INT PRIMARY KEY, NAME VARCHAR(64), CREATED_AT DATE DEFAULT now(), flag BOOLEAN DEFAULT false);" +
                        "CREATE TABLE TT (ID UUID, NAME VARCHAR(64));")
    }

    fun createprocedure(): RawSqlActionBuilder {
        return jdbc("Procedure create")
                .rawSql("""
                     CREATE ALIAS TEST_PROCEDURE AS ${'$'}${'$'}
                                String testProcedure(String p1, Long p2) {
                                    String suf = p1 + "test";
                                    return p2.toString() + suf;
                                }
                                ${'$'}${'$'};
                """
                )
    }

    fun insertTest(): DBInsertActionBuilder {
        return jdbc("INSERT TEST")
                .insertInto("TEST_TABLE", "id", "name")
                .values(mapOf("id" to 2, "name" to "Test3"))
    }

    fun callTest(): DBCallActionBuilder {
        return jdbc("CALL PROCEDURE TEST")
                .call("TEST_PROCEDURE")
                .params(mapOf("p1" to "value1", "p2" to 24L))
    }

    fun batchTest(): BatchActionBuilder {
        return jdbc("Batch records").batch(
                insetInto("TEST_TABLE", "ID", "NAME", "FLAG")
                        .values(mapOf("ID" to 20, "NAME" to "Test 12", "FLAG" to true)),
                insetInto("TEST_TABLE", "ID", "NAME")
                        .values(mapOf("ID" to 40, "NAME" to "Test 34")),
                update("TEST_TABLE").set(mapOf("NAME" to "Test5")).where("ID = 2"),
                insetInto("TT", "ID", "NAME").values(mapOf("ID" to UUID.randomUUID(), "NAME" to "OOO342ff"))
        )
    }

    fun selectTT(): QueryActionBuilder {
        return jdbc("select tt").query("SELECT * FROM TT")
                .check(simpleCheck(simpleCheckType.NonEmpty),
                        allResults().saveAs("ttr"))
    }

    fun selectTest(): QueryActionBuilder {
        return jdbc("SELECT TEST")
                .queryP("SELECT * FROM TEST_TABLE WHERE ID = {id}")
                .params(mapOf("id" to 20))
                .check(simpleCheck(simpleCheckType.NonEmpty),
                        allResults().saveAs("R"))
    }

    fun selectAfterBatch(): QueryActionBuilder {
        return jdbc("SELECT SOME")
                .query("SELECT * FROM TEST_TABLE")
                .check(allResults().saveAs("RR"))
    }

    fun checkTestTableAfterBatch(): QueryActionBuilder {
        return jdbc("Check TEST_TABLE")
            .query(
                "SELECT * FROM TEST_TABLE WHERE EXISTS(SELECT NAME FROM TEST_TABLE WHERE ID = 2 AND NAME = 'Test5')" +
                        "AND (SELECT COUNT(ID) FROM TEST_TABLE WHERE ID IN (20, 40, 2)) = 3"
            )
            .check(simpleCheck(simpleCheckType.NonEmpty))
    }
}