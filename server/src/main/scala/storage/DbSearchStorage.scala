package storage

import com.typesafe.scalalogging.LazyLogging
import models.SearchResult
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import storage.DTO.{SearchQuery, SearchResult => SearchResultDTO}
import utils.ExecutionTimeMeter

import scala.concurrent.{ExecutionContext, Future}

class DbSearchStorage(val dbConfig: DatabaseConfig[JdbcProfile])
                     (implicit executionContext: ExecutionContext)
  extends SearchStorage with LazyLogging {

  import dbConfig.profile.api._

  private val db = dbConfig.db

  class SearchQueryTable(tag: Tag) extends Table[SearchQuery](tag, "query") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def query: Rep[String] = column[String]("query")

    def * = (id, query) <> (SearchQuery.tupled, SearchQuery.unapply)
  }

  class SearchResultTable(tag: Tag) extends Table[SearchResultDTO](tag, "result") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def query_id: Rep[Long] = column[Long]("query_id")

    def hyperlink: Rep[String] = column[String]("hyperlink")

    def searchQueryFk = foreignKey("FK_query_result", query_id, TableQuery[SearchQueryTable])(_.id,
      onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def * = (id, query_id, hyperlink) <> (SearchResultDTO.tupled, SearchResultDTO.unapply)
  }

  object Query {
    val searchQueries = TableQuery[SearchQueryTable]
    val searchResults = TableQuery[SearchResultTable]

    val writeSearchQuery = searchQueries returning searchQueries
      .map(_.id) into ((sq, id) => sq.copy(id))

    val writeSearchResults = searchResults returning searchResults
      .map(_.id) into ((sr, id) => sr.copy(id))

    val searchQueryWithResult = for {
      q <- searchQueries
      r <- searchResults if q.id === r.query_id
    } yield (q, r)
  }

  def add(sr: SearchResult): Future[Long] = {
    val insertAction = for {
      q <- Query.writeSearchQuery += SearchQuery(0, sr.query)
      r <- Query.writeSearchResults ++= sr.hyperLinks.map(h => SearchResultDTO(0, q.id, h))
    } yield (q, r)

    ExecutionTimeMeter.meterFuture("Add SearchResult to database", db.run(insertAction.transactionally)
      .map { x =>
        x._1.id
      }, logger = (s: String) => logger.debug(s))
  }

  def getAll(): Future[Seq[SearchResult]] = {
    ExecutionTimeMeter.meterFuture("Get all SearchResults",
      db.run(Query.searchQueryWithResult.result).map { r =>
        r.foldLeft(Map.empty[Long, SearchResult]) {
          case (map, (sq, sr)) =>
            val value = map.get(sq.id) match {
              case Some(t) => t.copy(hyperLinks = t.hyperLinks :+ sr.hyperlink)
              case None => SearchResult(sq.query, Seq[String](sr.hyperlink))
            }
            map + (sq.id -> value)
        }.values.toSeq
      }, logger = (s: String) => logger.debug(s))
  }
}