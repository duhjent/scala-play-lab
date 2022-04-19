package controllers

import play.api.mvc.ControllerComponents
import play.api.mvc.BaseController
import javax.inject.Singleton
import javax.inject.Inject
import play.api.mvc
import models.TodoItem
import play.api.libs.json._

@Singleton
class TodoController @Inject() (val controllerComponents: ControllerComponents)
    extends BaseController {
  implicit val todoListJson = Json.format[TodoItem]

  private val todoList = List[TodoItem](
    TodoItem(1, "test", true),
    TodoItem(2, "some other value", false)
  )

  def getAll(): mvc.Action[mvc.AnyContent] = Action {
    if (todoList.isEmpty) {
      NoContent
    } else {
      Ok(Json.toJson(todoList));
    }
  }

  def getById(id: Long): mvc.Action[mvc.AnyContent] = Action {
    val item = todoList.find(_.id == id)
    item match {
      case Some(i) => Ok(Json.toJson(i))
      case None => NotFound
    }
  }
}
