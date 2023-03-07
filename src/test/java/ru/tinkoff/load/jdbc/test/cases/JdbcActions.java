package ru.tinkoff.load.jdbc.test.cases;

import ru.tinkoff.load.javaapi.actions.*;
import ru.tinkoff.load.javaapi.check.simpleCheckType;

import java.util.Map;
import java.util.UUID;

import static ru.tinkoff.load.javaapi.JdbcDsl.*;

public class JdbcActions {

    public static RawSqlActionBuilder createTable(){
        return jdbc("Create table")
                .rawSql("CREATE TABLE TEST_TABLE (ID INT PRIMARY KEY, NAME VARCHAR(64), CREATED_AT DATE DEFAULT now(), flag BOOLEAN DEFAULT false);" +
                                        "CREATE TABLE TT (ID UUID, NAME VARCHAR(64));");
    }

    public static RawSqlActionBuilder createprocedure(){
        return jdbc("Procedure create")
                .rawSql(
                        """
                                CREATE ALIAS TEST_PROCEDURE AS $$
                                String testProcedure(String p1, Long p2) {
                                    String suf = p1 + "test";
                                    return p2.toString() + suf;
                                }
                                $$;
                                """
                );
    }

    public static DBInsertActionBuilder insertTest(){
        return jdbc("INSERT TEST")
                .insertInto("TEST_TABLE", "id", "name")
                .values(Map.of("id", 2, "name", "Test3"));
    }

    public static DBCallActionBuilder callTest(){
        return jdbc("CALL PROCEDURE TEST")
                .call("TEST_PROCEDURE")
                .params(Map.of("p1", "value1", "p2", 24L));
    }

    public static BatchActionBuilder batchTest(){
        return jdbc("Batch records").batch(
                insetInto("TEST_TABLE", "ID", "NAME", "FLAG")
                        .values(Map.of("ID", 20, "NAME", "Test 12", "FLAG", true)),
                insetInto("TEST_TABLE", "ID", "NAME")
                        .values(Map.of("ID", 40, "NAME", "Test 34")),
                update("TEST_TABLE").set(Map.of("NAME", "Test5")).where("ID = 2"),
                insetInto("TT", "ID", "NAME").values(Map.of("ID", UUID.randomUUID(), "NAME", "OOO342ff"))
        );
    }

    public static QueryActionBuilder selectTT(){
        return jdbc("select tt").query("SELECT * FROM TT")
                .check(simpleCheck(simpleCheckType.NonEmpty),
                        allResults().saveAs("ttr"));
    }

    public static QueryActionBuilder selectTest(){
        return jdbc("SELECT TEST")
                .queryP("SELECT * FROM TEST_TABLE WHERE ID = {id}")
                .params(Map.of("id", 20))
                .check(simpleCheck(simpleCheckType.NonEmpty),
                        allResults().saveAs("R"));
    }

    public static QueryActionBuilder selectAfterBatch(){
        return jdbc("SELECT SOME")
                .query("SELECT * FROM TEST_TABLE")
                .check(allResults().saveAs("RR"));
    }

    public static QueryActionBuilder checkTestTableAfterBatch(){
        return jdbc("Check TEST_TABLE")
                .query("SELECT * FROM TEST_TABLE WHERE EXISTS(SELECT NAME FROM TEST_TABLE WHERE ID = 2 AND NAME = 'Test5')" +
                        "AND (SELECT COUNT(ID) FROM TEST_TABLE WHERE ID IN (20, 40, 2)) = 3")
                .check(simpleCheck(simpleCheckType.NonEmpty));
    }
}
