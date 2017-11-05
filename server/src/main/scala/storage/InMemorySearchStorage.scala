package storage
import java.util.concurrent.ConcurrentLinkedQueue

import models.SearchResult

import scala.concurrent.Future

class InMemorySearchStorage extends SearchStorage {
  private val searchResults: ConcurrentLinkedQueue[SearchResult] =
    new ConcurrentLinkedQueue[SearchResult]()

  def add(sr: SearchResult): Future[Long] = Future.successful( {
    searchResults.add(sr)
    1
  })

  def getAll: Future[Seq[SearchResult]] = {
    Future.successful(searchResults.toArray(Array.ofDim[SearchResult](0)))
  }
}