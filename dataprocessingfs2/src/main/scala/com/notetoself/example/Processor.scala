package com.notetoself.example

import java.nio.file.Paths

import cats.effect.{Blocker, ContextShift, ExitCode, IO, IOApp}
import com.notetoself.example.Processor.{Date, Log}
import fs2.{Stream, io, text}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex

trait Processor {

  def processData(in: String, out: String, parallelism: Int)(
    implicit contextShift: ContextShift[IO]
  ): Stream[IO, Unit] = {
    Stream.resource(Blocker[IO]).flatMap { blocker =>
      val inResource = getClass.getResource(in)
      val outResource = getClass.getResource(out)
      io.file
        .readAll[IO](Paths.get(inResource.toURI), blocker, 4096)
        .through(text.utf8Decode)
        .through(text.lines)
        .filter(isValidIp) // filter out valid IP
        .parEvalMapUnordered(parallelism)(convertToLog)
        .debug(a => s"parallel map ${a}")
        //NOTE: does this do asynchronous fold?
        .fold(Map.empty[String, Int]) { (map, currLog) =>
          val updatedStatus = map.getOrElse(currLog.status, 0) + 1
          map + (currLog.status -> updatedStatus)
        }
        .debug(a => s"fold $a")
        .flatMap { m =>
          Stream.fromIterator[IO](m.keys.map { key =>
            s"Status : ${key} has a total of ${m(key)} amount "
          }.iterator)
        }
        .debug(a => s"flatMap $a")
        .through(text.utf8Encode)
        .through(io.file.writeAll(Paths.get(outResource.toURI), blocker))
    }
  }

  def convertToLog(line: String): IO[Log] = line.split(",").toList match {
    case ip :: time :: url :: status :: _ =>
      IO(Log(ip, convertToDate(time), url, status))
  }

  def isValidIp(line: String): Boolean = {
    val ipRegex: Regex = """.*?(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3}).*""".r
    ipRegex.pattern.matcher(line.split(",")(0)).matches()
  }

  def convertToDate(time: String): Date = time.substring(1).split("/").toList match {
    case date :: month :: yearAndTime :: _ =>
      yearAndTime.split(":").toList match {
        case year :: rest => Date(year.toInt, month, date.toInt, rest.mkString(":"))
      }
  }
}

object Processor {
  case class Log(ip: String, time: Date, url: String, status: String)
  case class Date(year: Int, month: String, date: Int, time: String)
}

object Main extends IOApp with Processor {

  def run(args: List[String]): IO[ExitCode] = {

    processData("/weblog.csv", "/out.txt", 100).compile.last.map(_ => ExitCode.Success)

  }
}
