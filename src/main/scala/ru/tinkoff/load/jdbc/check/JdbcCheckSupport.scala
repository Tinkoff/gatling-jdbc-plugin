package ru.tinkoff.load.jdbc.check

import io.gatling.commons.validation._
import io.gatling.core.check.{CheckBuilder, CheckMaterializer, Extractor, Preparer}
import io.gatling.core.session.{Expression, _}
import ru.tinkoff.load.jdbc.JdbcCheck

import scala.annotation.implicitNotFound

trait JdbcCheckSupport {
  trait JdbcAllRecordCheckType

  type AllRecordResult = List[Map[String, Any]]

  val AllRecordPreparer: Preparer[AllRecordResult, AllRecordResult] = something => something.success

  implicit val AllRecordCheckMaterializer
      : CheckMaterializer[JdbcAllRecordCheckType, JdbcCheck, AllRecordResult, AllRecordResult] =
    new CheckMaterializer[JdbcAllRecordCheckType, JdbcCheck, AllRecordResult, AllRecordResult](identity) {
      override protected def preparer: Preparer[AllRecordResult, AllRecordResult] = AllRecordPreparer
    }

  val AllRecordExtractor: Expression[Extractor[AllRecordResult, AllRecordResult]] =
    new Extractor[AllRecordResult, AllRecordResult] {
      override def name: String = "allRecords"

      override def apply(prepared: AllRecordResult): Validation[Option[AllRecordResult]] = Some(prepared).success

      override def arity: String = "find"
    }.expressionSuccess

  val AllRecordResults = new CheckBuilder[JdbcAllRecordCheckType, AllRecordResult, AllRecordResult](
    AllRecordExtractor,
    displayActualValue = true,
  )

  val allResults: CheckBuilder[JdbcAllRecordCheckType, AllRecordResult, AllRecordResult] = AllRecordResults

  val allRecordsCheck: JdbcAllRecordsCheck.type = JdbcAllRecordsCheck

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for JDBC.")
  implicit def findCheckBuilder2JdbcCheck[A, P, X](findCheckBuilder: CheckBuilder[A, P, X])(implicit
      CheckMaterializer: CheckMaterializer[A, JdbcCheck, AllRecordResult, P],
  ): JdbcCheck =
    findCheckBuilder.find.exists

  @implicitNotFound("Could not find a CheckMaterializer. This check might not be valid for JDBC.")
  implicit def checkBuilder2JdbcCheck[A, P, X](checkBuilder: CheckBuilder[A, P, X])(implicit
      materializer: CheckMaterializer[A, JdbcCheck, AllRecordResult, P],
  ): JdbcCheck =
    checkBuilder.build(materializer)
}
