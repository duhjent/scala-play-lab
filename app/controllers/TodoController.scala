package controllers

import models.CreateTodoItemRequest
import models.TodoItem
import models.TodoItemEntity
import play.api.mvc
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import reactivemongo.api.AsyncDriver
import reactivemongo.api.MongoConnection
import reactivemongo.play.json._
import repositories.TodoItemRepository

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import scala.util.Failure
import scala.concurrent.Future
import scala.util.Success

@Singleton
class TodoController @Inject() (implicit
    executionContext: ExecutionContext,
    val controllerComponents: ControllerComponents,
    val todoItemRepo: TodoItemRepository
) extends BaseController {
  implicit val todoListJson = Json.format[TodoItem]
  implicit val createTodoJson = Json.format[CreateTodoItemRequest]

  private var todoList = List[TodoItem](
    TodoItem(1, "test", true),
    TodoItem(2, "some other value", false)
  )

  def getAll(): mvc.Action[mvc.AnyContent] = Action.async {
    todoItemRepo.getAll().map { tis =>
      Ok(Json.toJson(tis))
    }
  }

  def getById(id: String): mvc.Action[mvc.AnyContent] = Action.async {
    val idParseResult = BSONObjectID.parse(id)
    idParseResult match {
      case Failure(exception) =>
        Future.successful(BadRequest("Unable to parse id"))
      case Success(parsedId) =>
        todoItemRepo.getById(parsedId).map { ety =>
          ety match {
            case None        => NotFound
            case Some(value) => Ok(Json.toJson(value))
          }
        }
    }
  }

  def createNew(): mvc.Action[mvc.AnyContent] = Action.async {
    request: mvc.Request[mvc.AnyContent] =>
      val content = request.body
      val jsonObject = content.asJson
      val item: Option[CreateTodoItemRequest] =
        jsonObject.flatMap(Json.fromJson[CreateTodoItemRequest](_).asOpt)

      item match {
        case Some(value) =>
          val etyToAdd = new TodoItemEntity(
            Option.apply(BSONObjectID.generate()),
            Option.empty,
            Option.empty,
            value.description,
            false
          )
          todoItemRepo.insert(etyToAdd).map { _ =>
            Created(Json.toJson(etyToAdd))
          }
        case None =>
          Future.successful(BadRequest("Unable to parse"))
      }
  }

  def delete(id: String): mvc.Action[mvc.AnyContent] = Action.async {
    val idParseResult = BSONObjectID.parse(id)
    idParseResult match {
      case Failure(exception) =>
        Future.successful(BadRequest("Unable to parse id"))
      case Success(parsedId) =>
        todoItemRepo.delete(parsedId).map { _ =>
          NoContent
        }
    }
  }

}
