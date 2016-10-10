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
    assert(teamRepository.readTeam(listTeams(1).id.get).get === listTeams(1))
  }

  it should "read a non-existing id and return nothing" in withInMemDatabase { (teamRepository, listTeams) =>
    assert(teamRepository.readTeam(258741).isEmpty)
  }

  it should "update an existing team with success" in withInMemDatabase { (teamRepository, listTeams) =>
    val teamToUpdate = Team(teamRepository.readTeam(listTeams.head.id.get).get.id, "new_secret", "new team", 500)
    val teamUpdated = teamRepository.updateTeam(teamToUpdate).get

    assert(teamUpdated === teamToUpdate)
  }

  it should "try to update a non-existing team without success" in withInMemDatabase { (teamRepository, listTeams) =>
    assert(teamRepository.updateTeam(Team(Some(258741), "", "", 500)).isEmpty)
  }

  it should "delete an existing team with success" in withInMemDatabase { (teamRepository, listTeams) =>
    assert(teamRepository.deleteTeam(listTeams.head.id.get))
    assert(teamRepository.getTeams().size === listTeams.size - 1)
  }

  it should "try to delete a non-existing team without success" in withInMemDatabase { (teamRepository, listTeams) =>
    assert(teamRepository.deleteTeam(258741) === false)
    assert(teamRepository.getTeams().size === listTeams.size)
  }

/*
  it should "read a non-existing id and return nothing" in withInMemDatabase { (teamRepository, listTeams) =>
    assert(teamRepository.readTeam(258741).isEmpty)
  }


  def test4 = teamRepository.updateTeam(1) must_=== true
  def test5 = teamRepository.updateTeam(3) must_=== false

  def test6 = teamRepository.deleteTeam(team3.id.get) must_=== true
  def test7 = teamRepository.deleteTeam(585858) must_=== false

  def test8 = teamRepository.getTeams() must_=== List(team1, team2)
  */

}
