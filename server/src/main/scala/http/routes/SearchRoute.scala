package http.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import services.Searching
import store.SearchStorage

import scala.concurrent.ExecutionContext

class SearchRoute(
                   searching: Searching,
                   searchStorage: SearchStorage
                 )(implicit executionContext: ExecutionContext) extends PlayJsonSupport {


  val route: Route = pathPrefix("search") {
    (pathEndOrSingleSlash & get) {
      parameters('q.as[String].?) {
        case Some(q) =>
          // QUESTION: What should we do if could not write SearchResult to storage?
          // QUESTION: Should we return result to client just after getting it from search engine
          // or only after inserting to storage ?

          // Currently implemented it in "synchronous" manner - client gets response
          // only when SearchResult has written in storage
          val future = searching.search(q)
            .flatMap(sr => searchStorage.add(sr).map(_ => sr.hyperLinks))
          complete(future)
        case None =>
          val queries = searchStorage.getAll()
            .map(searchResults => searchResults.map(x => x.query))
          complete(queries)
      }
    }
  }
}