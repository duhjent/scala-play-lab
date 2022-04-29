package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Injecting
import play.api.test.Helpers._
import play.api.test.FakeRequest
import play.api.http.HttpVerbs
import org.scalatest.words.HaveWord
import repositories.TodoItemRepository
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import scala.concurrent.Future
import models.TodoItemEntity
import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray

class TodoControllerScpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting
    with MockitoSugar {
  private val timestamps = List(1000000, 2000000, 3000000)

  def getItems() = {
    List(
      new TodoItemEntity(
        Option.apply(BSONObjectID.fromTime(timestamps(0))),
        Option.apply(DateTime.parse("2022-02-15T01:20")),
        Option.empty,
        "Test 1",
        false
      ),
      new TodoItemEntity(
        Option.apply(BSONObjectID.fromTime(timestamps(1))),
        Option.apply(DateTime.parse("2022-02-15T01:20")),
        Option.apply(DateTime.parse("2022-02-20T01:20")),
        "Test 2",
        true
      ),
      new TodoItemEntity(
        Option.apply(BSONObjectID.fromTime(timestamps(2))),
        Option.apply(DateTime.parse("2022-02-18T01:20")),
        Option.empty,
        "Test 3",
        false
      )
    )
  }

  "TodoController GET" should {
    "return OK and json" in {
      val repo = mock[TodoItemRepository]
      when(repo.getAll()).thenReturn(Future.successful(List.empty))
      val cc = stubControllerComponents()
      val controller = new TodoController(cc, repo)

      val allItemsResp = controller.getAll()(FakeRequest(GET, "/"))

      status(allItemsResp) mustBe OK
      contentType(allItemsResp) mustBe Some("application/json")
    }

    "return correct amount of items" in {
      val repo = mock[TodoItemRepository]
      when(repo.getAll()).thenReturn(Future.successful(getItems()))
      val cc = stubControllerComponents()
      val controller = new TodoController(cc, repo)

      val allItems = controller.getAll()(FakeRequest())

      val jsonContent = contentAsJson(allItems)
      jsonContent.as[JsArray].value.length mustBe 3
      jsonContent.validate[List[TodoItemEntity]].isSuccess mustBe true
    }
  }
}
