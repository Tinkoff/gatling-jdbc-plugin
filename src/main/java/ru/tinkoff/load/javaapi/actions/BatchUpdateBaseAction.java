package ru.tinkoff.load.javaapi.actions;

import ru.tinkoff.load.javaapi.internal.Utils;
import ru.tinkoff.load.jdbc.actions.actions;

import java.util.Map;

public class BatchUpdateBaseAction implements BatchAction {
    private final ru.tinkoff.load.jdbc.actions.actions.BatchUpdateBaseAction wrapped;
    public BatchUpdateBaseAction(ru.tinkoff.load.jdbc.actions.actions.BatchUpdateBaseAction batchUpdateBaseAction) {
        this.wrapped = batchUpdateBaseAction;
    }

    public BatchUpdateValuesStepAction set(Map<String, Object> values){
        return new BatchUpdateValuesStepAction(new actions.BatchUpdateValuesStepAction(wrapped.tableName(),
                Utils.getSeq(values)));
    }
}
