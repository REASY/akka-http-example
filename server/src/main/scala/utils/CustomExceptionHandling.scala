package utils

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.typesafe.scalalogging.LazyLogging
import models.ServerError
import play.api.libs.json.Json

object CustomExceptionHandling extends LazyLogging {
  def handler: ExceptionHandler = ExceptionHandler {
    case t: Throwable =>
      extractClientIP { remoteAddress =>
        extractRequest { request =>
          logger.error(s"Exception during processing $request from $remoteAddress: ${t.getMessage}", t)
          val serverError = ServerError("InternalServerError", t.getMessage)
          complete(HttpResponse(StatusCodes.InternalServerError, entity = Json.toJson(serverError).toString))
        }
      }
  }
}