package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class SearchResult(query: String, hyperLinks: Seq[String])

object SearchResult {
  implicit lazy val format: Format[SearchResult] = ({
    (JsPath \ "query").format[String] and
    (JsPath \ "hyperLinks").format[Seq[String]]
  }) (SearchResult.apply, unlift(SearchResult.unapply))
}
