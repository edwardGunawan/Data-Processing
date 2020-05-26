package com.notetoself.example
import fs2.Stream
import cats.effect.IO
import cats.implicits._

object Playground extends App {
  def putStrLn[Any](e: Any): IO[Unit] = IO(println(e))
  Stream.emit(42).toList
  Stream.eval(IO(println("Hello World"))).compile.drain.unsafeRunSync

  println(Stream.emits(List(1, 2, 3)).toList)

  // this emits the real stream
  val a = Stream.constant(10).take(5)
  val b = Stream.iterate(1)(_ * 2)

//  println((a ++ b).flatMap(el => Stream.constant(el)).take(13).toList)

  println(
    (a ++ b)
      .evalMap { e =>
        putStrLn(s"in println $e").map(_ => e + 1)
      }
      .debug(b => s"EvalMap $b")
      .take(5)
      .debug(b => s"Take 5 ${b}")
      .compile
      .toList
      .unsafeRunSync()
  )

//  Stream.awakeEvery()
}
