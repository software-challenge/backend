package sc.plugin2025

import sc.api.plugins.IMove
import sc.shared.IMoveMistake

interface Move: IMove {
    fun perform(state: GameState): IMoveMistake?
}

