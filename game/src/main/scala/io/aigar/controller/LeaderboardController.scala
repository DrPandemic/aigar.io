package io.aigar.controller

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import slick.driver.H2Driver.api._

class LeaderboardController(db: Database) extends AigarStack with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  case class Leaderboard(name: String)

  get("/") {
    List(
      Leaderboard("asd"),
      Leaderboard("another")
    )
  }
}
