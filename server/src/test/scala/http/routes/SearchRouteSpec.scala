package http.routes

import akka.http.scaladsl.server.Route
import models.SearchResult
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import services.Searching
import storage.SearchStorage
import utils.RouteTestEx

import scala.concurrent.Future

class SearchRouteSpec extends RouteTestEx {

  "SearchRoute" when {
    "GET /search?q=hi" should {
      "return 200 and first 10 found search results" in new Context {
        when(searching.search(anyString))
          .thenReturn(Future.successful(searchResult1))

        when(storage.add(any[SearchResult]))
          .thenReturn(Future.successful(true))

        Get("/search?q=hi") ~> searchRoute ~> check {
          status.intValue() shouldBe 200
          responseAs[Seq[String]] shouldBe searchResult1.hyperLinks
        }

        verify(searching, times(1)).search(anyString)
        verify(storage, times(1)).add(any[SearchResult])
      }
    }

    "GET /search" should {
      "return 200 and history of queries" in new Context {
        when(storage.getAll())
          .thenReturn(Future.successful(Seq[SearchResult](searchResult1, searchResult2)))

        Get("/search") ~> searchRoute ~> check {
          status.intValue() shouldBe 200
          responseAs[Seq[String]] shouldBe Seq[String](searchResult1.query, searchResult2.query)
        }

        verify(storage, times(1)).getAll()
      }
    }
  }

  trait Context {
    val searching: Searching = mock[Searching]
    val storage: SearchStorage = mock[SearchStorage]
    val searchRoute: Route = new SearchRoute(searching, storage).route

    val searchResult1: SearchResult =
      SearchResult("hi",
        Seq("www.dictionary.com/browse/hi", "www.thesaurus.com/browse/hi",
          "https://www.merriam-webster.com/dictionary/hi",
          "https://en.oxforddictionaries.com/definition/hi",
          "https://dictionary.cambridge.org/dictionary/english/hi",
          "https://en.wiktionary.org/wiki/hi",
          "https://www.urbandictionary.com/define.php?term=hi",
          "https://www.thefreedictionary.com/hi",
          "https://www.youtube.com/watch?v=_c1NJQ0UP_Q",
          "https://hi.service-now.com/"
        ))

    val searchResult2: SearchResult =
      SearchResult("bye",
        Seq("https://www.merriam-webster.com/dictionary/bye",
          "https://en.oxforddictionaries.com/definition/bye",
          "https://dict.longdo.com/search/bye",
          "https://dictionary.cambridge.org/dictionary/english/bye",
          "https://www.urbandictionary.com/define.php?term=Bye",
          "www.dictionary.com/browse/bye",
          "www.learnersdictionary.com/definition/bye",
          "https://www.collinsdictionary.com/dictionary/english/bye",
          "https://en.wiktionary.org/wiki/bye",
          "https://www.vocabulary.com/dictionary/bye"
        ))
  }
}
