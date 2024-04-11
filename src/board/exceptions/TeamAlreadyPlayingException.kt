package board.exceptions

/**
 * Exception thrown when the team is already added to the board
 * Team cannot play two games at the same time
 */
class TeamAlreadyPlayingException(message: String) : Exception(message)