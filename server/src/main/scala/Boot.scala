import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import http.routes.SearchRoute
import models.{GoogleSearchingConfig, ServerConfig}
import play.api.libs.ws.StandaloneWSClient
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import services.{GoogleSearching, Searching}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import storage.{DbSearchStorage, InMemorySearchStorage, SearchStorage}
import utils.CustomExceptionHandling

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

object Boot extends LazyLogging {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executor: ExecutionContext = actorSystem.dispatcher

  val ws: StandaloneWSClient = StandaloneAhcWSClient()

  def cleanUp(): Unit = {
    Try(ws.close())
    Try(materializer.shutdown())
    actorSystem.terminate()
    ()
  }

  def startApplication(): Unit = {
    Try {
      val config: Config = ConfigFactory.load()

      val serverConfig: ServerConfig = ServerConfig(config.getConfig("server"))
      logger.info(s"ServerConfig: $serverConfig")

      val googleSearchingConfig = GoogleSearchingConfig(config.getConfig("google-searching-config"))
      logger.info(s"GoogleSearchingConfig: $googleSearchingConfig")

      val dbConfig: DatabaseConfig[JdbcProfile] =
        DatabaseConfig.forConfig[JdbcProfile]("database", config)

      val searching: Searching = new GoogleSearching(googleSearchingConfig, ws)
      val storage: SearchStorage = new DbSearchStorage(dbConfig)

      val searchRoute = new SearchRoute(searching, storage)

      val boostedRoute = handleExceptions(CustomExceptionHandling.handler)(searchRoute.route)

      Http().bindAndHandle(boostedRoute, serverConfig.interface, serverConfig.port)
    } match {
      case Failure(ex) =>
        cleanUp()
        logger.error(s"Failed to start application: ${ex.getMessage}", ex)
        throw ex
      case Success(future) => future.recover {
        case t =>
          cleanUp()
          logger.error(s"Failed to start application: ${t.getMessage}", t)
          throw t
      }
    }
  }

  def main(args: Array[String]): Unit = {
    startApplication()
  }
}
