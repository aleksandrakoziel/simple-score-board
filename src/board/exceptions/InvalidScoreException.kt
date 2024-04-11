package board.exceptions

/**
 * Exception thrown when the given score cannot be set
 * due to invalid format or other ongoing operations
 */
class InvalidScoreException(message: String) : Exception(message)