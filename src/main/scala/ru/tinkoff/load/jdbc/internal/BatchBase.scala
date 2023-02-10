package ru.tinkoff.load.jdbc.internal

import ru.tinkoff.load.jdbc.actions.actions.BatchAction

object BatchBase {

  def toScalaBatch(batchAction: Object): BatchAction = {
    batchAction match {
      case insert: ru.tinkoff.load.javaapi.actions.BatchInsertAction => insert.wrapped
      case update: ru.tinkoff.load.javaapi.actions.BatchUpdateAction => update.wrapped
      case unknown                                                   => throw new IllegalArgumentException(s"JDBC DSL doesn't support $unknown")
    }
  }
}
