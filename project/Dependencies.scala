package com.notetoself

import sbt._

case class Module(groupID: String, artifactID: String, version: String) {
  def java: Def.Initialize[ModuleID] = Def.setting(groupID % artifactID % version)
  def scala: Def.Initialize[ModuleID] = Def.setting(groupID %% artifactID % version)
}

object Dependencies {

  object Scala {
    val v2 = "2.12.10"
    val v3 = "2.13.1"
  }

  object AWSSDK2 {
    private val version = "2.13.9"
    val core = Module("software.amazon.awssdk", "core", version)
    val sts = Module("software.amazon.awssdk", "sts", version)
    val dynamodb = Module("software.amazon.awssdk", "dynamodb", version)
    val kinesis = Module("software.amazon.awssdk", "kinesis", version)
    val s3 = Module("software.amazon.awssdk", "s3", version)
  }

  object Akka {
    private val version = "2.6.5"
    private val groupID = "com.typesafe.akka"
    val stream = Module(groupID, "akka-stream", version)
    val testKit = Module(groupID, "akka-testkit", version)
    val slf4j = Module(groupID, "akka-slf4j", version)
    val actor = Module(groupID, "akka-actor", version)
  }

  object BetterFiles {
    private val version = "3.8.0"
    val core = Module("com.github.pathikrit", "better-files", version)
  }

  object Cats {
    private val version = "2.1.1"
    val core = Module("org.typelevel", "cats-core", version)
    val kernel = Module("org.typelevel", "cats-kernel", version)
    val effect = Module("org.typelevel", "cats-effect", version)
    val testEffect = Module("com.codecommit", "cats-effect-testing-scalatest", "0.4.0")
  }

  object Circe {
    private val version = "0.11.0"
    val core = Module("io.circe", "circe-core", version)
    val config = Module("io.circe", "circe-config", "0.6.1")
    val parser = Module("io.circe", "circe-parser", version)
    val generic = Module("io.cirice", "circe-generic", version)
    val genericExtras = Module("io.circe", "circe-generic-extras", version)
  }

  object Http4s {
    private val version = "0.20.0"
    private val groupID = "org.http4s"
    val dsl = Module(groupID, "http4s-dsl", version)
    val server = Module(groupID, "http4s-blaze-server", version)
    val client = Module(groupID, "http4s-blaze-client", version)
    val circe = Module(groupID, "http4s-circe", version)
  }

  object TypesafeConfig {
    private val version = "1.4.0"
    val core = Module("com.typesafe", "config", version)
  }

  object ScalaJava8Compat {
    private val version = "0.9.1"
    val scalaJava8Compat = Module("org.scala-lang.modules", "scala-java8-compat", version)
  }

//  object SLF4J {
//    private val version = "1.7.30"
//    val api = Module("org.slf4j", "slf4j-api", version)
//  }

  object ScalaTest {
    private val version = "3.1.1"
    val core = Module("org.scalatest", "scalatest", version)
  }

  object ScalaMock {
    private val version = "1.14.3"
    val scalaCheck = Module("org.scalacheck", "scalacheck", version)
  }

}
