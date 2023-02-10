package ru.tinkoff.load.javaapi.actions;

import java.util.Map;
import static ru.tinkoff.load.javaapi.internal.Utils.getSeq;

public class BatchUpdateBaseAction implements BatchAction {
    private final ru.tinkoff.load.jdbc.actions.actions.BatchUpdateBaseAction wrapped;
    public BatchUpdateBaseAction(ru.tinkoff.load.jdbc.actions.actions.BatchUpdateBaseAction batchUpdateBaseAction) {
        this.wrapped = batchUpdateBaseAction;
    }

    public BatchUpdateValuesStepAction set(Map<String, Object> values){
        return new BatchUpdateValuesStepAction(new ru.tinkoff.load.jdbc.actions.actions.BatchUpdateValuesStepAction(wrapped.tableName(),
                getSeq(values)));
    }
}
