package ru.tinkoff.load.jdbc.test.cases

import ru.tinkoff.load.javaapi.check.JdbcCheck
import ru.tinkoff.load.javaapi.check.simpleCheckType

object JdbcActions {
    fun createTable(): ru.tinkoff.load.javaapi.actions.RawSqlActionBuilder {
        return ru.tinkoff.load.javaapi.JdbcDsl.jdbc("Create table")
                .rawSql("CREATE TABLE TEST_TABLE (ID INT PRIMARY KEY, NAME VARCHAR(64), CREATED_AT DATE DEFAULT now(), flag BOOLEAN DEFAULT false);" +
                        "CREATE TABLE TT (ID UUID, NAME VARCHAR(64));")
    }

    fun createprocedure(): ru.tinkoff.load.javaapi.actions.RawSqlActionBuilder {
        return ru.tinkoff.load.javaapi.JdbcDsl.jdbc("Procedure create")
                .rawSql("""
                    CREATE OR REPLACE PROCEDURE TEST_PROCEDURE(p1 varchar(10), p2 integer) 
                    LANGUAGE SQL 
                    AS $$ 
                        select 1 as result;
                    $$;
                """
                )
    }

    fun insertTest(): ru.tinkoff.load.javaapi.actions.DBInsertActionBuilder {
        return ru.tinkoff.load.javaapi.JdbcDsl.jdbc("INSERT TEST")
                .insertInto("TEST_TABLE", "id", "name")
                .values(mapOf("id" to 2, "name" to "Test3"))
    }

    fun callTest(): ru.tinkoff.load.javaapi.actions.DBCallActionBuilder {
        return ru.tinkoff.load.javaapi.JdbcDsl.jdbc("CALL PROCEDURE TEST")
                .call("TEST_PROCEDURE")
                .params(mapOf("p1" to "value1", "p2" to 24L))
    }

    fun batchTest(): ru.tinkoff.load.javaapi.actions.BatchActionBuilder {
        return ru.tinkoff.load.javaapi.JdbcDsl.jdbc("Batch records").batch(
                ru.tinkoff.load.javaapi.JdbcDsl.insetInto("TEST_TABLE", "ID", "NAME", "FLAG")
                        .values(mapOf("ID" to 20, "NAME" to "Test 12", "FLAG" to true)),
                ru.tinkoff.load.javaapi.JdbcDsl.insetInto("TEST_TABLE", "ID", "NAME")
                        .values(mapOf("ID" to 40, "NAME" to "Test 34")),
                ru.tinkoff.load.javaapi.JdbcDsl.update("TEST_TABLE").set(mapOf("NAME" to "Test5")).where("ID = 2")
        )
    }

    fun selectTT(): ru.tinkoff.load.javaapi.actions.QueryActionBuilder {
        return ru.tinkoff.load.javaapi.JdbcDsl.jdbc("select tt").query("SELECT * FROM TT")
                .check(JdbcCheck.simpleCheck(simpleCheckType.NonEmpty),
                        JdbcCheck.results().saveAs("ttr"))
    }

    fun selectTest(): ru.tinkoff.load.javaapi.actions.QueryActionBuilder {
        return ru.tinkoff.load.javaapi.JdbcDsl.jdbc("SELECT TEST")
                .queryP("SELECT * FROM TEST_TABLE WHERE ID = {id}")
                .params(mapOf("id" to 1))
                .check(JdbcCheck.simpleCheck(simpleCheckType.NonEmpty),
                        JdbcCheck.results().saveAs("R"))
    }

    fun selectAfterBatch(): ru.tinkoff.load.javaapi.actions.QueryActionBuilder {
        return ru.tinkoff.load.javaapi.JdbcDsl.jdbc("SELECT SOME")
                .query("SELECT * FROM TEST_TABLE")
                .check(JdbcCheck.results().saveAs("RR"))
    }
}