package io.aigar.model

object seed {
  def seedPlayers(playerRepository: PlayerRepository, scoreRepository: ScoreRepository, playerCount: Int): Unit = {
    playerRepository.dropSchema
    playerRepository.createSchema
    scoreRepository.dropSchema
    scoreRepository.createSchema

    for(id <- 1 to playerCount) {
      playerRepository.createPlayer(PlayerModel(None, "EdgQWhJ!v&" + id, "player" + id, 0f))
    }
  }
}
