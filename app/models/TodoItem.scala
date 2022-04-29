package models

import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID
import play.api.libs.json.Json
import play.api.libs.json.Format
import reactivemongo.play.json._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDateTime
import reactivemongo.bson.BSONDocumentWriter

case class UpdateTodoItemRequest(id: String, description: String, isDone: Boolean)

case class CreateTodoItemRequest(description: String)

case class TodoItemEntity(
    _id: Option[BSONObjectID],
    _creationDate: Option[DateTime],
    _updateDate: Option[DateTime],
    description: String,
    isDone: Boolean
)

object TodoItemEntity {
  implicit val fmt: Format[TodoItemEntity] = Json.format[TodoItemEntity]
  implicit object TodoItemEntityBsonReader
      extends BSONDocumentReader[TodoItemEntity] {
    def read(doc: BSONDocument): TodoItemEntity = {
      TodoItemEntity(
        doc.getAs[BSONObjectID]("_id"),
        doc
          .getAs[BSONDateTime]("_creationDate")
          .map(dt => new DateTime(dt.value)),
        doc
          .getAs[BSONDateTime]("_updateDate")
          .map(dt => new DateTime(dt.value)),
        doc.getAs[String]("description").get,
        doc.getAs[Boolean]("isDone").get
      )
    }
  }

  implicit object TodoItemEntityBsonWriter
      extends BSONDocumentWriter[TodoItemEntity] {
    def write(ety: TodoItemEntity): BSONDocument = {
      BSONDocument(
        "_id" -> ety._id,
        "_creationDate" -> ety._creationDate.map(x =>
          BSONDateTime(x.getMillis)
        ),
        "_updateDate" -> ety._updateDate.map(x => BSONDateTime(x.getMillis)),
        "description" -> ety.description,
        "isDone" -> ety.isDone
      )
    }
  }
}
