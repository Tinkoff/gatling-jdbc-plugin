package ru.tinkoff.load.jdbc.db

import java.sql.{CallableStatement, Connection, PreparedStatement, Statement}
import scala.concurrent.{ExecutionContext, Future}

trait ConnectionWrapper[F[_]] {
  def createStatement: F[Statement]
  def prepareStatement(sql: String): F[PreparedStatement]
  def prepareCall(sql: String): F[CallableStatement]
  def getAutoCommit: F[Boolean]
  def setAutoCommit(autoCommit: Boolean): F[Unit]
  def commit: F[Unit]
  def close: F[Unit]
}

object ConnectionWrapper {
  private final class Impl private (c: Connection)(implicit ec: ExecutionContext) extends ConnectionWrapper[Future] {

    override def createStatement: Future[Statement] = Future(c.createStatement())

    override def prepareStatement(sql: String): Future[PreparedStatement] = Future(c.prepareStatement(sql))

    override def prepareCall(sql: String): Future[CallableStatement] = Future(c.prepareCall(sql))

    override def close: Future[Unit] = Future(c.close())

    override def getAutoCommit: Future[Boolean] = Future(c.getAutoCommit)

    override def setAutoCommit(autoCommit: Boolean): Future[Unit] = Future(c.setAutoCommit(autoCommit))

    override def commit: Future[Unit] = Future(c.commit())
  }

  object Impl {
    def apply(c: Connection, ec: ExecutionContext): ConnectionWrapper[Future] = new Impl(c)(ec)
  }
}
