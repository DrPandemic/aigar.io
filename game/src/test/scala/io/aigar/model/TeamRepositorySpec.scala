package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._
import org.specs2.Specification

class TeamRepositorySpec extends Specification {def is = s2"""

 This is my first specification
   Team created must be returned                        $test1
   Should read existing team without error                     $test2
   Should read team 3 with error                        $test3
   Should update team 1 without error                   $test4
   Should update team 3 with error                      $test5
   Should delete team 1 without error                   $test6
   Should delete team 3 without error                   $test7
   Should return a list with team 1 and 2               $test8
                                 """

  val cpds = new ComboPooledDataSource
  cpds.setDriverClass("org.h2.Driver")
  cpds.setJdbcUrl("jdbc:h2:mem:test")
  cpds.setUser("root")
  cpds.setPassword("")
  cpds.setMinPoolSize(1)
  cpds.setAcquireIncrement(1)
  cpds.setMaxPoolSize(50)

  val db = Database.forDataSource(cpds)

  val teamRepositoryMem = new TeamRepositoryMem(db)


  val team1 = Team(1, "EdgQWhJ!v&", "New Team", 0)
  val team2 = Team(2,"not_that_secret", "your_team", 50)

  def test1 = teamRepositoryMem.createTeam(team1) must_=== team1

  def test2 = teamRepositoryMem.readTeam(1) must_=== team1
  def test3 = teamRepositoryMem.readTeam(3) must_=== null

  def test4 = teamRepositoryMem.updateTeam(1) must_=== true
  def test5 = teamRepositoryMem.updateTeam(3) must_=== false

  def test6 = teamRepositoryMem.deleteTeam(1) must_=== true
  def test7 = teamRepositoryMem.deleteTeam(3) must_=== false

  def test8 =  teamRepositoryMem.getTeams() must_=== List(team1, team2)
}
