package board

import board.models.dto.ResultDTO
import board.exceptions.*
import board.models.domain.Game
import board.models.domain.Result
import board.models.domain.TeamScore
import board.models.domain.toDTO
import java.util.concurrent.atomic.AtomicInteger

class InMemoryBoard : Board {

    private val board: MutableList<Game> = mutableListOf()

    override fun addGame(home: String?, away: String?): String = when {
        home == null || away == null -> throw IllegalArgumentException(
            "Host: $home or guest $away cannot be null"
        )

        home == away -> throw InvalidGameException("Team $home cannot play itself")

        isTeamPlaying(home) -> throw TeamAlreadyPlayingException(
            "Host team $home is already playing a game. End previous game before adding."
        )

        isTeamPlaying(away) -> throw TeamAlreadyPlayingException(
            "Guest team $away is already playing a game. End previous game before adding."
        )

        else -> generateGameId(home, away).also { id ->
            board.add(
                Game(
                    Result(
                        TeamScore(AtomicInteger(0), home),
                        TeamScore(AtomicInteger(0), away)
                    ),
                    id,
                    System.currentTimeMillis()
                )
            )
        }
    }

    private fun isTeamPlaying(team: String?) =
        board.any { it.result.home.team == team || it.result.away.team == team }

    override fun removeGame(gameId: String?): ResultDTO =
        gameId?.let {
            board.firstOrNull {
                it.id == gameId
            }?.let { game ->
                board.remove(game)
                game.result.toDTO()
            } ?: throw UnknownGameException(
                """Score board does not contain the game matching the predicate:
            GAME ID: $gameId
            Please, try to identify the game by home and away teams.""".trimMargin()
            )
        } ?: throw IllegalArgumentException("Game ID cannot be null")

    override fun removeGame(home: String?, away: String?): ResultDTO =
        if (home != null && away != null) {
            board.firstOrNull {
                it.result.home.team == home && it.result.away.team == away
            }?.let { game ->
                board.remove(game)
                game.result.toDTO()
            } ?: throw UnknownTeamException(
                """Score board does not contain the game matching the predicate:
            HOME: $home
            AWAY: $away
            Please, try to identify the game by id.""".trimMargin()
            )
        } else {
            throw IllegalArgumentException("Teams cannot be null")
        }

    override fun getGameIdByTeam(team: String?): String =
        if (team != null) {
            board.firstOrNull { it.result.home.team == team || it.result.away.team == team }?.id
                ?: throw UnknownTeamException(
                    """Score board does not contain the game matching the predicate:
                   TEAM: $team""".trimMargin()
                )
        } else throw IllegalArgumentException("Team value cannot be null")

    override fun getScoreBoardDescending(): List<ResultDTO> = board
        .sortedWith(
            compareBy<Game> { it.result.away.score.get() + it.result.home.score.get() }
                .reversed()
                .thenBy { it.startTime }
        )
        .map { it.result.toDTO() }

    override fun updateScore(gameId: String?, homeScore: Int, awayScore: Int): ResultDTO = when {
        gameId.isNullOrEmpty() ->
            throw IllegalArgumentException("GameId cannot be null or empty")

        homeScore < 0 || awayScore < 0 ->
            throw InvalidScoreException(
                """Scores cannot be negative number. 
                |Home Score: $homeScore
                |Away Score: $awayScore""".trimMargin()
            )

        else -> board.firstOrNull { it.id == gameId }?.result?.let {
            it.home.score.set(homeScore)
            it.away.score.set(awayScore)
            it.toDTO()
        } ?: throw UnknownGameException("There is no game with given id: $gameId")
    }

    override fun goal(team: String?) {
        if (team != null) {
            board.firstOrNull { it.result.home.team == team }?.result?.home?.score?.incrementAndGet()
                ?: board.firstOrNull { it.result.away.team == team }?.result?.away?.score?.incrementAndGet()
                ?: throw UnknownTeamException("There is no game with given team: $team")
        } else throw IllegalArgumentException("Team cannot be null")
    }

    override fun revokeGoal(team: String?) {
        if (team != null) {
            board.firstOrNull { it.result.home.team == team }?.result?.let {
                it.home.score.updateAndGet { score ->
                    if (score > 0) score - 1
                    else throw InvalidScoreException("There is no goal to revoke for the given team: $team")
                }
            } ?: board.firstOrNull { it.result.away.team == team }?.result?.let {
                it.away.score.updateAndGet { score ->
                    if (score > 0) score - 1
                    else throw InvalidScoreException("There is no goal to revoke for the given team: $team")
                }
            } ?: throw UnknownTeamException("There is no game with given team: $team")
        } else throw IllegalArgumentException("Team cannot be null")
    }

    override fun getCurrentResult(gameId: String?): ResultDTO =
        if (!gameId.isNullOrEmpty()) {
            board.firstOrNull {
                it.id == gameId
            }?.result?.toDTO() ?: throw UnknownGameException("There is no game with given id: $gameId")
        } else {
            throw IllegalArgumentException("Invalid game id value: $gameId")
        }

    override fun getScoreBoard(): List<ResultDTO> = board.map { it.result.toDTO() }

    private fun generateGameId(host: String, guest: String) = "$host-$guest"

    fun clear() = board.clear()

}