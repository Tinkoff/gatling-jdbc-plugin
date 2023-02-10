package ru.tinkoff.load.javaapi.actions;

import java.util.Map;
import static ru.tinkoff.load.javaapi.internal.Utils.getSeq;

public class DBInsertActionValuesStep {
    private final ru.tinkoff.load.jdbc.actions.actions.DBInsertActionValuesStep wrapped;

    public DBInsertActionValuesStep(ru.tinkoff.load.jdbc.actions.actions.DBInsertActionValuesStep wrapped){
        this.wrapped = wrapped;
    }

    public DBInsertActionBuilder values(Map<String, Object> vals){
        return new DBInsertActionBuilder(wrapped.values(
                getSeq(vals)
        ));
    }
}
