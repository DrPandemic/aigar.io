package io.aigar.model

import com.mchange.v2.c3p0.ComboPooledDataSource
import slick.driver.H2Driver.api._
import org.specs2.Specification

class TeamRepositorySpec extends Specification {def is = s2"""

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
  val teamRepository = new TeamRepository(db)

  val team1 = teamRepository.createTeam(Team(None, "EdgQWhJ!v&", "team1", 0))
  val team2 = teamRepository.createTeam(Team(None, "not_that_secret", "team2", 50))
  val team3 = teamRepository.createTeam(Team(None, "xx3ddfas3", "team3", 56))

  teamRepository.dropSchema()

  def test1 = teamRepository.readTeam(team1.id.get) must_=== Some(team1)
  def test2 = teamRepository.readTeam(team1.id.get) must_=== Some(team1)
  def test3 = teamRepository.readTeam(585858) must_=== null

  def test4 = teamRepository.updateTeam(1) must_=== true
  def test5 = teamRepository.updateTeam(3) must_=== false

  def test6 = teamRepository.deleteTeam(team3.id.get) must_=== true
  def test7 = teamRepository.deleteTeam(585858) must_=== false

  def test8 =  teamRepository.getTeams() must_=== List(team1, team2)

}
