package sc.plugin2027.util

import sc.plugin2027.Color

/** Eine Sammlung an verschiedenen Konstanten, die im Spiel verwendet werden. */
object Constants {
    /** Anzahl an verschiedenen Spielsteinen. */
    const val TOTAL_PIECE_SHAPES = 21
    
    /** Anzahl an im Spiel verfügbaren Farben. */
    val COLORS = Color.entries.size
    
    /** Die Anzahl an erlaubten Runden. */
    const val ROUND_LIMIT: Int = 25
    
    /** Die Länge des Spielfelds als Anzahl an Felders. */
    const val BOARD_LENGTH = 20
    
    /** Kontrolliert, ob Züge vor dem Setzen validiert werden. Existiert nur für Testzwecke. */
    const val VALIDATE_MOVE = true
}