package ru.tinkoff.load.javaapi.actions;

import io.gatling.javaapi.core.ActionBuilder;

public class DBCallActionBuilder implements ActionBuilder {
    private final ru.tinkoff.load.jdbc.actions.actions.DBCallActionBuilder wrapped;

    public DBCallActionBuilder(ru.tinkoff.load.jdbc.actions.actions.DBCallActionBuilder dbCallActionBuilder){
        this.wrapped = dbCallActionBuilder;
    }

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return this.wrapped;
    }
}
