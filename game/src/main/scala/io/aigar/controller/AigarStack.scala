package io.aigar.controller
import io.aigar.controller.response.{ErrorResponse}

import org.scalatra._
import scalate.ScalateSupport

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

trait AigarStack extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }


  trap(404) {
    returnError(404, "not found")
  }

  trap(422) {
    returnError(422, "unprocessable entity")
  }

  def returnError(statusCode: Int, message: String): ErrorResponse  = {
    status = statusCode
    contentType = formats("json")
    ErrorResponse(message)
  }
}
