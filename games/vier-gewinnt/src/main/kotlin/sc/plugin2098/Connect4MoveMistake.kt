package sc.plugin2098

import sc.shared.IMoveMistake

/** Spielspezifische Zug-Fehler. Siehe auch [sc.shared.MoveMistake]. */
enum class Connect4MoveMistake(override val message: String) : IMoveMistake {
    DESTINATION_BLOCKED("Zielfeld ist besetzt"),
    DESTINATION_IN_AIR("Kann nicht auf ein Feld, mit leeren Feldern zwischen dem untersten Plätchen oder dem unteren Rand, legen"),
    DESTINATION_OUT_OF_BOUNDS("Das Zielfeld liegt außerhalb des Spielfelds");
}
