package com.scalacoder.train

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestKit, ImplicitSender}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, FunSuiteLike}

import scala.util.Random

@RunWith(classOf[JUnitRunner])
class SomeTests(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FunSuiteLike with Matchers {

  private var countActor: Option[ActorRef] = None

  test("Counting") {
    numberOfPassengersIn shouldBe 0
    passengerComesIn
    numberOfPassengersIn shouldBe 1

    1 to 10 foreach (_ => passengerComesIn)

    numberOfPassengersIn shouldBe 11

    1 to 5 foreach (_ => passengerLeaved)

    numberOfPassengersIn shouldBe 6

    stopCounter()
  }

  test("Stop and Start Counter") {
    numberOfPassengersIn shouldBe 0

    1 to 10 foreach (_ => passengerComesIn)
    1 to 5 foreach (_ => passengerLeaved)

    numberOfPassengersIn shouldBe 5

    stopCounter()

    passengerComesIn

    numberOfPassengersIn shouldBe 1

    stopCounter()
  }

  test("Number of passenger can't be less than 0") {
    numberOfPassengersIn shouldBe 0

    1 to 5 foreach (_ => passengerComesIn)
    1 to 20 foreach (_ => passengerLeaved)

    numberOfPassengersIn shouldBe 0

    stopCounter()
  }

  def this() = this(ActorSystem("SomeTests"))

  private def passengerComesIn: Unit = passengerCounter ! PassengerCountActor.Enter

  private def passengerLeaved: Unit = passengerCounter ! PassengerCountActor.Leave

  private def passengerCounter: ActorRef = countActor match {
    case Some(actor) => actor
    case None =>
      countActor = Some(system.actorOf(PassengerCountActor.props, s"Counter${Random.nextInt()}"))
      countActor.get
  }

  private def numberOfPassengersIn: Int = {
    passengerCounter ! PassengerCountActor.NumberOfPassengers.Get
    expectMsgType[PassengerCountActor.NumberOfPassengers.Result].numberOfPassengers
  }

  private def stopCounter(): Unit = countActor match {
    case Some(actor) =>
      system.stop(actor)
      countActor = None
    case None =>
  }

}
