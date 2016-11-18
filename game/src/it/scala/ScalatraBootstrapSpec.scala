package io.aigar.it

import io.aigar.controller.response._
import io.aigar.game._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import org.scalatra.test.specs2._
import org.specs2.matcher._

class ScalatraBootstrapSpec extends WholeAppTest {
  protected implicit val jsonFormats: Formats = DefaultFormats

  "Launching the whole application" should {
    "launch a game thread without a game inside" in {
      get("/api/1/game/" + Game.RankedGameId.toString) {
        status must be_==(404).eventually
      }
    }
  }
}
