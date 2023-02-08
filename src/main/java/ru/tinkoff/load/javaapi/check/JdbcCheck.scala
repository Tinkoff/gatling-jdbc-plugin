package ru.tinkoff.load.javaapi.check

import io.gatling.core.check.Check.Simple
import io.gatling.core.check.{Check, CheckBuilder}
import ru.tinkoff.load.jdbc.Predef.{AllRecordCheckMaterializer, AllRecordResult, checkBuilder2JdbcCheck}
import ru.tinkoff.load.jdbc.{JdbcCheck, Predef}

import scala.jdk.CollectionConverters._

object JdbcCheck {

  case class JdbcCheckTypeWrapper(value: Check[JdbcCheck]) {}

  case class SimpleWrapper(value: Check.Simple[JdbcCheck])

  def simpleCheck(t: simpleCheckType): Simple[AllRecordResult] = {
    t match {
      case simpleCheckType.NonEmpty => Predef.simpleCheck(x => x.nonEmpty).asInstanceOf[Simple[AllRecordResult]]
      case simpleCheckType.Empty    => Predef.simpleCheck(x => x.isEmpty).asInstanceOf[Simple[AllRecordResult]]
    }
  }

  def results(): CheckBuilder.Final[Predef.JdbcAllRecordCheckType, Predef.AllRecordResult] =
    io.gatling.core.Predef.find2Final(Predef.allResults)

  private def toScalaCheck(javaCheck: Object): JdbcCheck = {
    javaCheck match {
      case simpleCheck: Simple[_]                            => simpleCheck.asInstanceOf[Simple[AllRecordResult]]
      case defaultCheck: CheckBuilder.Final.Default[_, _, _] =>
        checkBuilder2JdbcCheck(
          defaultCheck.asInstanceOf[CheckBuilder.Final.Default[Predef.JdbcAllRecordCheckType, AllRecordResult, AllRecordResult]],
        )
      case unknown                                           => throw new IllegalArgumentException(s"JDBC DSL doesn't support $unknown")
    }
  }

  def toScalaChecks(javaChecks: java.util.List[Object]): Seq[JdbcCheck] =
    javaChecks.asScala.map(x => toScalaCheck(x)).toSeq

}
