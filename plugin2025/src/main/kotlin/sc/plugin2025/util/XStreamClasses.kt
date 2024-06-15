package sc.plugin2025.util

import sc.networking.XStreamProvider
import sc.plugin2025.*

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
        listOf(
            Advance::class.java,
            Card::class.java,
            FallBack::class.java,
            EatSalad::class.java,
            ExchangeCarrots::class.java,
        )
    
}