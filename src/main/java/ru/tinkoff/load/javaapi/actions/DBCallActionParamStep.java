package ru.tinkoff.load.javaapi.actions;

import ru.tinkoff.load.javaapi.internal.Utils;
import java.util.Map;

import static ru.tinkoff.load.javaapi.internal.Utils.getSeq;

public class DBCallActionParamStep {
    private final ru.tinkoff.load.jdbc.actions.actions.DBCallActionParamsStep wrapped;

    public DBCallActionParamStep(ru.tinkoff.load.jdbc.actions.actions.DBCallActionParamsStep wrapped){
        this.wrapped = wrapped;
    }

    public DBCallActionBuilder params(Map<String, Object> values){
        return new DBCallActionBuilder(wrapped.params(
                getSeq(values))
        );
    }
}
