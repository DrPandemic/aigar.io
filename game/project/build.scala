import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object AigarBuild extends Build {
  val Organization = "io.aigar"
  val Name = "aigar"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.8"
  val ScalatraVersion = "2.4.1"

  lazy val project = Project ("aigar", file("."))
    .configs(IntegrationTest)
    .settings(ScalatraPlugin.scalatraSettings)
    .settings(scalateSettings)
    .settings(Defaults.itSettings : _*)
    .settings(projectSettings)
    .enablePlugins(JettyPlugin)

  lazy val projectSettings = Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= deps,
      testOptions += Tests.Setup(_ => sys.props("testing") = "true"),
      scalateTemplates)

  lazy val deps = Seq(
    "org.scalatra" %% "scalatra" % ScalatraVersion,
    "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
    "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test,it",
    "org.slf4j" % "slf4j-api" % "1.7.13" % "provided",
    "org.slf4j" % "slf4j-nop" % "1.7.13" % "test,it",
    "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container",
    "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
    "org.json4s" %% "json4s-jackson" % "3.3.0.RC2",
    "org.scalatra" %% "scalatra-json" % "2.4.0-RC2-2",
    "com.typesafe.slick" %% "slick" % "3.1.1",
    "com.h2database" % "h2" % "1.4.192",
    "com.mchange" % "c3p0" % "0.9.5.1",
    "org.scalactic" %% "scalactic" % "3.0.0",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test,it",
    "com.github.jpbetz" % "subspace" % "0.1.0"
      )
  lazy val scalateTemplates =
    scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
      Seq(
        TemplateConfig(
          base / "webapp" / "WEB-INF" / "templates",
          Seq.empty,  /* default imports should be added here */
         Seq(
           Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
         ),  /* add extra bindings here */
        Some("templates")
        )
      )
    }
}
