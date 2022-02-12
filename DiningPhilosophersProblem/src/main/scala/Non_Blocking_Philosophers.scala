import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import Non_Blocking_Philosophers._
import Forks.forks

class Non_Blocking_Philosophers(ctx: ActorContext[Actions],
                                name: String,
                                leftFork: Int,
                                rightFork: Int) {
  
  private def thinks(): Behavior[Actions] = Behaviors.receiveMessage {
    case Thinks =>
      thinking()
      ctx.self ! TakeLeftFork
      takeLeftFork()
    case _ => Behaviors.same
  }

  private def takeLeftFork(): Behavior[Actions] = Behaviors.receiveMessage {
    case TakeLeftFork =>
      val res = takingLeftFork()
      if (res) {
        ctx.self ! TakeRightFork
        takeRightFork()
      } else {
        ctx.self ! Thinks
        thinks()
      }
    case _ => Behaviors.same
  }

  private def takeRightFork(): Behavior[Actions] = Behaviors.receiveMessage {
    case TakeRightFork =>
      val res = takingRightFork()
      if (res) {
        ctx.self ! Eat
        eat()
      } else {
        puttingLeftFork()
        ctx.self ! Thinks
        thinks()
      }
    case _ => Behaviors.same
  }

  private def eat(): Behavior[Actions] = Behaviors.receiveMessage {
    case Eat =>
      eating()
      ctx.self ! PutLeftFork
      putLeftFork()
    case _ => Behaviors.same
  }

  private def putLeftFork(): Behavior[Actions] = Behaviors.receiveMessage {
    case PutLeftFork =>
      puttingLeftFork()
      ctx.self ! PutRightFork
      putRightFork()
    case _ => Behaviors.same
  }

  private def putRightFork(): Behavior[Actions] = Behaviors.receiveMessage {
    case PutRightFork =>
      puttingRightFork()
      ctx.self ! Thinks
      thinks()
    case _ => Behaviors.same
  }

  private def takingLeftFork(): Boolean = {
    if (forks(leftFork)) {
      println(s"$name: Taking left fork $leftFork.")
      forks(leftFork) = false
      true
    } else {
      println(s"$name: Left fork $leftFork is busy.")
      false
    }
  }

  private def takingRightFork(): Boolean = {
    if (forks(rightFork)) {
      println(s"$name: Taking right fork $rightFork.")
      forks(rightFork) = false
      true
    } else {
      println(s"$name: Left fork $rightFork is busy.")
      false
    }
  }

  private def eating(): Unit = {
    println(s"$name: I'm eating...")
    Thread.sleep(1000)
  }

  private def thinking(): Unit = {
    println(s"$name: I'm thinking...")
    Thread.sleep(1000)
  }

  private def puttingLeftFork(): Unit = {
    println(s"$name: Putting left fork $leftFork")
    forks(leftFork) = true
  }

  private def puttingRightFork(): Unit = {
    println(s"$name: Putting right fork $rightFork")
    forks(rightFork) = true
  }
}

object Non_Blocking_Philosophers {

  sealed trait Actions


  case object Thinks extends Actions
  case object TakeLeftFork extends Actions
  case object TakeRightFork extends Actions
  case object Eat extends Actions
  case object PutRightFork extends Actions
  case object PutLeftFork extends Actions

  def apply(name: String,
            leftFork: Int,
            rightFork: Int): Behavior[Actions] = Behaviors.setup { ctx =>
    new Non_Blocking_Philosophers(ctx, name, leftFork, rightFork).thinks()
  }
  
}
