package services

import models.SearchResult

import scala.concurrent.Future

class GoogleSearching extends Searching {
  def search(query: String): Future[SearchResult] = ???
}
