package com.notetoself.example

import java.time.Instant

import cats.effect.concurrent.Deferred
import cats.effect.{ContextShift, Effect, IO}
import fs2._
import fs2.concurrent.Queue

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object QueueStream extends App {
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer = IO.timer(ExecutionContext.global)
  val queue: Stream[IO, Queue[IO, String]] = Stream.eval(Queue.circularBuffer[IO, String](5))

  val element: Stream[IO, String] = for {
    q <- queue
    data <- q.dequeue
  } yield data

  val infiniteStream = Stream.emit(1).repeat.covary[IO].map(_ + 3).take(4) // covary lift the stream to the effect type

  println("Finish defining infiniteStream, if there is no take it will run forever...")

  // if there is no take it will run forever and won't go to the println
  val output = infiniteStream.compile.toVector.unsafeRunSync() //println Result >> Vector(4,4,4,4, ...)

  println(s"Result >> $output")

  Stream.eval(IO(println("something"))).delayBy(5 second)

  val program = Stream.eval(Deferred[IO, Unit]).flatMap { switch =>
    val switcher = Stream.eval(switch.complete()).delayBy(5 seconds).debug(_ => "switcher")

    val program = Stream.repeatEval(IO(println(Instant.now))).metered(1 second)

    program
      .interruptWhen(switch.get.attempt)
      .concurrently(switcher) //run the switcher concurrently in the background with the program (ignoring the output of the switcher)
  }

  program.compile.drain.unsafeRunSync()

}
