import cats.Defer
import cats.effect.{Concurrent, Sync}
import cats.syntax.all.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer

object Storage:
  private var items: ListBuffer[TodoItem] = ListBuffer(TodoItem("Work", "2022-02-03", "done"))

  def list[F[_]](using Sync[F]): F[ListBuffer[TodoItem]] =
    items.pure

  def get[F[_]](using Sync[F])(text: String): F[Option[TodoItem]] = {
    items.find(_.text == text)
  }.pure

  def prepend[F[_]: Concurrent](item: TodoItem): F[Unit] = Concurrent[F].pure{
    items = item +: items
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
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    items = items.sortBy {
      case TodoItem(_, date, _) => LocalDate.parse(date, formatter)
    }
  }.pure
