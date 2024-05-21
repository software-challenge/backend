package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IMove
import sc.shared.IMoveMistake

@XStreamAlias(value = "action")
interface Move: IMove {
    fun perform(state: GameState): IMoveMistake?
}

