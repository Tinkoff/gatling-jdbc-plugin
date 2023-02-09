package ru.tinkoff.load.javaapi.internal

import io.gatling.core.check.Check.Simple
import io.gatling.core.check.CheckBuilder.Final
import io.gatling.core.check.CheckBuilder.Final._
import io.gatling.core.check._
import ru.tinkoff.load.jdbc.Predef._
import ru.tinkoff.load.jdbc._
import io.gatling.core.Predef.find2Final

import scala.jdk.CollectionConverters.CollectionHasAsScala

object JdbcCheck {

  case class JdbcCheckTypeWrapper(value: Check[JdbcCheck]) {}

  case class SimpleWrapper(value: Simple[JdbcCheck])

  def simpleCheck(t: ru.tinkoff.load.javaapi.check.simpleCheckType): Simple[AllRecordResult] = {
    t match {
      case ru.tinkoff.load.javaapi.check.simpleCheckType.NonEmpty =>
        Predef.simpleCheck(x => x.nonEmpty).asInstanceOf[Simple[AllRecordResult]]
      case ru.tinkoff.load.javaapi.check.simpleCheckType.Empty    =>
        Predef.simpleCheck(x => x.isEmpty).asInstanceOf[Simple[AllRecordResult]]
    }
  }

  def results(): Final[JdbcAllRecordCheckType, AllRecordResult] =
    find2Final(allResults)

  private def toScalaCheck(javaCheck: Object): JdbcCheck = {
    javaCheck match {
      case simpleCheck: Simple[_]                            => simpleCheck.asInstanceOf[Simple[AllRecordResult]]
      case defaultCheck: CheckBuilder.Final.Default[_, _, _] =>
        checkBuilder2JdbcCheck(
          defaultCheck.asInstanceOf[Default[Predef.JdbcAllRecordCheckType, AllRecordResult, AllRecordResult]],
        )
      case unknown                                           => throw new IllegalArgumentException(s"JDBC DSL doesn't support $unknown")
    }
  }

  def toScalaChecks(javaChecks: java.util.List[Object]): Seq[JdbcCheck] =
    javaChecks.asScala.map(x => toScalaCheck(x)).toSeq
}
