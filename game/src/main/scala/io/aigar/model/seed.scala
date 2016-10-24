package io.aigar.model

object seed {
  def seedTeams: Unit = {
    val teamRepository = new TeamRepository(None)
    teamRepository.dropSchema
    teamRepository.createSchema

    for(id <- 1 to 15) {
      teamRepository.createTeam(Team(None, "EdgQWhJ!v&" + id, "team" + id, id))
    }
  }
}
