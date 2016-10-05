package io.aigar.model

import slick.driver.H2Driver.api._

case class Team(id:Option[Int], teamSecret:String, teamName:String, score:Int)

class Teams(tag: Tag) extends Table[(Int, String, String, Int)](tag, "TEAMS") {
  def id = column[Int]("ID", O.PrimaryKey)
  def teamSecret = column[String]("TEAM_SECRET")
  def teamName = column[String]("TEAM_NAME")
  def score = column[Int]("SCORE", O.Default(0))
  def * = (id, teamSecret, teamName, score)
}


