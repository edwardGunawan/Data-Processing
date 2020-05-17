package com.notetoself

import akka.actor.{Actor, ActorLogging, Props}
import com.notetoself.Supervisor.{Start, Stop}

object Supervisor {
  case object Start
  case object Stop

  // good way pass create an actor
  def props(parallelism: Int) = Props(new Supervisor(parallelism))
}

class Supervisor(nWorker: Int) extends Actor with ActorLogging {
  override def receive: Receive = {
    case Start =>
      val ingestion = context.actorOf(Props[Ingestion], "ingestion")
      ingestion ! Ingestion.StartIngestion(nWorker)
    // create ingestor actor and send message to ingestor actor
    // ingestor actor will the value from weblog.csv and send the value one by one to master node
    // master node start distributing each of the weblog.csv to the worker
    // master node will aggregate them after.
    case Stop =>
      log.info("[Supervisor] All things are done, stopping the system")
      context.system.terminate()
  }
}
