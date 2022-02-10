import cats.effect.{IO, IOApp, ExitCode}
import cats.effect.syntax._
import org.http4s.blaze.server.BlazeServerBuilder
import cats.implicits._
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.global

object TodoServer extends IOApp with TodoListRoutes[IO] with Config:

  val app = (
    todoListRoutes <+>
    todoListModifyRoutes
    ).orNotFound

  val server = BlazeServerBuilder[IO](global)
    .bindHttp(port)
    .withHttpApp(app)

  val serverResource = server.resource

  def run(args: List[String]): IO[ExitCode] =
    server
      .serve
      .compile.drain
      .as(ExitCode.Success)