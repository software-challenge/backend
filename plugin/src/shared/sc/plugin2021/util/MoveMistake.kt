package sc.plugin2021.util

import sc.shared.IMoveMistake

/**
 * Wird optional bei Validierung von Zügen zurückgegeben, falls ein Zug nicht valide ist.
 * MoveMistakes entstehen bei Zügen, die theoretisch möglich sein könnten,
 * es aber bei dem jeweiligen Spielstand nicht sind.
 */
enum class MoveMistake: IMoveMistake {
    WRONG_COLOR {
        override fun toString(): String = "Die Farbe des Zuges ist nicht an der Reihe"
    },
    NOT_IN_CORNER {
        override fun toString(): String = "Der erste Zug muss auf eine freie Ecke gesetzt werden"
    },
    NO_SHARED_CORNER {
        override fun toString(): String = "Alle Teile müssen ein vorheriges Teil gleicher Farbe über mindestens eine Ecke berühren"
    },
    WRONG_SHAPE {
        override fun toString(): String = "Der erste Zug muss den festgelegten Spielstein setzen"
    },
    SKIP_FIRST_TURN {
        override fun toString(): String = "Der erste Zug muss einen Stein setzen"
    },
    DUPLICATE_SHAPE {
        override fun toString(): String = "Der gewählte Stein wurde bereits gesetzt"
    },
    OUT_OF_BOUNDS {
        override fun toString(): String = "Der Spielstein passt nicht vollständig auf das Spielfeld"
    },
    OBSTRUCTED {
        override fun toString(): String = "Der Spielstein würde eine andere Farbe überlagern"
    },
    TOUCHES_SAME_COLOR {
        override fun toString(): String = "Der Spielstein berührt ein Feld gleicher Farbe"
    };
}