package board.models.domain

import board.models.dto.ResultDTO

data class Result(
    val home: TeamScore,
    val away: TeamScore
)

fun Result.toDTO(): ResultDTO = ResultDTO(
    home.score.get(),
    home.team,
    away.score.get(),
    away.team
)
