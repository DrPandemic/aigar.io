package io.aigar.model

import slick.driver.H2Driver.api._

trait TeamRepository {
  def addTeam(team:Team):Boolean
  def getTeam(id:Int):Team
  def updateTeam(id:Int):Boolean
  def removeTeam(id:Int):Boolean
  def getTeams():List[Team]
}
