package io.aigar.controller

import io.aigar.controller.response._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class LeaderboardController extends AigarStack with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/") {
    LeaderboardResponse(List(
                          LeaderboardEntry(465, "asd", 132),
                          LeaderboardEntry(745, "wow", 2),
                          LeaderboardEntry(7, "such score", 22)
                        ))
  }
}
