package services

import models.{GoogleSearchingConfig, SearchResult}
import play.api.libs.json.{Format, Json}
import play.api.libs.ws.StandaloneWSClient

import scala.concurrent.{ExecutionContext, Future}

case class GoogleSearchingException(message: String, cause: Throwable = null)
  extends Exception(message, cause)

private case class Item(title: String, link: String)
private case class GoogleSearchResult(kind: String, items: Seq[Item])

class GoogleSearching(config: GoogleSearchingConfig, ws: StandaloneWSClient)
                     (implicit executionContext: ExecutionContext) extends Searching {

  private implicit val itemFormat: Format[Item] = Json.using[Json.WithDefaultValues].format[Item]
  private implicit val googleSearchResultFormat: Format[GoogleSearchResult] = Json.using[Json.WithDefaultValues].format[GoogleSearchResult]

  private val apiUrl= s"https://www.googleapis.com/customsearch/v1?key=${config.key}&cx=${config.cx}" +
    "&num=10&fields=kind,items(title,link)"

  def search(query: String): Future[SearchResult] = {
    val url = s"$apiUrl&q=$query"
    val request = ws.url(url)
      .withFollowRedirects(true)
      .withHttpHeaders("Content-Type" -> "application/json")
      .get
    request.map {
      r =>
        val strBody = new String(r.bodyAsBytes.toArray)
        if (r.status == 200) {
          val gsr = Json.parse(strBody).as[GoogleSearchResult]
          SearchResult(query, gsr.items.map(x => x.link))
        }
        else {
          throw GoogleSearchingException(s"Unable to fetch data from $url. Body: $strBody")
        }
    }
  }
}


