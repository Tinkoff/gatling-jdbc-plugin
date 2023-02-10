package ru.tinkoff.load.javaapi;

import io.gatling.javaapi.core.internal.Expressions;
import ru.tinkoff.load.javaapi.actions.BatchInsertBaseAction;
import ru.tinkoff.load.javaapi.actions.BatchUpdateBaseAction;
import ru.tinkoff.load.javaapi.actions.DBBaseAction;
import ru.tinkoff.load.javaapi.check.simpleCheckType;
import ru.tinkoff.load.javaapi.protocol.JdbcProtocolBuilderBase;
import ru.tinkoff.load.jdbc.check.JdbcCheckSupport;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static scala.jdk.javaapi.CollectionConverters.asScala;

public final class JdbcDsl {
    private JdbcDsl(){}

    public static JdbcProtocolBuilderBase DB(){
        return new JdbcProtocolBuilderBase();
    }
    @Nonnull
    public static DBBaseAction jdbc(@Nonnull String name){
        return new DBBaseAction(ru.tinkoff.load.jdbc.Predef.jdbc(Expressions.toStringExpression(name)));
    }

    @Nonnull
    public static BatchInsertBaseAction insetInto(@Nonnull String tableName, String... columns){
        return new BatchInsertBaseAction(ru.tinkoff.load.jdbc.Predef.insertInto(Expressions.toStringExpression(tableName),
                new ru.tinkoff.load.jdbc.actions.actions.Columns(
                        asScala(
                                Arrays
                                        .stream(columns)
                                        .toList())
                                .toSeq())));
    }

    @Nonnull
    public static BatchUpdateBaseAction update(@Nonnull String tableName){
        return new BatchUpdateBaseAction(ru.tinkoff.load.jdbc.Predef.update(Expressions.toStringExpression(tableName)));
    }

    @Nonnull
    public static io.gatling.core.check.Check.Simple<scala.collection.immutable.List<scala.collection.immutable.Map<java.lang.String,java.lang.Object>>> simpleCheck(simpleCheckType checkType){
        return ru.tinkoff.load.jdbc.internal.JdbcCheck.simpleJavaCheck(checkType);
    }

    @Nonnull
    public static io.gatling.core.check.CheckBuilder.Final<JdbcCheckSupport.JdbcAllRecordCheckType,scala.collection.immutable.List<scala.collection.immutable.Map<java.lang.String,java.lang.Object>>> allResults(){
        return ru.tinkoff.load.jdbc.internal.JdbcCheck.results();
    }

}
