package services

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.GoogleSearchingConfig
import org.scalatest.Matchers._
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.util.Try

class GoogleSearchingTest extends AsyncFunSuite with BeforeAndAfterAll {
  val config = GoogleSearchingConfig("https://www.googleapis.com/customsearch/v1",
    "AIzaSyANcx4iIldg0ZRrDrPbWBUgnGUl76Onau8", "004881717601273209752:o6ec1aj_gze")

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()

  val ws = StandaloneAhcWSClient()

  override def beforeAll(): Unit = {
  }

  override def afterAll(): Unit = {
    Try(ws.close())
    Try(actorMaterializer.shutdown())
    actorSystem.terminate()
  }

  test("should be able to fetch data") {
    val gs = new GoogleSearching(config, ws)
    gs.search("hi").map {
      r =>
        r.query should be ("hi")
        r.hyperLinks.length should be (10)
    }
  }
}