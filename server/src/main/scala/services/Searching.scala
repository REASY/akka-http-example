package services

import models.SearchResult

import scala.concurrent.Future

trait Searching {
  def search(query: String): Future[SearchResult]
}