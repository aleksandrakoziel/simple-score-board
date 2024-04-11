package board

import board.exceptions.*
import board.models.dto.ResultDTO
import kotlin.jvm.Throws

interface BoardManager {

    /***
     * Start game for two teams with given name
     *
     * @return GameId
     * @throws IllegalArgumentException
     * @throws InvalidGameException
     * @throws TeamAlreadyPlayingException
     */
    @Throws(
        IllegalArgumentException::class,
        InvalidGameException::class,
        TeamAlreadyPlayingException::class
    )
    fun startGame(home: String?, away: String?): String

    /***
     * End game with given parameters
     * @param home home team name
     * @param away away team name
     *
     * @return final result of the game
     *
     * @throws IllegalArgumentException
     * @throws UnknownTeamException
     */
    @Throws(
        IllegalArgumentException::class,
        UnknownTeamException::class
    )
    fun endGame(home: String?, away: String?): ResultDTO

    /***
     * Update score of the game with given parameters
     * @param home home team name
     * @param away away team name
     * @param homeScore home team score
     * @param awayScore away team score
     *
     * @return current game result
     *
     * @throws IllegalArgumentException
     * @throws InvalidScoreException
     * @throws UnknownGameException
     */
    @Throws(
        IllegalArgumentException::class,
        InvalidScoreException::class,
        UnknownGameException::class
    )
    fun updateScore(home: String?, away: String?, homeScore: Int, awayScore: Int): ResultDTO

    /***
     * Get current capture of the board as a list of results
     *
     * @return list of current results
     * @throws IllegalArgumentException
     */
    @Throws(
        IllegalArgumentException::class
    )
    fun getBoardDescending(): List<ResultDTO>
}