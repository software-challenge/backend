package sc.framework.plugins

/** Eine Sammlung an verschiedenen Konstanten, die im Spiel verwendet werden. */
object Constants {
    /** Punkte für eine Niederlage. */
    const val LOSE_SCORE = 0
    /** Punkte für einen Gleichstand. */
    const val DRAW_SCORE = 1
    /** Punkte für einen Sieg. */
    const val WIN_SCORE = 2
    
    /** Zeit (in ms), die für einen Zug zur Verfügung steht. */
    const val SOFT_TIMEOUT = 2_000
    /** Zeit (in ms), ab dem eine Zuganfrage abgebrochen wird. */
    const val HARD_TIMEOUT = 10_000
}