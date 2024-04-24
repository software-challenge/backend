package sc.plugin2025.util

import sc.networking.XStreamProvider
import sc.plugin2025.*

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
            listOf(
                    Move::class.java
            )
    
}