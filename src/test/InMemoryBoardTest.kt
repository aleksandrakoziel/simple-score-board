package test

import board.InMemoryBoard
import board.InMemoryBoardButJava
import board.exceptions.*
import org.junit.Assert
import org.junit.Test

class InMemoryBoardTest {

    // private val board: InMemoryBoard = InMemoryBoard()
    private val board: InMemoryBoardButJava = InMemoryBoardButJava()

    @Test
    fun `Should add a game to the board`() {
        // given
        val size = board.getScoreBoard().size

        // when
        board.addGame("France", "Germany")

        // then
        Assert.assertTrue(size < board.getScoreBoard().size)
    }

    @Test(expected = TeamAlreadyPlayingException::class)
    fun `Should not add a game to the board if the away team is already playing`() {
        // given
        board.addGame("Brazil", "Portugal")

        // when
        board.addGame("Portugal", "Spain")

        // then throws
    }

    @Test(expected = TeamAlreadyPlayingException::class)
    fun `Should not add a game to the board if the home team is already playing`() {
        // given
        board.addGame("England", "Scotland")

        // when
        board.addGame("England", "Ireland")

        // then throws
    }

    @Test(expected = InvalidGameException::class)
    fun `Should not add a game to the board if the home team and the away team are the same`() {
        // given
        // when
        board.addGame("Australia", "Australia")

        // then throws
    }

    @Test
    fun `Should return result of game when removed by game id`() {
        // given
        val id = board.addGame("Poland", "Slovakia")
        val size = board.getScoreBoard().size

        // when
        val result = board.removeGame(id)

        // then
        Assert.assertTrue(size > board.getScoreBoard().size)
        Assert.assertEquals("Poland", result.homeTeam)
        Assert.assertEquals("Slovakia", result.awayTeam)
        Assert.assertEquals(0, result.homeScore)
        Assert.assertEquals(0, result.awayScore)
    }

    @Test
    fun `Should return result of a game when removed by teams`() {
        // given
        board.addGame("Poland", "Slovakia")
        val size = board.getScoreBoard().size

        // when
        val result = board.removeGame("Poland", "Slovakia")

        // then
        Assert.assertTrue(size > board.getScoreBoard().size)
        Assert.assertEquals("Poland", result.homeTeam)
        Assert.assertEquals("Slovakia", result.awayTeam)
        Assert.assertEquals(0, result.homeScore)
        Assert.assertEquals(0, result.awayScore)
    }

    @Test(expected = UnknownTeamException::class)
    fun `Should not remove a non-existing game by teams`() {
        // given
        // when
        board.removeGame("Australia", "Bosnia")

        // then throws
    }

