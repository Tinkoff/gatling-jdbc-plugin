package ru.tinkoff.load.javaapi.actions;

import io.gatling.javaapi.core.ActionBuilder;
import java.util.Arrays;
import java.util.List;

public class QueryActionBuilder implements ActionBuilder {
    private ru.tinkoff.load.jdbc.actions.actions.QueryActionBuilder wrapped;

    public QueryActionBuilder(ru.tinkoff.load.jdbc.actions.actions.QueryActionBuilder wrapped){
        this.wrapped = wrapped;
    }

    public QueryActionBuilder check(Object...checks){
        return check(Arrays.asList(checks));
    }

    public QueryActionBuilder check(List<Object> checks) {
        this.wrapped = wrapped.check(ru.tinkoff.load.jdbc.internal.JdbcCheck.toScalaChecks(checks));
        return this;
    }

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return this.wrapped;
    }
}
