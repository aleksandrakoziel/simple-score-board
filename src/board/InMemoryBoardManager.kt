package board

import board.models.dto.ResultDTO
import board.exceptions.UnknownGameException

class InMemoryBoardManager(private val board: Board) : BoardManager {

    override fun startGame(home: String?, away: String?) = board.addGame(home, away)

    override fun endGame(home: String?, away: String?) = board.removeGame(home, away)

    override fun updateScore(home: String?, away: String?, homeScore: Int, awayScore: Int): ResultDTO =
        board.getGameIdByTeam(away).let { gameId ->
            if (gameId == board.getGameIdByTeam(home)) board.updateScore(gameId, homeScore, awayScore)
            else throw UnknownGameException("No game found for team HOME: $home, AWAY: $away")
        }

    override fun getBoardDescending(): List<ResultDTO> = board.getScoreBoardDescending()
}