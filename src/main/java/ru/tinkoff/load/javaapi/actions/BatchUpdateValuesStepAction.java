package ru.tinkoff.load.javaapi.actions;

import ru.tinkoff.load.jdbc.actions.actions;

import static io.gatling.javaapi.core.internal.Expressions.toStringExpression;

public class BatchUpdateValuesStepAction {
    private final ru.tinkoff.load.jdbc.actions.actions.BatchUpdateValuesStepAction wrapped;

    public BatchUpdateValuesStepAction(ru.tinkoff.load.jdbc.actions.actions.BatchUpdateValuesStepAction batchUpdateValuesStepAction) {
        this.wrapped = batchUpdateValuesStepAction;
    }

    public BatchUpdateAction where(String whereExpression){
        return new BatchUpdateAction(new actions.BatchUpdateAction(wrapped.tableName(),
                wrapped.updateValues(),
                scala.Option.apply(toStringExpression(whereExpression))));

    }
}
