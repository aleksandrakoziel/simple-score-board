package board.exceptions

/**
 * Exception thrown when the game with given parameters
 * cannot be created due to wrong initial values
 */
class InvalidGameException(message: String) : Exception(message)