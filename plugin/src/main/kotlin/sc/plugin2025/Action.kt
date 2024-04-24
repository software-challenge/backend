package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IMoveMistake

@XStreamAlias(value = "action")
interface Action {
    fun perform(state: GameState): IMoveMistake?
}

