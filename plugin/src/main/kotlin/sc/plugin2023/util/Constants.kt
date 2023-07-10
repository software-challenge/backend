package sc.plugin2023.util

import sc.framework.plugins.Constants

/** Eine Sammlung an verschiedenen Konstanten, die im Spiel verwendet werden. */
object PluginConstants {
    /** Die Länge des Spielfelds als Anzahl an Felders. */
    const val BOARD_SIZE = 8
    val boardrange = 0 until BOARD_SIZE

    // Max game length: Tiles * SOFT_TIMEOUT, one second buffer per round
    /** Zeit (in ms), die ein Spiel höchstens dauern sollte. */
    @JvmField
    val GAME_TIMEOUT = BOARD_SIZE * BOARD_SIZE * Constants.SOFT_TIMEOUT

    /** Anzahl der Pinguine pro Spieler. */
    const val PENGUINS = 4
}