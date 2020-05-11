name := "DataProcessing"

import com.notetoself.CompilerPlugins._
import com.notetoself.Dependencies._

import com.timushev.sbt.updates.UpdatesPlugin.autoImport.moduleFilterRemoveValue
import sbt._
import sbtassembly.AssemblyKeys

import sbt.Keys.scalaVersion

version := "0.1"

name := "Data-Processing"

lazy val commonSettings = Seq(
  organization := "Note To Self",
  scalacOptions := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2,12)) =>
        Seq("-Ypartial-unification", "-deprecation")
      case _ =>
        Seq("-Xlint", "-Ywarn-unused", "-deprecation", "-Ymacro-annotations")
    }
  },
  dependencyUpdatesFilter -= moduleFilter(name = "scala-library"),
  dependencyUpdatesFilter -= moduleFilter(organization = "io.circe"),
  dependencyUpdatesFilter -= moduleFilter(organization = "org.http4s"),
  dependencyUpdatesFailBuild := true,
  scalaVersion := Scala.v2,
  addCompilerPlugin(KindProjector.core cross CrossVersion.full),
  addCompilerPlugin(MacroParadise.core cross CrossVersion.full)
)

lazy val root = project.in(file("."))
  .aggregate(dataProcessingAkka)
  .settings(
    commonSettings
  )

lazy val dataProcessingAkka = project.in(file("dataprocessingakka"))
  .settings(
    name := "Data Processing with Akka Actor",
    commonSettings,
    libraryDependencies ++= Seq(
      Akka.actor.scala.value
    )
  )


lazy val assemblySettings = Seq(
  test in AssemblyKeys.assembly := {},
  AssemblyKeys.assemblyJarName in AssemblyKeys.assembly := name.value + ".jar",
  AssemblyKeys.assemblyMergeStrategy in AssemblyKeys.assembly := {
    case "application.conf" => MergeStrategy.concat
    case "reference.conf" => MergeStrategy.concat
    case "deriving.conf" => MergeStrategy.concat
    case PathList("io", "netty", _@_*) => MergeStrategy.first
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case PathList(ps @ _*)
      if Set(
        "service-2.json",
        "waiters-2.json",
        "customization.config",
        "paginators-1.json",
        "module-info.class",
        "mime.types"
      ).contains(ps.last) =>
    MergeStrategy.discard
    case x => MergeStrategy.defaultMergeStrategy(x)
  }
)
