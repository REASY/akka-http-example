package utils

import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.scalatest._
import org.scalatest.mockito.MockitoSugar

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait RouteTestEx extends WordSpec with Matchers with ScalatestRouteTest with MockitoSugar with PlayJsonSupport {

  def awaitForResult[T](futureResult: Future[T]): T =
    Await.result(futureResult, 5.seconds)

}