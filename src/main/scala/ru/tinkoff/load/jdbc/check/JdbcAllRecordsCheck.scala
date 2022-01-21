package ru.tinkoff.load.jdbc.check

import io.gatling.commons.validation._
import io.gatling.core.check.CheckResult
import io.gatling.core.session.{Expression, Session}
import ru.tinkoff.load.jdbc.JdbcCheck

import java.util.{Map => JMap}

case class JdbcAllRecordsCheck(wrapped: JdbcCheck) extends JdbcCheck {

  override def check(
      response: List[Map[String, Any]],
      session: Session,
      preparedCache: JMap[Any, Any],
  ): Validation[CheckResult] = wrapped.check(response, session, preparedCache)

  override def checkIf(condition: Expression[Boolean]): JdbcCheck = copy(
    wrapped.checkIf(condition),
  )

  override def checkIf(condition: (List[Map[String, Any]], Session) => Validation[Boolean]): JdbcCheck = copy(
    wrapped.checkIf(condition),
  )
}
