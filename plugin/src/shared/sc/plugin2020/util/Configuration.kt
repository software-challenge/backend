package sc.plugin2020.util

import com.thoughtworks.xstream.XStream
import sc.plugin2020.*
import sc.protocol.LobbyProtocol
import sc.shared.WelcomeMessage
import java.util.concurrent.locks.Condition

object Configuration {
    @JvmStatic
    var xStream: XStream
        private set

    @JvmStatic
    val classesToRegister: List<Class<*>>
        get() = listOf(Game::class.java, Board::class.java,
                GameState::class.java, SetMove::class.java, DragMove::class.java, SkipMove::class.java,
                Direction::class.java, Field::class.java,
                WelcomeMessage::class.java, Condition::class.java)

    init {
        xStream = XStream()
        xStream.setMode(XStream.NO_REFERENCES)
        xStream.classLoader = Configuration::class.java.classLoader
        LobbyProtocol.registerMessages(xStream)
        LobbyProtocol.registerAdditionalMessages(xStream, classesToRegister)
    }
}
