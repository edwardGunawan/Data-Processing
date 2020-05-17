package com.notetoself

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.notetoself.Worker.{Date, Execute, Log, Result}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class WorkerSpec
    extends TestKit(ActorSystem("WorkSpec"))
    with ImplicitSender
    with AnyWordSpecLike
    with BeforeAndAfterAll {
  import WorkerSpec._

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "WorkerHandler" should {
    "convert String to Log case class" in {
      workerHandler.convertToLog(logSample) equals (expectedLog)
    }

    "convert date String to Date case class" in {
      workerHandler.convertToDate(dateSample) equals (expectedDate)
    }
  }

  "Worker" should {
    "reply with a Result to an execution command" in {
      val worker = system.actorOf(Props[Worker])
      val taskId = 1
      worker ! Execute(taskId = taskId, logSample, testActor)

      val expectedResult = Result(taskId, expectedLog)
      expectMsg(expectedResult)
    }
  }
}

object WorkerSpec {
  lazy val logSample =
    "10.131.2.1,[29/Nov/2017:13:51:46,GET /contestproblem.php?name=RUET%20OJ%20Server%20Testing%20Contest HTTP/1.1,200"

  lazy val dateSample = "[29/Nov/2017:13:51:46"

  lazy val workerHandler = new WorkerHandler {}
  lazy val expectedDate = Date(year = 2017, month = "Nov", date = 29, time = "13:51:46")

  lazy val expectedLog = Log(
    ip = "10.131.2.1",
    time = expectedDate,
    url = "GET /contestproblem.php?name=RUET%20OJ%20Server%20Testing%20Contest HTTP/1.1",
    status = "200"
  )
}
