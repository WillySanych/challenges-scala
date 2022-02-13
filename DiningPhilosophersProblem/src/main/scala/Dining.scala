import akka.NotUsed
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, SpawnProtocol}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object Dining {

  var blocking = 0

  def apply():  Behavior[NotUsed] = Behaviors.setup { ctx =>

    val listPhilosophers = List("Philoposher 1", "Philoposher 2", "Philoposher 3", "Philoposher 4", "Philoposher 5")

    val philosophers = listPhilosophers.zipWithIndex.map { case (name, i) =>
      ctx.spawn(nonBlockingPhilosophers(name, i, ((i + 1) % 5)), name.replace(" ", ""))
    }

    philosophers.foreach(_ ! nonBlockingPhilosophers.Think)

    Behaviors.empty
  }

  def main(args: Array[String]): Unit = {

    if (args.length > 0 && args(0).toInt == 1) {
      blocking = 1
    }

    val mainActor = ActorSystem(Dining(), "Table")
    mainActor ! NotUsed
    Thread.sleep(20_000)
    mainActor ! NotUsed
    Await.result(mainActor.whenTerminated, 1.second)
  }

}
