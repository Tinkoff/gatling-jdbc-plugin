package ru.tinkoff.load.javaapi.actions;

import io.gatling.javaapi.core.ActionBuilder;

public class RawSqlActionBuilder implements ActionBuilder{
    private final ru.tinkoff.load.jdbc.actions.actions.RawSqlActionBuilder wrapped;

    public RawSqlActionBuilder(ru.tinkoff.load.jdbc.actions.actions.RawSqlActionBuilder wrapped){
        this.wrapped = wrapped;
    }

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return this.wrapped;
    }
}
