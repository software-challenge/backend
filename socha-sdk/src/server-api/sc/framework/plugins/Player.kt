package sc.framework.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.LoggerFactory
import sc.api.plugins.host.IPlayerListener
import sc.framework.plugins.protocol.MoveRequest
import sc.protocol.responses.ProtocolMessage
import sc.shared.Team
import java.util.*

private val logger = LoggerFactory.getLogger(Player::class.java)

@XStreamAlias(value = "player")
open class Player @JvmOverloads constructor(
        @XStreamAsAttribute var color: Team,
        @XStreamAsAttribute var displayName: String = "") : Cloneable {
    
    public override fun clone() = Player(color, displayName)
    
    override fun equals(other: Any?) = other is Player && other.color == color && other.displayName == displayName

    @XStreamOmitField
    protected var listeners: MutableList<IPlayerListener> = ArrayList()

    @XStreamOmitField
    var isCanTimeout: Boolean = false

    @XStreamOmitField
    var isShouldBePaused: Boolean = false

    @XStreamOmitField
    var violated = false

    fun hasViolated() = violated

    @XStreamOmitField
    var left = false

    fun hasLeft() = left

    @XStreamOmitField
    var softTimeout = false

    fun hasSoftTimeout() = softTimeout

    @XStreamOmitField
    var hardTimeout = false

    fun hasHardTimeout() = hardTimeout

    /** @return Reason for violation
     */
    @XStreamOmitField
    var violationReason: String? = null

    fun addPlayerListener(listener: IPlayerListener) {
        this.listeners.add(listener)
    }

    fun removePlayerListener(listener: IPlayerListener) {
        this.listeners.remove(listener)
    }

    fun notifyListeners(o: ProtocolMessage) {
        for (listener in this.listeners) {
            listener.onPlayerEvent(o)
        }
    }

    open fun requestMove() {
        val request = MoveRequest()
        notifyListeners(request)
        logger.debug("Move requested from $this")
    }

    override fun toString(): String = "Player %s(%s)".format(color, displayName)

}
