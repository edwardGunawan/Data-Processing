package com.notetoself

import akka.actor.{ActorSystem, Props}
import akka.testkit.{EventFilter, TestActorRef, TestKit}
import com.notetoself.Ingestion.StartIngestion
import com.notetoself.Master.{Aggregate, InitializeWorker}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class IngestionSpec
    extends TestKit(ActorSystem("IngestionSpec", ConfigFactory.load().getConfig("dataprocessing")))
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "IngestionHandler" should {
    "test the right valid IP" in {
      val ingestionHandler = new IngestionHandler {}
      val validIp = "10.131.2.1"
      ingestionHandler.isValidIp(validIp) must equal(true)
    }

    "return false with invalid IP" in {
      val ingestionHandler = new IngestionHandler {}
      val validIp = "something"
      ingestionHandler.isValidIp(validIp) must equal(false)
    }
  }

  "Ingestion" should {
    "Return initialized when parent worker sends StartIngestion message" in {

      val nWorker = 3

      EventFilter.info(
        message = "[Ingestion] worker is initialized. Getting lines from source and send to masterNode ..."
      ) intercept {
        val ingestion = system.actorOf(Props[Ingestion])
        ingestion ! StartIngestion(nWorker)
      }

    }

    "Return the right aggregate result" in {

      EventFilter.info(
        start = "[Ingestion] total status Code: "
      ) intercept {
        val ingestion = system.actorOf(Props[Ingestion])
        ingestion ! StartIngestion(nWorker = 3)
      }

    }
  }
}
