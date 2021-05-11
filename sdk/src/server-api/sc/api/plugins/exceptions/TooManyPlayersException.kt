package sc.api.plugins.exceptions

class TooManyPlayersException: IllegalStateException("Attempted to join an already full game")
