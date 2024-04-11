package board.models.domain

import java.util.concurrent.atomic.AtomicInteger

data class TeamScore(
    var score: AtomicInteger,
    val team: String
)