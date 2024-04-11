package board.exceptions

/**
 * Exception thrown when the game with given parameters
 * does not exist in the board
 */
class UnknownGameException(message: String) : Exception(message)