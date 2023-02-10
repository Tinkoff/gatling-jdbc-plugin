package ru.tinkoff.load.jdbc

import ru.tinkoff.load.jdbc.Predef._
import ru.tinkoff.load.jdbc.protocol.JdbcProtocolBuilder

import scala.concurrent.duration._
import scala.util.Random

package object test {

  private val gen                      = Random.javaRandomToRandom(new java.util.Random(System.currentTimeMillis()))
  private val numeric                  = "1234567890"
  private val alfaNumericUpper         = s"${numeric}ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  private val lower                    = s"${numeric}abcdefghijklmnopqrstuvwxyz"
  def randomNumeric(len: Int): String  = (1 to len).foldLeft("")((a, _) => s"$a${numeric(gen.nextInt(numeric.length))}")
  def randomAlphaNum(len: Int): String =
    (1 to len).foldLeft("")((a, _) => s"$a${alfaNumericUpper(gen.nextInt(alfaNumericUpper.length))}")

  def randomAlNumLower(len: Int): String =
    (1 to len)
      .foldLeft((new StringBuilder(), '\u0000')) { case ((b, prev), _) =>
        val index = gen.nextInt(lower.length - 1)
        if (lower(index) == prev) {
          val newIndex = (index + 1) % lower.length
          (b append lower(newIndex), lower(newIndex))
        } else
          (b append lower(index), lower(index))
      }
      ._1
      .toString()

  def randomId(suffixLen: Int = 8): String = s"${gen.nextInt(4) + 2}-${randomAlphaNum(suffixLen)}"
  def serial                               = s"${randomNumeric(3)}-${randomNumeric(3)}-${randomNumeric(3)}"
  def anyOf[T](vs: T*): T                  = vs(gen.nextInt(vs.size))

  val dataBase: JdbcProtocolBuilder = DB
    .url("jdbc:h2:mem:test")
    .username("sa")
    .password("")
    .maximumPoolSize(23)
    .connectionTimeout(2.minute)

}
