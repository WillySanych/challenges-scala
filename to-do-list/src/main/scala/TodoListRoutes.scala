import cats.{Applicative, Monad, MonadThrow}
import cats.effect.{Concurrent, IO, Spawn, Sync}
import org.http4s.circe.*
import org.http4s.{EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import cats.syntax.all.*
import io.circe.Encoder
import org.http4s.circe.jsonDecoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder

trait TodoListRoutes[F[_]]:
  val dsl = Http4sDsl[F]
  import dsl._

  def todoListRoutes(using Sync[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {

      case GET -> Root / "items" =>
        for
          items <- Storage.list[F]
          resp <- Ok(items)
        yield
          resp

      case GET -> Root / "item" / name =>
        for
          item <- Storage.get(name)
          resp <- Ok(item)
        yield
          resp
          
      case GET -> Root / "sorted-items" =>
        for
          _ <- Storage.sortByDate
          items <- Storage.list[F]
          resp <- Ok(items)
        yield 
          resp
    }

  def todoListModifyRoutes(using Concurrent[F]): HttpRoutes[F] =
    HttpRoutes.of[F]{

      case req @ DELETE -> Root / "item" =>
        for
          item <- req.as[TodoItem]
          _ <- Storage.remove(item)
          resp <- Ok(item)
        yield
          resp

      case req @ POST -> Root / "item" =>
        for
          item <- req.as[TodoItem]
          isItem <- Storage.add(item)
          resp <- isItem match {
            case true =>
              Ok(item)
            case false =>
              Ok("Already exists")
          }
        yield
          resp

      case req @ PUT -> Root / "item" =>
        for
          item <- req.as[TodoItem]
          _ <- Storage.update(item)
          resp <- Ok(item)
        yield
          resp
    }

