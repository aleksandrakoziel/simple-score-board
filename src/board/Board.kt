package board

import board.models.dto.ResultDTO
import board.exceptions.*
import kotlin.jvm.Throws

interface Board {
    /**
     * Add a game to the board with initial result 0 - 0
     * @param home host team name, not null
     * @param away away team name, not null
     * @return unique game id
     * @throws IllegalArgumentException
     * @throws InvalidGameException
     * @throws TeamAlreadyPlayingException
     */
    @Throws(
        IllegalArgumentException::class,
        InvalidGameException::class,
        TeamAlreadyPlayingException::class
    )
    fun addGame(home: String?, away: String?): String

    /**
     * Remove a game from the board by a unique game id
     * @param gameId unique game id of the current game, not null
     * @return game result
     * @throws IllegalArgumentException
     * @throws UnknownGameException
     */
    @Throws(
        IllegalArgumentException::class,
        UnknownGameException::class
    )
    fun removeGame(gameId: String?): ResultDTO

    /**
     * Remove a game from the board by the specific home and away team names
     * @param home host team name of the current game, not null
     * @param away away team name of the current game, not null
     * @return game result
     * @throws IllegalArgumentException
     * @throws UnknownTeamException
     */
    @Throws(
        IllegalArgumentException::class,
        UnknownTeamException::class
    )
    fun removeGame(home: String?, away: String?): ResultDTO

    /**
     * Remove a game from the board by the specific home and away team names
     * @param team currently playing team name, not null
     * @return unique game id, which the team is currently playing
     * @throws UnknownTeamException
     */
    @Throws(
        IllegalArgumentException::class,
        UnknownTeamException::class
    )
    fun getGameIdByTeam(team: String?): String

    /**
     * Return the score board
     * @return list of results
     * @throws IllegalArgumentException
     */
    @Throws(
        IllegalArgumentException::class,
        UnknownTeamException::class,
        InvalidScoreException::class
    )
    fun getScoreBoard(): List<ResultDTO>

    /**
     * Return the score board sorted by the summary score and start time
     * @return sorted list of results
     * @throws IllegalArgumentException
     */
    fun getScoreBoardDescending(): List<ResultDTO>

    /**
     * Manually update the game score by ID, a home team score and an away team score
     * @param gameId unique id of the current game, not null
     * @param homeScore host team score, not negative
     * @param awayScore away team score, not negative
     * @return
     * @throws IllegalArgumentException
     */
    @Throws(
        IllegalArgumentException::class,
        InvalidScoreException::class,
        UnknownGameException::class
    )
    fun updateScore(gameId: String?, homeScore: Int, awayScore: Int): ResultDTO

    /**
     * Increase a score of the given team by 1
     * @param team team name which is currently added to the board, not null
     * @return
     * @throws IllegalArgumentException
     * @throws UnknownTeamException
     */
    @Throws(
        IllegalArgumentException::class,
        UnknownTeamException::class
    )
    fun goal(team: String?)

    /**
     * Decrease a score of the given team by 1 if the score is positive
     * @param team team name which is currently added to the board, not null
     * @return
     * @throws IllegalArgumentException
     * @throws UnknownTeamException
     * @throws InvalidScoreException
     */
    @Throws(
        IllegalArgumentException::class,
        UnknownTeamException::class,
        InvalidScoreException::class
    )
    fun revokeGoal(team: String?)

    /**
     * Decrease a score of the given team by 1 if the score is positive
     * @param gameId unique ID of the current game, not null
     * @return game result
     * @throws IllegalArgumentException
     * @throws UnknownGameException
     */
    @Throws(
        IllegalArgumentException::class,
        UnknownGameException::class
    )
    fun getCurrentResult(gameId: String?): ResultDTO
}