package board.models.domain


data class Game(
    val result: Result,
    val id: String,
    val startTime: Long
)