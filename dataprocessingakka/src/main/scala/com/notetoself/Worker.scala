package com.notetoself

import akka.actor.{Actor, ActorRef}
import com.notetoself.Worker.{Date, Execute, Log, Result}

class Worker extends Actor with WorkerHandler {
  override def receive: Receive = {
    case Execute(id, task, sender) =>
      sender ! Result(id, convertToLog(task))
  }

}

trait WorkerHandler {

  def convertToLog(line: String): Log = line.split(",").toList match {
    case ip :: time :: url :: status :: _ =>
      Log(ip, convertToDate(time), url, status)
  }

  def convertToDate(time: String): Date = time.substring(1).split("/").toList match {
    case date :: month :: yearAndTime :: _ =>
      yearAndTime.split(":").toList match {
        case year :: rest => Date(year.toInt, month, date.toInt, rest.mkString(":"))
      }
  }

}

object Worker {
  case class Log(ip: String, time: Date, url: String, status: String)
  case class Date(year: Int, month: String, date: Int, time: String)
  case class Execute(taskId: Int, task: String, replyTo: ActorRef) // received Execute Task
  case class Result(workerId: Int, result: Log) // Send Result
}
