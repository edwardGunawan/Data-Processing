package com.notetoself

import akka.actor.{ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit, TestProbe}
import com.notetoself.Ingestion.Data
import com.notetoself.Master.{Aggregate, InitializeWorker, WorkerInitialized}

import scala.concurrent.duration._
import com.notetoself.Worker.{Date, Log, Result}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{Assertion, BeforeAndAfterAll}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class MasterSpec
    extends TestKit(ActorSystem("MasterSpec", ConfigFactory.load().getConfig("dataprocessing")))
    with ImplicitSender
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {
  import MasterSpec._

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Master Node" should {
    "Initialized Worker Actor and reply WorkerInitialized" in {
      EventFilter.info(message = s"[Master] Initializing $nWorker worker(s)...", occurrences = 1) intercept {
        val masterRef = system.actorOf(Master.props(testActor))
        masterRef ! InitializeWorker(nWorker)
        expectMsg(WorkerInitialized)
      }
    }

    "Delegate task to worker that is initialized" in {
      EventFilter.info(
        message = s"[Master] received $logString assigning taskId 0 to worker 0",
        occurrences = 1
      ) intercept {
        val masterRef = system.actorOf(Master.props(testActor))
        masterRef ! InitializeWorker(nWorker)
        masterRef ! Data(logString)
      }
    }

    "Wait for all workers done working on its task" in {

      EventFilter.info(
        message = s"[Master] Task is not yet all done, waiting for other workers to send back results",
        occurrences = 1
      ) intercept {
        val masterRef = system.actorOf(Master.props(testActor))
        masterRef ! InitializeWorker(nWorker)
        masterRef ! Data(logString)
        masterRef ! Data(logString1)
      }
    }

    "Reply to the correct ResultMap" in {
      val parent = TestProbe("parent")
      val masterRef = system.actorOf(Master.props(parent.ref))
      masterRef ! InitializeWorker(nWorker)
      masterRef ! Data(logString)
      masterRef ! Data(logString1)

//      expectMsg(Aggregate(Map("200" -> listOfLog)))
      val assertions: Seq[Assertion] = parent.receiveWhile(max = 1 second) {
        case Aggregate(numberOfStatus) => {
          numberOfStatus.values.head.length must equal(2)
        }
        case WorkerInitialized => assert(true)
      }

      assertions.forall(_ == assert(true))

    }

  }
}

object MasterSpec {
  lazy val nWorker = 3

  lazy val logString =
    "10.131.2.1,[29/Nov/2017:13:51:46,GET /contestproblem.php?name=RUET%20OJ%20Server%20Testing%20Contest HTTP/1.1,200"
  lazy val logString1 =
    "10.131.2.1,[29/Nov/2017:13:51:49,GET /details.php?id=44 HTTP/1.1,200"

  lazy val log = Log(
    "10.131.2.1",
    Date(2017, "Nov", 29, "13:51:46"),
    "GET /contestproblem.php?name=RUET%20OJ%20Server%20Testing%20Contest HTTP/1.1",
    "200"
  )
  lazy val log1 = Log("10.131.2.1", Date(2017, "Nov", 29, "13:51:49"), "GET /details.php?id=44 HTTP/1.1", "200")

  lazy val listOfLog = List(log1, log)

}
