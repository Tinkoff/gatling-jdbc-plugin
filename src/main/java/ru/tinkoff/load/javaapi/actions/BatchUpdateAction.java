package ru.tinkoff.load.javaapi.actions;

public class BatchUpdateAction implements BatchAction {
    public ru.tinkoff.load.jdbc.actions.actions.BatchUpdateAction wrapped;
    public BatchUpdateAction(ru.tinkoff.load.jdbc.actions.actions.BatchUpdateAction batchUpdateAction){
        this.wrapped = batchUpdateAction;
    }
}