    @Test(expected = UnknownGameException::class)
    fun `Should not remove a non-existing game by game id`() {
        // given
        // when
        board.removeGame("whatever")

        // then throws
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Should not remove a game with id null`() {
        // given
        // when
        board.removeGame(null)

        // then throws
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Should not remove a game when one of the teams is null`() {
        // given
        // when
        board.removeGame(null, "Germany")

        // then throws
    }

    @Test
    fun `Should return game id by a team name for a home team`() {
        // given
        val id = board.addGame("Belgium", "RPA")

        // when
        val result = board.getGameIdByTeam("Belgium")

        // then
        Assert.assertEquals(id, result)
    }

    @Test
    fun `Should return game id by a team name for an away team`() {
        // given
        val id = board.addGame("Netherlands", "China")

        // when
        val result = board.getGameIdByTeam("China")

        // then
        Assert.assertEquals(id, result)
    }

    @Test(expected = UnknownTeamException::class)
    fun `Should not return game id when the team is not playing`() {
        // given

        // when
        board.getGameIdByTeam("Finland")

        // then throws
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Should not return game id when the team is null`() {
        // given

        // when
        board.getGameIdByTeam(null)

        // then throws
    }

    @Test
    fun `Should return a score board descending`() {
        // given
        board.clear()
        board.addGame("France", "Japan")
        board.addGame("Poland", "Germany")
        board.addGame("USA", "Panama")
        board.goal("Poland")

        // when
        val result = board.getScoreBoardDescending()

        // then
        result.forEach { println("${it.homeTeam} ${it.homeScore} - ${it.awayTeam} ${it.awayScore}") }
        println()
        Assert.assertEquals("Poland", result[0].homeTeam)
    }

    @Test
    fun `Should return a simple score board`() {
        // given
        board.clear()
        board.addGame("France", "Japan")
        board.addGame("Poland", "Germany")
        board.addGame("USA", "Panama")
        board.goal("Poland")

        // when
        val result = board.getScoreBoard()

        // then
        result.forEach { println("${it.homeTeam} - ${it.awayTeam}: ${it.homeScore} - ${it.awayScore}") }
        println()
        Assert.assertEquals("France", result[0].homeTeam)
    }

    @Test
    fun `Should return a score board and a score board descending with a given data`() {
        // given
        board.clear()
        listOf(
            listOf("Mexico", "Canada", "0", "5"),
            listOf("Uruguay", "Italy", "6", "6"),
            listOf("Argentina", "Australia", "3", "1"),
            listOf("Spain", "Brazil", "10", "2"),
            listOf("Germany", "France", "2", "2")
        ).forEach {
            board.updateScore(
                board.addGame(it[0], it[1]),
                it[2].toInt(),
                it[3].toInt()
            )
        }

        // when
        val resultDescending = board.getScoreBoardDescending()
        val result = board.getScoreBoard()

        // then
        println("ORIGINAL BOARD")
        result.forEach { println("${it.homeTeam} - ${it.awayTeam}: ${it.homeScore} - ${it.awayScore}") }
        println()
        println("DESCENDING BOARD")
        resultDescending.forEach { println("${it.homeTeam} ${it.homeScore} - ${it.awayTeam} ${it.awayScore}") }
        println()

        Assert.assertEquals("Uruguay", resultDescending[0].homeTeam)
        Assert.assertEquals("Mexico", result[0].homeTeam)
    }

    @Test
    fun `Should not throw the exception when attempting to get an empty board`() {
        // given
        board.clear()

        // when
        val result = board.getScoreBoard()

        // then
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun `Should not throw the exception when attempting to get and to sort an empty board`() {
        // given
        board.clear()

        // when
        val result = board.getScoreBoardDescending()

        // then
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun `Should update score with a result and a game id`() {
        // given
        val gameId = board.addGame("Greece", "South Korea")

        // when
        board.updateScore(gameId, 5, 4)
        val result = board.removeGame(gameId)

        // then
        Assert.assertEquals(5, result.homeScore)
        Assert.assertEquals(4, result.awayScore)
    }

    @Test(expected = UnknownGameException::class)
    fun `Should not update a score of a non-existing game`() {
        // given
        // when
        board.updateScore("Tomato-Cucumber", 5, 4)

        // then throws
    }

    @Test(expected = InvalidScoreException::class)
    fun `Should not update a score with negative value of the home team score`() {
        // given
        val gameId = board.addGame("Morocco", "Argentina")

        // when
        board.updateScore(gameId, -4, 3)

        // then throws
    }

    @Test(expected = InvalidScoreException::class)
    fun `Should not update a score with negative value of the away team score`() {
        // given
        val gameId = board.addGame("Hungary", "Faroe Islands")

        // when
        board.updateScore(gameId, 2, -5)

        // then throws
    }

    @Test
    fun `Should add a goal for a home team`() {
        // given
        val gameId = board.addGame("Slovenia", "Kongo")

        // when
        board.goal("Slovenia")
        val result = board.getCurrentResult(gameId)
        // then
        Assert.assertEquals("Slovenia", result.homeTeam)
        Assert.assertEquals(1, result.homeScore)
    }

    @Test
    fun `Should add a goal for an away team`() {
        // given
        val gameId = board.addGame("Sweden", "Moldova")

        // when
        board.goal("Moldova")
        val result = board.getCurrentResult(gameId)
        // then
        Assert.assertEquals("Moldova", result.awayTeam)
        Assert.assertEquals(1, result.awayScore)
    }

    @Test(expected = UnknownTeamException::class)
    fun `Should not add a goal for a team which is not playing right now`() {
        // given
        // when
        board.goal("Mordor")
        // then throws
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Should not add a goal for a null team`() {
        // given
        // when
        board.goal(null)
        // then throws
    }

    @Test
    fun `Should revoke a goal for a home team`() {
        // given
        val gameId = board.addGame("Russia", "Kazakhstan")
        board.goal("Russia")

        // when
        board.revokeGoal("Russia")
        val result = board.getCurrentResult(gameId)

        // then
        Assert.assertEquals("Russia", result.homeTeam)
        Assert.assertEquals(0, result.homeScore)
    }

    @Test
    fun `Should revoke a goal for an away team`() {
        // given
        val gameId = board.addGame("Nepal", "Gambia")
        board.goal("Gambia")

        // when
        board.revokeGoal("Gambia")
        val result = board.getCurrentResult(gameId)

        // then
        Assert.assertEquals("Gambia", result.awayTeam)
        Assert.assertEquals(0, result.awayScore)
    }

    @Test(expected = UnknownTeamException::class)
    fun `Should not revoke a goal for a team which is not playing right now`() {
        // given
        // when
        board.revokeGoal("Shire")
        // then throws
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Should not revoke a goal for a null team`() {
        // given
        // when
        board.revokeGoal(null)
        // then throws
    }

    @Test(expected = InvalidScoreException::class)
    fun `Should not revoke a goal for a team without any goals`() {
        // given
        board.addGame("India", "Columbia")
        // when
        board.revokeGoal("Columbia")
        // then throws
    }

}