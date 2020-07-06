package ru.tinkoff.load.jdbc.db

import com.zaxxer.hikari.HikariDataSource

import scala.util.Try

case class ConnectedDB(pool: HikariDataSource) {

  def executeUpdate(implicit exec: ManagedConnection => Try[Int]): Try[Int] =
    for {
      c        <- Try(pool.getConnection)
      inserted <- exec(ManagedConnection(c))
      _        <- Try(c.close())
    } yield inserted

  def executeRaw(implicit exec: ManagedConnection => Try[Unit]): Try[Unit] =
    for {
      c        <- Try(pool.getConnection)
      inserted <- exec(ManagedConnection(c))
      _        <- Try(c.close())
    } yield inserted

  def executeQuery(implicit exec: ManagedConnection => Try[List[Map[String, Any]]]): Try[List[Map[String, Any]]] =
    for {
      c      <- Try(pool.getConnection)
      result <- exec(ManagedConnection(c))
      _      <- Try(c.close())
    } yield result
}
