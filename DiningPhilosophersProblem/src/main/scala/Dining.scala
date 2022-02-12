import akka.NotUsed
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import Non_Blocking_Philosophers.*
import Forks.forks

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object Dining {

  var blocking = 0

  def apply():  Behavior[NotUsed] = Behaviors.setup { ctx =>

    val listPhilosophers = List("Philoposher 1", "Philoposher 2", "Philoposher 3", "Philoposher 4", "Philoposher 5")

    def philosophers = if(blocking == 0) non_Blocking_Philosophers() else blocking_Philosophers()

    def non_Blocking_Philosophers (): Unit = {
      val philosophers = listPhilosophers.zipWithIndex.map { case (name, i) =>
        ctx.spawn(Non_Blocking_Philosophers(name, i, ((i + 1) % forks.length)), name.replace(" ", ""))
      }

      philosophers.foreach(_ ! Non_Blocking_Philosophers.Thinks)
    }

    def blocking_Philosophers (): Unit = {
      val philosophers = listPhilosophers.zipWithIndex.map { case (name, i) =>
        ctx.spawn(Blocking_Philosophers(name, i, ((i + 1) % forks.length)), name.replace(" ", ""))
      }

      philosophers.foreach(_ ! Blocking_Philosophers.Thinks)
    }

    philosophers

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
