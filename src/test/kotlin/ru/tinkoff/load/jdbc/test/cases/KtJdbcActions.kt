package ru.tinkoff.load.jdbc.test.cases

import ru.tinkoff.load.javaapi.JdbcDsl.*
import ru.tinkoff.load.javaapi.internal.JdbcCheck
import ru.tinkoff.load.javaapi.check.simpleCheckType.*
import ru.tinkoff.load.javaapi.actions.*
import ru.tinkoff.load.javaapi.check.simpleCheckType

object KtJdbcActions {
    fun createTable(): RawSqlActionBuilder {
        return jdbc("Create table")
                .rawSql("CREATE TABLE TEST_TABLE (ID INT PRIMARY KEY, NAME VARCHAR(64), CREATED_AT DATE DEFAULT now(), flag BOOLEAN DEFAULT false);" +
                        "CREATE TABLE TT (ID UUID, NAME VARCHAR(64));")
    }

    fun createprocedure(): RawSqlActionBuilder {
        return jdbc("Procedure create")
                .rawSql("""
                    CREATE OR REPLACE PROCEDURE TEST_PROCEDURE(p1 varchar(10), p2 integer) 
                    LANGUAGE SQL 
                    AS $$ 
                        select 1 as result;
                    $$;
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
                update("TEST_TABLE").set(mapOf("NAME" to "Test5")).where("ID = 2")
        )
    }

    fun selectTT(): QueryActionBuilder {
        return jdbc("select tt").query("SELECT * FROM TT")
                .check(JdbcCheck.simpleCheck(simpleCheckType.NonEmpty),
                        JdbcCheck.results().saveAs("ttr"))
    }

    fun selectTest(): QueryActionBuilder {
        return jdbc("SELECT TEST")
                .queryP("SELECT * FROM TEST_TABLE WHERE ID = {id}")
                .params(mapOf("id" to 1))
                .check(JdbcCheck.simpleCheck(simpleCheckType.NonEmpty),
                        JdbcCheck.results().saveAs("R"))
    }

    fun selectAfterBatch(): QueryActionBuilder {
        return jdbc("SELECT SOME")
                .query("SELECT * FROM TEST_TABLE")
                .check(JdbcCheck.results().saveAs("RR"))
    }
}