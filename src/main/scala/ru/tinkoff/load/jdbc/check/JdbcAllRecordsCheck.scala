package ru.tinkoff.load.jdbc.check

import java.util.{Map => JMap}

import io.gatling.commons.validation._
import io.gatling.core.check.CheckResult
import io.gatling.core.session.Session
import ru.tinkoff.load.jdbc.JdbcCheck

object JdbcAllRecordsCheck {
  private val JdbcAnyCheckFailure = "Jdbc check failed".failure
}

case class JdbcAllRecordsCheck(pred: List[Map[String, Any]] => Boolean) extends JdbcCheck {
  override def check(response: List[Map[String, Any]],
                     session: Session,
                     preparedCache: JMap[Any, Any]): Validation[CheckResult] =
    if (pred(response)) {
      CheckResult.NoopCheckResultSuccess
    } else {
      JdbcAllRecordsCheck.JdbcAnyCheckFailure
    }
}
