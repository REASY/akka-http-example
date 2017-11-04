package models

import play.api.libs.json.{Format, Json}

case class ServerError(title: String, detail: String)

object ServerError {
  implicit val serverErrorFormat: Format[ServerError] =
    Json.using[Json.WithDefaultValues].format[ServerError]
}