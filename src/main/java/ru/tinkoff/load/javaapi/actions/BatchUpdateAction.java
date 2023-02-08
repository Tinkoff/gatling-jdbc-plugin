package ru.tinkoff.load.javaapi.actions;

import ru.tinkoff.load.jdbc.actions.actions;

public class BatchUpdateAction implements BatchAction {
    ru.tinkoff.load.jdbc.actions.actions.BatchUpdateAction wrapped;

    public BatchUpdateAction(actions.BatchUpdateAction batchUpdateAction){
        this.wrapped = batchUpdateAction;
    }
}
