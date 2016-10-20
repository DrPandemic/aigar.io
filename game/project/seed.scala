package io.aigar.configs

import sbt._
import Keys._

object seed {
  val key = TaskKey[Unit]("seed", "Seeds the DB")
  val seedTask = key := {
    println("13")
  }
}
