import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import nonBlockingPhilosophers._
import Forks.{setFork, getFork}
import Dining._

class nonBlockingPhilosophers(ctx: ActorContext[Actions],
                              name: String,
                              leftFork: Int,
                              rightFork: Int) {
  
  private def thinks(): Behavior[Actions] = Behaviors.receiveMessage {
    case Think =>
      println(s"$name: I'm thinking...")
      action()
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
        ctx.self ! Think
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
        if (blocking == 0) {puttingLeftFork()}
        ctx.self ! Think
        thinks()
      }
    case _ => Behaviors.same
  }

  private def eat(): Behavior[Actions] = Behaviors.receiveMessage {
    case Eat =>
      println(s"$name: I'm eating...")
      action()
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
      ctx.self ! Think
      thinks()
    case _ => Behaviors.same
  }

  private def takingLeftFork(): Boolean = {
    if (getFork(leftFork)) {
      setFork(leftFork, false)
      println(s"$name: Taking left fork $leftFork.")
      true
    } else {
      println(s"$name: Left fork $leftFork is busy.")
      false
    }
  }

  private def takingRightFork(): Boolean = {
    if (getFork(rightFork)) {
      setFork(rightFork, false)
      println(s"$name: Taking right fork $rightFork.")
      true
    } else {
      println(s"$name: Right fork $rightFork is busy.")
      false
    }
  }

  private def action(): Unit = {
    Thread.sleep(1000)
  }

  private def puttingLeftFork(): Unit = {
    setFork(leftFork, true)
    println(s"$name: Putting left fork $leftFork")
  }

  private def puttingRightFork(): Unit = {
    setFork(rightFork, true)
    println(s"$name: Putting right fork $rightFork")
  }
}

object nonBlockingPhilosophers {

  sealed trait Actions
  case object Think extends Actions
  case object TakeLeftFork extends Actions
  case object TakeRightFork extends Actions
  case object Eat extends Actions
  case object PutRightFork extends Actions
  case object PutLeftFork extends Actions

  def apply(name: String,
            leftFork: Int,
            rightFork: Int): Behavior[Actions] = Behaviors.setup { ctx =>
    new nonBlockingPhilosophers(ctx, name, leftFork, rightFork).thinks()
  }
  
}
