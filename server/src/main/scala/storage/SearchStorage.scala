package storage

import models.SearchResult

import scala.concurrent.Future

trait SearchStorage {
  def add(sr: SearchResult): Future[Boolean]
  def getAll(): Future[Seq[SearchResult]]
}
