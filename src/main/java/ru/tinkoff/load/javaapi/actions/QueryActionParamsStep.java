package ru.tinkoff.load.javaapi.actions;

import ru.tinkoff.load.javaapi.internal.Utils;

import java.util.Map;

public class QueryActionParamsStep {
    private final ru.tinkoff.load.jdbc.actions.actions.QueryActionParamsStep wrapped;

    public QueryActionParamsStep(ru.tinkoff.load.jdbc.actions.actions.QueryActionParamsStep wrapped){
        this.wrapped = wrapped;
    }

    public QueryActionBuilder params(Map<String, Object> values){
        return new QueryActionBuilder  (wrapped.params(
                Utils.getSeq(values)
        ));
    }
}
