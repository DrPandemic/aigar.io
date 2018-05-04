package io.aigar.model

object seed {
  def seedPlayers(playerRepository: PlayerRepository = new PlayerRepository(None), playerCount: Int): Unit = {
    playerRepository.dropSchema
    playerRepository.createSchema

    for(id <- 1 to playerCount) {
      playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&" + id, "player" + id, 0f))
    }
  }
}
