package storage

import com.typesafe.config.{Config, ConfigFactory}
import models.SearchResult
import org.scalatest.AsyncWordSpec
import org.scalatest.Matchers._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import storage.DTO.{SearchQuery, SearchResult => SearchResultDTO}

import scala.concurrent.Await
import scala.concurrent.duration._

class DbSearchStorageTest extends AsyncWordSpec {

  val searchResult = SearchResult("asd",
    Seq[String]("http://asd.com", "http://asd.ru"))

  "DbSearchStorage" when {
    "add" should {
      "save it to db" in {
        val ctx = new Context("test1")
        val dbConfig = ctx.dbConfig
        import dbConfig.profile.api._

        val storage = new DbSearchStorage(dbConfig)

        Await.result(dbConfig.db.run(DBIO.seq(
          storage.Query.searchQueries.schema.create,
          storage.Query.searchResults.schema.create,
        )), 5.seconds)


        storage.add(searchResult)
          .flatMap(id => {
            storage.getAll().map { all =>
              all.length should be(1)
              all.head should be(searchResult)
            }
          })
      }
    }

    "getAll" should {
      "return saved results" in {
        val ctx = new Context("test2")
        val dbConfig = ctx.dbConfig
        import dbConfig.profile.api._

        val storage = new DbSearchStorage(dbConfig)

        Await.result(dbConfig.db.run(DBIO.seq(
          storage.Query.searchQueries.schema.create,
          storage.Query.searchResults.schema.create
        )), 5.seconds)


        val writeAction = for {
          q <- storage.Query.writeSearchQuery += SearchQuery(0, searchResult.query)
          r <- storage.Query.writeSearchResults ++= searchResult.hyperLinks.map(h => SearchResultDTO(0, q.id, h))
        } yield (q, r)

        dbConfig.db.run(writeAction.transactionally)
          .flatMap { _ =>
            storage.getAll()
              .map { all =>
                all.length should be(1)
                all.head should be(searchResult)
              }
          }
      }
    }
  }

  class Context(dbName: String) {
    val config: Config = ConfigFactory.parseString(
      """test-db {
        |  driver = "slick.driver.H2Driver$"
        |  db = {
        |    driver = "org.h2.Driver"
        |    url = "jdbc:h2:mem:###DB_NAME###"
        |  }
        |}
        |""".stripMargin.replace("###DB_NAME###", dbName))

    val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("test-db", config)
  }
}