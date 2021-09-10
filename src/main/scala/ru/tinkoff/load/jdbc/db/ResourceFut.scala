package ru.tinkoff.load.jdbc.db

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ResourceFut[R] {
  def use[U](f: R => Future[U]): Future[U]
}

object ResourceFut {
  def make[R](acquire: Future[R])(release: R => Future[Unit])(implicit ec: ExecutionContext): ResourceFut[R] =
    new ResourceFut[R] {
      override def use[U](f: R => Future[U]): Future[U] =
        for {
          res <- acquire
          result <- f(res).transformWith {
                     case Success(value)     => release(res).map(_ => value)
                     case Failure(exception) => release(res).flatMap(_ => Future.failed[U](exception))
                   }

        } yield result
    }

  def pure[A](r: => A)(implicit ec: ExecutionContext): ResourceFut[A] =
    ResourceFut.make(Future.successful(r))(_ => Future.successful(()))

  def liftFuture[A](fa: Future[A])(implicit ec: ExecutionContext): ResourceFut[A] = new ResourceFut[A] {
    override def use[U](f: A => Future[U]): Future[U] = fa.flatMap(f)
  }

  private def flatMap[A, B](r: ResourceFut[A])(mapping: A => ResourceFut[B]): ResourceFut[B] = new ResourceFut[B] {
    override def use[U](f: B => Future[U]): Future[U] = r.use(mapping(_).use(f))
  }

  private def map[A, B](r: ResourceFut[A])(mapping: A => B): ResourceFut[B] =
    new ResourceFut[B] {
      override def use[U](f: B => Future[U]): Future[U] = r.use(a => f(mapping(a)))
    }

  implicit class ResourceFutOps[A](val resA: ResourceFut[A]) extends AnyVal {
    def flatMap[B](mapping: A => ResourceFut[B]): ResourceFut[B] = ResourceFut.flatMap(resA)(mapping)
    def map[B](mapping: A => B): ResourceFut[B]                  = ResourceFut.map(resA)(mapping)
  }
}
