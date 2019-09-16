package sc.plugin2020.util

object Constants {

    const val ROUND_LIMIT = 30

    const val LOSE_SCORE = 0
    const val DRAW_SCORE = 1
    const val WIN_SCORE = 2
    const val GAME_STATS_ROUNDS = 0
    
    const val BOARD_SIZE = 9
    val FIELD_AMOUNT = fieldAmount((BOARD_SIZE + 1) / 2)
    fun fieldAmount(radius: Int): Int =
            if(radius==1) 1 else (radius-1) * 6 + fieldAmount(radius-1)
    /** A String representing the amount and type of starting pieces per player. */
    const val STARTING_PIECES = "QSSSGGBBAAA"
}
