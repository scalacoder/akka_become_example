package com.scalacoder.train

import akka.actor.{Actor, Props}
import com.scalacoder.train.PassengerCountActor.{NumberOfPassengers, Leave, Enter}


class PassengerCountActor extends Actor {
  override def receive: Receive = handleMessage(0)

  def handleMessage(numberOfPassengers: Int): Receive = {
    case Enter => context.become(handleMessage(numberOfPassengers + 1))
    case Leave if numberOfPassengers > 0 => context.become(handleMessage(numberOfPassengers - 1))
    case NumberOfPassengers.Get => sender ! NumberOfPassengers.Result(numberOfPassengers)
  }
}

object PassengerCountActor {
  def props: Props = Props(new PassengerCountActor)

  case object Enter

  case object Leave

  object NumberOfPassengers {
    case object Get
    case class Result(numberOfPassengers: Int)
  }

}
