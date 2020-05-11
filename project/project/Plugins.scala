package com.notetoself

import sbt._

object Plugins {
  object SBTUpdates {
    private val version = "0.4.2"
    val core:ModuleID = "com.timushev.sbt" % "sbt-updates" % version
  }

  object SBTAssembly {
    private val version = "0.14.9"
    val core:ModuleID = "com.eed3si9n" % "sbt-assembly" % version
  }

  object ScalaFmt {
    private val version = "2.0.1"
    val core:ModuleID = "org.scalameta" % "sbt-scalafmt" % version
  }

  object SBTDependencyGraph {
    private val version = "0.9.2"
    val core: ModuleID = "net.virtual-void" % "sbt-dependency-graph" % version
  }

  object SBTExplicitDependencies {
    private val version = "0.2.9"
    val core:ModuleID = "com.github.cb372" % "sbt-explicit-dependencies" % version
  }

}
