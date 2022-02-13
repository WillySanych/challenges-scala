//import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
//import akka.actor.typed.{ActorRef, Behavior}
//import blockingPhilosophers._
//import Forks.{setForks, getForks}
//
//class blockingPhilosophers(ctx: ActorContext[Actions],
//                           name: String,
//                           leftFork: Int,
//                           rightFork: Int) {
//
//  private def thinks(): Behavior[Actions] = Behaviors.receiveMessage {
//    case Think =>
//      thinking()
//      ctx.self ! TakeLeftFork
//      takeLeftFork()
//    case _ => Behaviors.same
//  }
//
//  private def takeLeftFork(): Behavior[Actions] = Behaviors.receiveMessage {
//    case TakeLeftFork =>
//      val res = takingLeftFork()
//      if (res) {
//        ctx.self ! TakeRightFork
//        takeRightFork()
//      } else {
//        ctx.self ! Think
//        thinks()
//      }
//    case _ => Behaviors.same
//  }
//
//  private def takeRightFork(): Behavior[Actions] = Behaviors.receiveMessage {
//    case TakeRightFork =>
//      val res = takingRightFork()
//      if (res) {
//        ctx.self ! Eat
//        eat()
//      } else {
////        puttingLeftFork()
//        ctx.self ! Think
//        thinks()
//      }
//    case _ => Behaviors.same
//  }
//
//  private def eat(): Behavior[Actions] = Behaviors.receiveMessage {
//    case Eat =>
//      eating()
//      ctx.self ! PutLeftFork
//      putLeftFork()
//    case _ => Behaviors.same
//  }
//
//  private def putLeftFork(): Behavior[Actions] = Behaviors.receiveMessage {
//    case PutLeftFork =>
//      puttingLeftFork()
//      ctx.self ! PutRightFork
//      putRightFork()
//    case _ => Behaviors.same
//  }
//
//  private def putRightFork(): Behavior[Actions] = Behaviors.receiveMessage {
//    case PutRightFork =>
//      puttingRightFork()
//      ctx.self ! Think
//      thinks()
//    case _ => Behaviors.same
//  }
//
//  private def takingLeftFork(): Boolean = {
//    if (getForks(leftFork)) {
//      println(s"$name: Taking left fork $leftFork.")
//      setForks(leftFork, false)
//      true
//    } else {
//      println(s"$name: Left fork $leftFork is busy.")
//      false
//    }
//  }
//
//  private def takingRightFork(): Boolean = {
//    if (getForks(rightFork)) {
//      println(s"$name: Taking right fork $rightFork.")
//      setForks(rightFork, false)
//      true
//    } else {
//      println(s"$name: Left fork $rightFork is busy.")
//      false
//    }
//  }
//
//  private def eating(): Unit = {
//    println(s"$name: I'm eating...")
//    Thread.sleep(1000)
//  }
//
//  private def thinking(): Unit = {
//    println(s"$name: I'm thinking...")
//    Thread.sleep(1000)
//  }
//
//  private def puttingLeftFork(): Unit = {
//    println(s"$name: Putting left fork $leftFork")
//    setForks(leftFork, true)
//  }
//
//  private def puttingRightFork(): Unit = {
//    println(s"$name: Putting right fork $rightFork")
//    setForks(rightFork, true)
//  }
//}
//
//object blockingPhilosophers {
//
//  sealed trait Actions
//
//
//  case object Think extends Actions
//  case object TakeLeftFork extends Actions
//  case object TakeRightFork extends Actions
//  case object Eat extends Actions
//  case object PutRightFork extends Actions
//  case object PutLeftFork extends Actions
//
//  def apply(name: String,
//            leftFork: Int,
//            rightFork: Int): Behavior[Actions] = Behaviors.setup { ctx =>
//    new blockingPhilosophers(ctx, name, leftFork, rightFork).thinks()
//  }
//  
//}
