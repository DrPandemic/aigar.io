package io.aigar.model

object seed {
  def seedPlayers(playerRepository: PlayerRepository = new PlayerRepository(None)): Unit = {
    playerRepository.dropSchema
    playerRepository.createSchema

    for(id <- 1 to 15) {
      playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&" + id, "player" + id, 0f))
    }
  }
}
