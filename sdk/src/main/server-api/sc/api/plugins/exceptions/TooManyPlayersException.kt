package sc.api.plugins.exceptions

class TooManyPlayersException(game: Any? = null):
        IllegalStateException("Attempted to join an already full game${game?.let { ": $it" }}")
