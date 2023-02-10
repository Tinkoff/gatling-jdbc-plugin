package ru.tinkoff.load.javaapi.actions;

import java.util.Map;

public class BatchInsertBaseAction implements BatchAction {
    private final ru.tinkoff.load.jdbc.actions.actions.BatchInsertBaseAction wrapped;

    public BatchInsertBaseAction(ru.tinkoff.load.jdbc.actions.actions.BatchInsertBaseAction batchInsertBaseAction){
        this.wrapped = batchInsertBaseAction;
    }

    public BatchInsertAction values(Map<String, Object> ps){
        return new BatchInsertAction(BatchInsertAction.toScala(wrapped.tableName(), ps, wrapped.columns()));
    }

}
