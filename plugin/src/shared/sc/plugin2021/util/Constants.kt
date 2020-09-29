package sc.plugin2021.util

object Constants {
    const val TOTAL_PIECE_SHAPES = 21
    const val COLORS = 4
    
    const val LOSE_SCORE = 0
    const val DRAW_SCORE = 1
    const val WIN_SCORE  = 2
    
    const val BOARD_SIZE = 20
    
    const val SOFT_TIMEOUT = 2000L
    const val HARD_TIMEOUT = 10000L
    
    // Max game length: turns(ROUND_LIMIT * 2) * SOFT_TIMEOUT, one second buffer per round
    /** After this amount of milliseconds, the game should definitely have ended. */
    const val GAME_TIMEOUT = TOTAL_PIECE_SHAPES * COLORS * SOFT_TIMEOUT
    
    /** Used to turn off move validation. If turned off, the GameState tests are supposed to and *will* fail. */
    const val VALIDATE_MOVE = true
}