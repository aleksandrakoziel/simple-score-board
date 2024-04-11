package board.exceptions

/**
 * Exception thrown when the team with given parameters
 * is not added to the board
 */
class UnknownTeamException(message: String) : Exception(message)