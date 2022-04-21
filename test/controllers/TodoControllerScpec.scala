package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Injecting
import play.api.test.Helpers._
import play.api.test.FakeRequest
import play.api.http.HttpVerbs
import org.scalatest.words.HaveWord

class TodoControllerScpec
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {
  "TodoController GET" should {
    "return items" in {
      val controller = new TodoController(stubControllerComponents())
      val allItems = controller.getAll()(FakeRequest(GET, "/"))

      status(allItems) mustBe OK
      contentType(allItems) mustBe Some("application/json")
    }
  }
}
