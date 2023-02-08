package ru.tinkoff.load.javaapi.actions

import ru.tinkoff.load.jdbc.actions.actions

object BatchBase {

  def toScalaBatch(batchAction: Object): actions.BatchAction = {
    batchAction match {
      case insert: ru.tinkoff.load.javaapi.actions.BatchInsertAction => insert.wrapped
      case update: ru.tinkoff.load.javaapi.actions.BatchUpdateAction => update.wrapped
      case unknown                                                   => throw new IllegalArgumentException(s"JDBC DSL doesn't support $unknown")
    }
  }
}
