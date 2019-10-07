package sc.framework.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import org.slf4j.LoggerFactory
import sc.api.plugins.host.IPlayerListener
import sc.framework.plugins.protocol.MoveRequest
import sc.protocol.responses.ProtocolMessage
import sc.shared.PlayerColor
import java.util.*

private val logger = LoggerFactory.getLogger(Player::class.java)

open class Player(override val color: PlayerColor, override val displayName: String): PlayerName, Cloneable {
    
    public override fun clone() = Player(color, displayName)
    
    protected var listeners: MutableList<IPlayerListener> = ArrayList()
    
    var isCanTimeout: Boolean = false
    var isShouldBePaused: Boolean = false
    var violated = false
    fun hasViolated() = violated
    var left = false
    fun hasLeft() = left
    var softTimeout = false
    fun hasSoftTimeout() = softTimeout
    var hardTimeout = false
    fun hasHardTimeout() = hardTimeout
    var violationReason: String? = null
    
    fun addPlayerListener(listener: IPlayerListener) {
        this.listeners.add(listener)
    }
    
    fun removePlayerListener(listener: IPlayerListener) {
        this.listeners.remove(listener)
    }
    
    fun notifyListeners(o: ProtocolMessage) {
        for(listener in this.listeners) {
            listener.onPlayerEvent(o)
        }
    }
    
    open fun requestMove() {
        val request = MoveRequest()
        notifyListeners(request)
        logger.debug("Move requested from $this")
    }
    
}

@XStreamAlias(value = "player")
data class NamedPlayer @JvmOverloads constructor(
        @XStreamAsAttribute override val color: PlayerColor,
        @XStreamAsAttribute override val displayName: String = ""): PlayerName {
    override fun toString(): String = "Player %s(%s)".format(color, displayName)
}

interface PlayerName {
    val color: PlayerColor
    val displayName: String
}