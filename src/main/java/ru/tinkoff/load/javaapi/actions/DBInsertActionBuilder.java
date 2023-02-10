package ru.tinkoff.load.javaapi.actions;

import io.gatling.javaapi.core.ActionBuilder;

public class DBInsertActionBuilder implements ActionBuilder {
    private final ru.tinkoff.load.jdbc.actions.actions.DBInsertActionBuilder wrapped;

    public DBInsertActionBuilder(ru.tinkoff.load.jdbc.actions.actions.DBInsertActionBuilder dbInsertActionBuilder){
        this.wrapped = dbInsertActionBuilder;
    }

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return this.wrapped;
    }
}
