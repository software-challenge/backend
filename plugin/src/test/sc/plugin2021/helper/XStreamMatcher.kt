package sc.plugin2021.helper

import sc.helpers.checkSerialization
import sc.plugin2021.GamePlugin

infix fun <T : Any> T.shouldSerializeTo(serialized: String) =
    checkSerialization(GamePlugin.loadXStream(), this, serialized)
