package ru.tinkoff.load.javaapi.actions;

import io.gatling.javaapi.core.ActionBuilder;
public class BatchActionBuilder implements ActionBuilder{
    private final ru.tinkoff.load.jdbc.actions.actions.BatchActionBuilder wrapped;

    public BatchActionBuilder(ru.tinkoff.load.jdbc.actions.actions.BatchActionBuilder batchActionBuilder){
        this.wrapped = batchActionBuilder;
    }

    @Override
    public io.gatling.core.action.builder.ActionBuilder asScala() {
        return this.wrapped;
    }
}
