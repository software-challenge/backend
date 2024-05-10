package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.plugin2025.GameRuleLogic.isValidToExchangeCarrots
import sc.shared.IMoveMistake

/**
 * Karottentauschaktion.
 * Auf einem Karottenfeld können 10 Karotten abgegeben oder aufgenommen werden.
 * Dies kann beliebig oft hintereinander ausgeführt werden.
 */
@XStreamAlias(value = "ExchangeCarrots")
data class ExchangeCarrots(val value: Int): HuIMove {
    override fun perform(state: GameState): IMoveMistake? {
        if(isValidToExchangeCarrots(state, this.value)) {
            state.currentPlayer.carrots += value
            return null
        } else {
            return MoveMistake.CANNOT_EXCHANGE_CARROTS
        }
    }
}
