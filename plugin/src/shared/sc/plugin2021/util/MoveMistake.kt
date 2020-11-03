package sc.plugin2021.util

import sc.plugin2021.Color
import sc.plugin2021.Move
import sc.plugin2021.PieceShape

/**
 * Wird optional bei Validierung von Zügen zurückgegeben, falls ein Zug nicht valide ist.
 * MoveMistakes entstehen bei Zügen, die theoretisch möglich sein könnten,
 * es aber bei dem jeweiligen Spielstand nicht sind.
 */
sealed class MoveMistake(
        val color: Color
)
/** Die Farbe des Zuges ist nicht an der Reihe. */
class WrongColor(color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color ist nicht an der Reihe"
}
/** Der erste Zug muss auf eine freie Ecke gesetzt werden. */
class NotInCorner(val move: Move): MoveMistake(move.color) {
    override fun toString(): String = "Farbe $color hat den ersten Zug nicht auf eine freie Ecke gesetzt"
}
/** Alle Teile müssen ein vorheriges Teil gleicher Farbe über mindestens eine Ecke berühren. */
class NoSharedCorner(val move: Move): MoveMistake(move.color) {
    override fun toString(): String = "Farbe $color hat einen Stein nicht an die Ecke eines vorhandenen Teils gleicher Farbe gelegt"
}
/** Der erste Zug muss den festgelegten Spielstein setzen. */
class WrongShape(val shape: PieceShape, color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color hat im ersten Zug den falschen Spielstein gewählt"
}
/** Der erste Zug muss einen Stein setzen. */
class SkipFirstTurn(color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color hat in der erstes Runde gepasst"
}
/** Der gewählte Stein wurde bereits gesetzt. */
class DuplicateShape(val shape: PieceShape, color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color hat einen bereits gelegten Stein erneut gelegt"
}
/** Der Spielstein passt nicht vollständig auf das Spielfeld. */
class OutOfBounds(val move: Move): MoveMistake(move.color) {
    override fun toString(): String = "Farbe $color hat einen Stein nicht vollständig aufs Spielfeld gelegt"
}
/** Der Spielstein würde eine andere Farbe überlagern. */
class Obstructed(val move: Move): MoveMistake(move.color) {
    override fun toString(): String = "Farbe $color hat einen Stein auf einen anderen Stein gelegt"
}
/** Der Spielstein berührt ein Feld gleicher Farbe. */
class TouchesSameColor(color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color hat einen Stein neben einen Stein gleicher Farbe gelegt"
}
