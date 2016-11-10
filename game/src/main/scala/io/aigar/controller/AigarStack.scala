package io.aigar.controller
import io.aigar.controller.response.{ErrorResponse}

import org.scalatra._
import scalate.ScalateSupport

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

trait AigarStack
    extends ScalatraServlet
    with ScalateSupport
    with JacksonJsonSupport
    with GZipSupport
{
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  trap(400) {
    returnError(400, "invalid request")
  }

  trap(403) {
    returnError(403, "forbidden")
  }

  trap(404) {
    returnError(404, "not found")
  }

  trap(422) {
    returnError(422, "unprocessable entity")
  }

  trap(500) {
    returnError(500, "internal server error")
  }

  def returnError(statusCode: Int, message: String): ErrorResponse  = {
    status = statusCode
    contentType = formats("json")
    ErrorResponse(message)
  }
}
