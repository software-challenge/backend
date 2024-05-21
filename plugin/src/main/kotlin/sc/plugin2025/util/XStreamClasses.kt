package sc.plugin2025.util

import sc.networking.XStreamProvider
import sc.plugin2025.Move

class XStreamClasses: XStreamProvider {
    
    override val classesToRegister =
            listOf(
                    Move::class.java
            )
    
}