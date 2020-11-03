package sc.plugin2021.util

import sc.plugin2021.Color

/**
 * Wird optional bei Validierung von Zügen zurückgegeben, falls ein Zug nicht valide ist.
 * MoveMistakes entstehen bei Zügen, die theoretisch möglich sein könnten,
 * es aber bei dem jeweiligen Spielstand nicht sind.
 */
enum class MoveMistake {
    WRONG_COLOR {
        override fun toString(): String = "Die Farbe des Zuges ist nicht an der Reihe"
        override fun toString(color: Color): String = "Farbe $color ist nicht an der Reihe"
    },
    NOT_IN_CORNER {
        override fun toString(): String = "Der erste Zug muss auf eine freie Ecke gesetzt werden"
        override fun toString(color: Color): String = "Farbe $color hat den ersten Zug nicht auf eine freie Ecke gesetzt"
    },
    NO_SHARED_CORNER {
        override fun toString(): String = "Alle Teile müssen ein vorheriges Teil gleicher Farbe über mindestens eine Ecke berühren"
        override fun toString(color: Color): String = "Farbe $color hat einen Stein nicht an die Ecke eines vorhandenen Teils gleicher Farbe gelegt"
    },
    WRONG_SHAPE {
        override fun toString(): String = "Der erste Zug muss den festgelegten Spielstein setzen"
        override fun toString(color: Color): String = "Farbe $color hat im ersten Zug den falschen Spielstein gewählt"
    },
    SKIP_FIRST_TURN {
        override fun toString(): String = "Der erste Zug muss einen Stein setzen"
        override fun toString(color: Color): String = "Farbe $color hat in der erstes Runde gepasst"
    },
    DUPLICATE_SHAPE {
        override fun toString(): String = "Der gewählte Stein wurde bereits gesetzt"
        override fun toString(color: Color): String = "Farbe $color hat einen bereits gelegten Stein erneut gelegt"
    },
    OUT_OF_BOUNDS {
        override fun toString(): String = "Der Spielstein passt nicht vollständig auf das Spielfeld"
        override fun toString(color: Color): String = "Farbe $color hat einen Stein nicht vollständig aufs Spielfeld gelegt"
    },
    OBSTRUCTED {
        override fun toString(): String = "Der Spielstein würde eine andere Farbe überlagern"
        override fun toString(color: Color): String = "Farbe $color hat einen Stein auf einen anderen Stein gelegt"
    },
    TOUCHES_SAME_COLOR {
        override fun toString(): String = "Der Spielstein berührt ein Feld gleicher Farbe"
        override fun toString(color: Color): String = "Farbe $color hat einen Stein neben einen Stein gleicher Farbe gelegt"
    };

    abstract override fun toString(): String
    abstract fun toString(color: Color): String
}