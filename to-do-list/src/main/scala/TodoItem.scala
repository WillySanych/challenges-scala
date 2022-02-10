import io.circe._
import io.circe.generic.semiauto._

import java.time.LocalDate

case class TodoItem(text: String, date: LocalDate, completed: Boolean)

object TodoItem:

  given decodeTodoItem: Decoder[TodoItem] = deriveDecoder[TodoItem]
  given encodeTodoItem: Encoder.AsObject[TodoItem] = deriveEncoder[TodoItem]