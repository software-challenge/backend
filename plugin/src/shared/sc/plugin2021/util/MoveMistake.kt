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

class WrongColor(color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color ist nicht an der Reihe"
}
class NotInCorner(val move: Move): MoveMistake(move.color) {
    override fun toString(): String = "Farbe $color hat den ersten Zug nicht auf eine freie Ecke gesetzt"
}
class NoSharedCorner(val move: Move): MoveMistake(move.color) {
    override fun toString(): String = "Farbe $color hat einen Stein nicht an die Ecke eines vorhandenen Teils gleicher Farbe gelegt"
}
class WrongShape(val shape: PieceShape, color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color hat im ersten Zug den falschen Spielstein gewählt"
}
class SkipFirstTurn(color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color hat in der erstes Runde gepasst"
}
class DuplicateShape(val shape: PieceShape, color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color hat einen bereits gelegten Stein erneut gelegt"
}
class OutOfBounds(val move: Move): MoveMistake(move.color) {
    override fun toString(): String = "Farbe $color hat einen Stein nicht vollständig aufs Spielfeld gelegt"
}
class Obstructed(val move: Move): MoveMistake(move.color) {
    override fun toString(): String = "Farbe $color hat einen Stein auf einen anderen Stein gelegt"
}
class TouchesSameColor(color: Color): MoveMistake(color) {
    override fun toString(): String = "Farbe $color hat einen Stein neben einen Stein gleicher Farbe gelegt"
}
