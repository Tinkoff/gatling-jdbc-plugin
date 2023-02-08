package ru.tinkoff.load.javaapi.actions;

import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import ru.tinkoff.load.javaapi.internal.Utils;
import ru.tinkoff.load.jdbc.actions.actions;
import scala.Function1;

import java.util.Map;

public class BatchInsertAction implements BatchAction {

    ru.tinkoff.load.jdbc.actions.actions.BatchInsertAction wrapped;

    public BatchInsertAction(actions.BatchInsertAction batchInsertAction){
        this.wrapped = batchInsertAction;
    }

    public static actions.BatchInsertAction toScala(
            Function1<Session, Validation<String>> tableName,
            Map<String, Object> values,
            actions.Columns columns
    ){
        return new actions.BatchInsertAction
                (tableName, columns, Utils.getSeq(values)
        );
    }
}
