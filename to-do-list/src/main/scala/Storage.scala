import cats.Defer
import cats.effect.{Concurrent, Sync}
import cats.syntax.all.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer
import io.circe.*
import io.circe.generic.semiauto.*
import org.http4s.Status._

object Storage:
  private var items: ListBuffer[TodoItem] = ListBuffer(TodoItem("Work", LocalDate.of(2022, 2, 11), false))

  def list[F[_]](using Sync[F]): F[List[TodoItem]] =
    items.toList.pure

  def get[F[_]](using Sync[F])(text: String): F[Option[TodoItem]] = {
    items.find(_.text == text)
  }.pure

  def add[F[_]: Concurrent](item: TodoItem): F[Boolean] = Concurrent[F].pure{
    val existingItem = items.find(_.text == item.text)
    existingItem match {
      case Some(item) =>
        false
      case None =>
        items = item +: items
        true
    }
  }
  
  def remove[F[_]: Concurrent](item: TodoItem): F[Unit] = Concurrent[F].pure{
    items -= item
  }

  def update[F[_]: Concurrent](item: TodoItem): F[Unit] = Concurrent[F].pure{
    val remItem = items.find(_.text == item.text)
    items -= remItem.get
    items = item +: items
  }

  def sortByDate[F[_]: Sync]: F[Unit] = {
    items = items.sortBy(_.date)
  }.pure
