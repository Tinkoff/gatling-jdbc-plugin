package ru.tinkoff.load.javaapi.internal;

import io.gatling.commons.validation.Validation;
import io.gatling.javaapi.core.internal.Expressions;
import scala.Function1;
import scala.Tuple2;

import java.util.Map;

import static scala.jdk.javaapi.CollectionConverters.asScala;

public final class Utils {
    public static scala.collection.immutable.Seq<Tuple2<String, Function1<io.gatling.core.session.Session, Validation<Object>>>> getSeq(Map<String, Object> values){
        return asScala(
                values
                        .entrySet()
                        .stream()
                        .map(pair ->
                                Tuple2.apply(pair.getKey(), Expressions.toExpression(pair.getValue().toString(), Object.class))
                        ).toList()
                        .stream()
                        .toList())
                .toSeq();
    }


}
