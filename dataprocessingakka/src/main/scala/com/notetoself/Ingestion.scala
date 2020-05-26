package com.notetoself

import java.io.{FileOutputStream, InputStream, File => JFile}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.notetoself.Master.Aggregate

import scala.io.Source
import scala.util.matching.Regex
import better.files._
import File._

/*
  1. Initializing the master node
  2. Get all the file to the master node
  3. Collect the result and write to out.txt
 */
class Ingestion extends Actor with ActorLogging with IngestionHandler {
  import Ingestion._
  val inputStream = openInputStream
  var masterNode: ActorRef = context.actorOf(Master.props(context.self), "masterNode")
  override def receive: Receive = {
    case StartIngestion(nWorker) =>
      log.info("[Ingestion] Initializing Worker ...")
      masterNode ! Master.InitializeWorker(nWorker)
    /*
      If worker is initialized, then get all the lines from the file, and send it one by one to the master node
     */
    case Master.WorkerInitialized =>
      log.info("[Ingestion] worker is initialized. Getting lines from source and send to masterNode ...")
      Source.fromInputStream(inputStream).getLines().toList.filter(isValidIp).foreach(masterNode ! Data(_))
      inputStream.close()
    case Aggregate(result) =>
      log.info(s"[Ingestion] total status Code: ${result.keys.map(k => s"$k -> ${result(k).length}").toString()}")
      val lines = result.keys.map { key =>
        s"Status : ${key} has a total of ${result(key).length} amount}"
      }

      writeToOutputFile(lines)
      context.parent ! Supervisor.Stop
  }

}

trait IngestionHandler {

  def isValidIp(line: String): Boolean = {
    val ipRegex: Regex = """.*?(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3}).*""".r
    ipRegex.pattern.matcher(line.split(",")(0)).matches()
  }

  def openInputStream: InputStream = getClass.getResourceAsStream("/weblog.csv")

  def writeToOutputFile(lines: Iterable[String]) = {
    val path = getClass.getResource("/out.txt").getPath
    File(path).createIfNotExists().clear().appendLines(lines.mkString(","))
  }
}

object Ingestion {
  case class StartIngestion(nWorker: Int)
  case class Data(logString: String)
}
