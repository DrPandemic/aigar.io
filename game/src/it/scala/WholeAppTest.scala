package io.aigar.it

import org.scalatra.test.specs2._
import org.scalatra.servlet._
import org.eclipse.jetty.servlet._

// https://github.com/scalatra/scalatra/issues/340#issuecomment-52962174
class WholeAppTest extends MutableScalatraSpec {
  override lazy val servletContextHandler = {
    val handler = new ServletContextHandler(ServletContextHandler.SESSIONS)
    handler.setContextPath(contextPath)
    handler.addEventListener(new ScalatraListener)
    handler.setResourceBase(resourceBasePath)
    handler
  }
}
