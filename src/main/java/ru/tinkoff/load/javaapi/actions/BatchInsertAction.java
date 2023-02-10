package ru.tinkoff.load.javaapi.actions;

import io.gatling.commons.validation.Validation;
import io.gatling.core.session.Session;
import ru.tinkoff.load.javaapi.internal.Utils;
import scala.Function1;
import java.util.Map;

public class BatchInsertAction implements BatchAction {

    public ru.tinkoff.load.jdbc.actions.actions.BatchInsertAction wrapped;

    public BatchInsertAction(ru.tinkoff.load.jdbc.actions.actions.BatchInsertAction batchInsertAction){
        this.wrapped = batchInsertAction;
    }

    public static ru.tinkoff.load.jdbc.actions.actions.BatchInsertAction toScala(
            Function1<Session, Validation<String>> tableName,
            Map<String, Object> values,
            ru.tinkoff.load.jdbc.actions.actions.Columns columns
    ){
        return new ru.tinkoff.load.jdbc.actions.actions.BatchInsertAction
                (tableName, columns, Utils.getSeq(values)
        );
    }
}
