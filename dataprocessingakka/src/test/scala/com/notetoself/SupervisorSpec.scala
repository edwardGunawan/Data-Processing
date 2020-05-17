package com.notetoself

import akka.actor.{ActorSystem, Terminated}

import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.notetoself.Supervisor.Start
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class SupervisorSpec
    extends TestKit(ActorSystem("SupervisorSpec"))
    with AnyWordSpecLike
    with ImplicitSender
    with BeforeAndAfterAll {
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Supervisor" should {
    "start Ingestion succesfully" in {
      EventFilter.info("[Ingestion] Initializing Worker ...") intercept {
        val supervisorRef = system.actorOf(Supervisor.props(1))
        supervisorRef ! Start
      }
    }

    "stop the system once everything is done" in {
      val supervisorRef = system.actorOf(Supervisor.props(1))
      watch(supervisorRef) // watching over supervisor actor
      supervisorRef ! Start
      val terminated = expectMsgType[Terminated]

      assert(terminated.actor == supervisorRef)

    }
  }
}
