import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

import scala.util.Random

object DiningPhilosophersProblem {

  trait Forks
  case class FirstForks() extends Forks
  case class SecondForks() extends Forks

  object Philosopher {

    sealed trait StartEating
    final case class StartEatingWithFirstPairOfForks(forks: FirstForks, name: String, replyTo: ActorRef[Forks]) extends StartEating
    final case class StartEatingWithSecondPairOfForks(forks1: SecondForks, name: String, replyTo: ActorRef[Forks]) extends StartEating

    def apply(): Behavior[StartEating] = {
      Behaviors.receiveMessage {
        case StartEatingWithFirstPairOfForks(forks, name, replyTo) =>
          println(s"$name philosopher started Eating")
          Thread.sleep(3000)
          println(s"$name philosopher finished Eating and thinking")
          Thread.sleep(1000)
          replyTo ! FirstForks()
          Behaviors.same

        case StartEatingWithSecondPairOfForks(forks1, name, replyTo) =>
          println(s"$name philosopher started Eating")
          Thread.sleep(3000)
          println(s"$name philosopher finished Eating and thinking")
          Thread.sleep(1000)
          replyTo ! SecondForks()
          Behaviors.same
      }
    }
  }

  object Waiter {

    def apply(): Behavior[Forks] = {
      Behaviors.setup[Forks] { context =>
        val philosopher1: ActorRef[Philosopher.StartEating] = context.spawn(Philosopher(), "1st-philosopher")
        val philosopher2: ActorRef[Philosopher.StartEating] = context.spawn(Philosopher(), "2nd-philosopher")
        val philosopher3: ActorRef[Philosopher.StartEating] = context.spawn(Philosopher(), "3rd-philosopher")
        val philosopher4: ActorRef[Philosopher.StartEating] = context.spawn(Philosopher(), "4th-philosopher")
        val philosopher5: ActorRef[Philosopher.StartEating] = context.spawn(Philosopher(), "5th-philosopher")

        var firstForks: Option[FirstForks] = None
        var secondForks: Option[SecondForks] = None

        def nextBehavior(): Behavior[Forks] =
          (firstForks, secondForks) match {
            case (Some(f), Some(s)) =>
              val philosophers = List(philosopher1, philosopher2, philosopher3, philosopher4, philosopher5)
              val random = new Random
              val firstNext = random.nextInt(philosophers.length)
              val secondNext = if (firstNext == 3) 1 else if (firstNext == 4) 2 else {firstNext + 2}
              println("\n Waiter is giving two pair of forks...")
              Thread.sleep(2000)
              philosophers(firstNext) ! Philosopher.StartEatingWithFirstPairOfForks(FirstForks(), philosophers(firstNext).path.name, context.self.narrow[Forks])
              philosophers(secondNext) ! Philosopher.StartEatingWithSecondPairOfForks(SecondForks(), philosophers(secondNext).path.name, context.self.narrow[Forks])
              firstForks = None
              secondForks = None
              Behaviors.same
            case _ =>
              Behaviors.same
          }

        Behaviors.receiveMessage {
          case f: FirstForks =>
            firstForks = Some(f)
            nextBehavior()
          case s: SecondForks =>
            secondForks = Some(s)
            nextBehavior()
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val forks = FirstForks()
    val forks1 = SecondForks()
    val waiter = ActorSystem(Waiter(), "waiter")
    waiter ! forks
    waiter ! forks1
    Thread.sleep(100000)
    waiter.terminate()
  }
}
