package services

import com.typesafe.scalalogging.LazyLogging
import models.{GoogleSearchingConfig, SearchResult}
import utils.ExecutionTimeMeter._
import play.api.libs.json.{Format, Json}
import play.api.libs.ws.StandaloneWSClient

import scala.concurrent.{ExecutionContext, Future}

case class GoogleSearchingException(message: String, cause: Throwable = null)
  extends Exception(message, cause)

private case class Item(title: String, link: String)

private case class GoogleSearchResult(kind: String, items: Seq[Item])

class GoogleSearching(config: GoogleSearchingConfig, ws: StandaloneWSClient)
                     (implicit executionContext: ExecutionContext)
  extends Searching with LazyLogging{

  private implicit val itemFormat: Format[Item] = Json.using[Json.WithDefaultValues].format[Item]
  private implicit val googleSearchResultFormat: Format[GoogleSearchResult] = Json.using[Json.WithDefaultValues].format[GoogleSearchResult]

  private val apiUrl = s"${config.apiUrl}?key=${config.key}&cx=${config.cx}" +
    "&num=10&fields=kind,items(title,link)"

  def search(query: String): Future[SearchResult] = {
    meterFuture(s"Search '$query'", {
      val url = s"$apiUrl&q=$query"
      val request = ws.url(url)
        .withFollowRedirects(true)
        .withHttpHeaders("Content-Type" -> "application/json")
        .get
      request.map {
        r =>
          val strBody = new String(r.bodyAsBytes.toArray)
          if (r.status == 200) {
            val gsr = Json.parse(strBody).asOpt[GoogleSearchResult]
            val links = gsr.map(_.items.map(_.link)).getOrElse(Seq.empty[String])
            SearchResult(query, links)
          }
          else {
            throw GoogleSearchingException(s"Unable to fetch data from $url. Body: $strBody")
          }
      }
    }, logger = (s: String) => logger.debug(s))
  }
}


