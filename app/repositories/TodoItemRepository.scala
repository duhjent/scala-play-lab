package repositories

import models.TodoItemEntity
import org.joda.time.DateTime
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.api.MongoConnection
import reactivemongo.api.ReadPreference
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.bson.compat._
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONObjectID

import javax.inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@inject.Singleton
class TodoItemRepository @inject.Inject() (implicit
    executionContext: ExecutionContext,
    reactiveMongoApi: ReactiveMongoApi
) {
  def collection: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection("todoItems"))

  def getAll(limit: Int = 100): Future[Seq[TodoItemEntity]] = {
    collection.flatMap(
      _.find(BSONDocument(), Option.empty[TodoItemEntity])
        .cursor[TodoItemEntity](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[TodoItemEntity]]())
    )
  }

  def getById(id: BSONObjectID): Future[Option[TodoItemEntity]] = {
    collection.flatMap(
      _.find(BSONDocument("_id" -> id), Option.empty[TodoItemEntity])
        .one[TodoItemEntity]
    )
  }

  def insert(ety: TodoItemEntity): Future[WriteResult] = {
    collection.flatMap(
      _.insert(ordered = false)
        .one(ety.copy(_creationDate = Some(new DateTime())))
    )
  }

  def update(ety: TodoItemEntity): Future[WriteResult] = {
    collection.flatMap(
      _.update(ordered = false).one(
        BSONDocument("_id" -> ety._id),
        ety.copy(_updateDate = Some(new DateTime()))
      )
    )
  }

  def delete(id: BSONObjectID): Future[WriteResult] = {
    collection.flatMap(_.delete().one(BSONDocument("_id" -> id), Some(1)))
  }
}
