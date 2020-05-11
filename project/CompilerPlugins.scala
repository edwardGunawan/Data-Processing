package com.notetoself

import sbt._

object CompilerPlugins {
  object KindProjector {
    private val version = "0.11.0"
    val core = "org.typelevel" % "kind-projector" % version
  }

  object MacroParadise {
    private val version = "2.1.1"
    val core = "org.scalamacros" % "paradise" % version
  }

}
