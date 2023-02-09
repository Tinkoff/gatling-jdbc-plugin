package ru.tinkoff.load.javaapi.internal;

import io.gatling.javaapi.core.internal.Expressions;

import java.util.Map;

import static scala.jdk.javaapi.CollectionConverters.asScala;

public final class Utils {
    public static scala.collection.immutable.Seq<scala.Tuple2<String, scala.Function1<io.gatling.core.session.Session, io.gatling.commons.validation.Validation<Object>>>> getSeq(Map<String, Object> values){
        return asScala(
                values
                        .entrySet()
                        .stream()
                        .map(pair ->
                                scala.Tuple2.apply(pair.getKey(), Expressions.toExpression(pair.getValue().toString(), Object.class))
                        ).toList()
                        .stream()
                        .toList())
                .toSeq();
    }
}
