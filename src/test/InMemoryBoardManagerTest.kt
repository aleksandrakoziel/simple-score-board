package test

import board.BoardManager
import board.InMemoryBoard
import board.InMemoryBoardButJava
import board.InMemoryBoardManager
import board.exceptions.UnknownGameException
import org.junit.Assert
import org.junit.Test

// more detailed tests are part of the InMemoryBoard test suite
class InMemoryBoardManagerTest {
    // private val boardManager: BoardManager = InMemoryBoardManager(InMemoryBoard())
    private val boardManager: BoardManager = InMemoryBoardManager(InMemoryBoardButJava())

    @Test
    fun `Should start a game with manager`() {
        // given
        val size = boardManager.getBoardDescending().size
        // when
        boardManager.startGame("Italy", "Portugal")

        // then
        Assert.assertTrue(size < boardManager.getBoardDescending().size)

        //cleanup
        boardManager.endGame("Italy", "Portugal")
    }

    @Test
    fun `Should end a game by given teams`() {
        // given
        boardManager.startGame("Ukraine", "RPA")
        val size = boardManager.getBoardDescending().size

        // when
        val result = boardManager.endGame("Ukraine", "RPA")

        // then
        Assert.assertTrue(size > boardManager.getBoardDescending().size)
        Assert.assertEquals("Ukraine", result.homeTeam)
        Assert.assertEquals("RPA", result.awayTeam)
        Assert.assertEquals(0, result.homeScore)
        Assert.assertEquals(0, result.awayScore)
    }

    @Test
    fun `Should update a game by given data`() {
        // given
        boardManager.startGame("Latvia", "Malta")

        // when
        val result = boardManager.updateScore("Latvia", "Malta", 3, 0)

        // then
        Assert.assertEquals("Latvia", result.homeTeam)
        Assert.assertEquals("Malta", result.awayTeam)
        Assert.assertEquals(3, result.homeScore)
        Assert.assertEquals(0, result.awayScore)

        //cleanup
        boardManager.endGame("Latvia", "Malta")
    }

    @Test(expected = UnknownGameException::class)
    fun `Should not update a game when two teams are not playing with each other`() {
        // given
        boardManager.startGame("Honduras", "SaudiArabia")
        boardManager.startGame("Greece", "Iran")

        // when
        boardManager.updateScore("Honduras", "Iran", 5, 0)

        // then throws
    }

    @Test
    fun `Should return a score board and a score board descending with a given data`() {
        // given
        listOf(
            listOf("Mexico", "Canada", "0", "5"),
            listOf("Uruguay", "Italy", "6", "6"),
            listOf("Argentina", "Australia", "3", "1"),
            listOf("Spain", "Brazil", "10", "2"),
            listOf("Germany", "France", "2", "2")
        ).forEach {
            boardManager.startGame(it[0], it[1])
            boardManager.updateScore(
                it[0],
                it[1],
                it[2].toInt(),
                it[3].toInt()
            )
        }

        // when
        val resultDescending = boardManager.getBoardDescending()

        // then
        println("DESCENDING BOARD")
        resultDescending.forEach { println("${it.homeTeam} ${it.homeScore} - ${it.awayTeam} ${it.awayScore}") }
        println()

        Assert.assertEquals("Uruguay", resultDescending[0].homeTeam)
    }
}