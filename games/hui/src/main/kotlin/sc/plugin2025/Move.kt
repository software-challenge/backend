package sc.plugin2025

import sc.api.plugins.IMove
import sc.shared.IMoveMistake

interface Move: IMove, HuIAction

interface HuIAction {
    fun perform(state: GameState): IMoveMistake?
}

