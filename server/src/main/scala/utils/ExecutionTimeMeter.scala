package utils

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object ExecutionTimeMeter {
  def meter[T](what: String, body: => T, logger: String => Unit = println): T = {
    val startTime = Deadline.now
    val result = body
    val stopTime = Deadline.now
    val d = stopTime - startTime
    logger(s"'$what' executed in ${d.toMillis} ms")
    result
  }

  def meterFuture[T](what: String, future: Future[T], logger: String => Unit = println)
                    (implicit ec: ExecutionContext): Future[T] = {
    val startTime = Deadline.now
    future.map { r =>
      val stopTime = Deadline.now
      val d = stopTime - startTime
      logger(s"$what executed in ${d.toMillis} ms")
      r
    }
  }
}