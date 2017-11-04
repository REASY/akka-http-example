package http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import http.routes.SearchRoute
import services.Searching
import store.SearchStorage

import scala.concurrent.ExecutionContext

class HttpRoute(
                 searching: Searching,
                 searchStorage: SearchStorage
               )(implicit executionContext: ExecutionContext) {
  // FIXME
  val searchRoute = new SearchRoute(null, null)

  val route: Route =
    searchRoute.route ~
      pathPrefix("healthcheck") {
        get {
          complete("OK")
        }
      }
}