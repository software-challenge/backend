package sc.plugin2022.util

import sc.plugin2022.Color

/** Eine Sammlung an verschiedenen Konstanten, die im Spiel verwendet werden. */
object Constants {
    /** Punkte für eine Niederlage. */
    const val LOSE_SCORE = 0
    /** Punkte für einen Gleichstand. */
    const val DRAW_SCORE = 1
    /** Punkte für einen Sieg. */
    const val WIN_SCORE = 2
    
    /** Die Länge des Spielfelds als Anzahl an Felders. */
    const val BOARD_SIZE = 8
    val boardrange = 0 until BOARD_SIZE

    /** Die Anzahl an erlaubten Runden. */
    const val ROUND_LIMIT = 30
    
    /** Zeit (in ms), die für einen Zug zur Verfügung steht. */
    const val SOFT_TIMEOUT = 2_000
    /** Zeit (in ms), ab dem eine Zuganfrage abgebrochen wird. */
    const val HARD_TIMEOUT = 10_000

    // Max game length: turns(=ROUND_LIMIT*2) * SOFT_TIMEOUT, one second buffer per round
    /** Zeit (in ms), die ein Spiel höchstens dauern sollte. */
    @JvmField
    val GAME_TIMEOUT = ROUND_LIMIT * 2 * SOFT_TIMEOUT

    /** Kontrolliert, ob Züge vor dem Setzen validiert werden. Existiert nur für Testzwecke. */
    const val VALIDATE_MOVE = true
}