package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.shared.IMoveMistake

/**
 * Karottentauschaktion.
 * Auf einem Karottenfeld können 10 Karotten abgegeben oder aufgenommen werden.
 * Dies kann beliebig oft hintereinander ausgeführt werden.
 */
@XStreamAlias(value = "exchangecarrots")
data class ExchangeCarrots(@XStreamAsAttribute val amount: Int): Move {
    override fun perform(state: GameState): IMoveMistake? {
        if(state.mayExchangeCarrots(this.amount)) {
            state.currentPlayer.carrots += amount
            return null
        } else {
            return HuIMoveMistake.CANNOT_EXCHANGE_CARROTS
        }
    }
    
    override fun toString(): String = "Karottenvorrat um $amount aendern"
}
