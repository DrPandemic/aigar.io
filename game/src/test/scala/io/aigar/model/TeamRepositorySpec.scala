package io.aigar.model

import org.scalatest._

class TeamRepositorySpec extends FlatSpec {

  def withInMemDatabase(testCode: (TeamRepository, List[Team]) => Any) {
    val teamRepository = new TeamRepository(true)
    val team1 = teamRepository.createTeam(Team(None, "EdgQWhJ!v&", "team1", 0))
    val team2 = teamRepository.createTeam(Team(None, "not_that_secret", "team2", 50))
    val team3 = teamRepository.createTeam(Team(None, "xx3ddfas3", "team3", 56))

    val listTeams = List(team1, team2, team3)

    try{
      testCode(teamRepository, listTeams)
    }
    finally {
      teamRepository.dropSchema
      teamRepository.closeConnection
    }
  }

  it should "create a new team object and return it" in withInMemDatabase { (teamRepository, listTeams) =>
    val team4 = teamRepository.createTeam(Team(None, "EdgQWhJ!v&", "team4", 0))
    assert(team4.teamSecret === "EdgQWhJ!v&")
    assert(team4.teamName === "team4")
    assert(team4.score === 0)
  }

  it should "read the team 2 by its id and equal it" in withInMemDatabase { (teamRepository, listTeams) =>
    assert(teamRepository.readTeam(listTeams(2).id.get).get === listTeams(2))
  }

  it should "read a non-existing id and return nothing" in withInMemDatabase { (teamRepository, listTeams) =>
    assert(teamRepository.readTeam(258741).isEmpty)
  }
  /*

  def test4 = teamRepository.updateTeam(1) must_=== true
  def test5 = teamRepository.updateTeam(3) must_=== false

  def test6 = teamRepository.deleteTeam(team3.id.get) must_=== true
  def test7 = teamRepository.deleteTeam(585858) must_=== false

  def test8 =  teamRepository.getTeams() must_=== List(team1, team2)
  */

}
