package io.aigar.model

object seed {
  def seedPlayers: Unit = {
    val playerRepository = new PlayerRepository(None)
    playerRepository.dropSchema
    playerRepository.createSchema

    for(id <- 1 to 15) {
      playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&" + id, "player" + id, 0))
    }
  }
}
