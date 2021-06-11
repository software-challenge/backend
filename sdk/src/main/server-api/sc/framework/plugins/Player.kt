package sc.framework.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.LoggerFactory
import sc.api.plugins.ITeam
import sc.api.plugins.host.IPlayerListener
import sc.framework.plugins.protocol.MoveRequest
import sc.protocol.room.RoomMessage

private val logger = LoggerFactory.getLogger(Player::class.java)

/**
 * Keeps information about a player:
 * - basic info: name and color
 * - state info: if they can time out, whether their game is paused
 * - game result info: left & timeouts, to determine the winner and potential violation information
 * - listeners: subscribers that get notified about new messages concerning this player, notably Welcome and Errors
 *
 * Note: the toString/equals/hashCode/clone methods only take [team] and [displayName] into account!
 */
// TODO split this beast up!
@XStreamAlias(value = "player")
open class Player @JvmOverloads constructor(
        @XStreamAsAttribute var team: ITeam,
        @XStreamAsAttribute var displayName: String = ""
): Cloneable {
    
    @XStreamOmitField
    protected var listeners: MutableList<IPlayerListener> = ArrayList()

    @XStreamOmitField
    var canTimeout: Boolean = false

    @XStreamOmitField
    var left = false

    fun hasLeft() = left

    @XStreamOmitField
    var softTimeout = false

    fun hasSoftTimeout() = softTimeout

    @XStreamOmitField
    var hardTimeout = false

    fun hasHardTimeout() = hardTimeout
    
    fun hasViolated() = violationReason != null
    
    @XStreamOmitField
    var violationReason: String? = null

    fun addPlayerListener(listener: IPlayerListener) {
        this.listeners.add(listener)
    }

    fun removePlayerListener(listener: IPlayerListener) {
        this.listeners.remove(listener)
    }

    fun notifyListeners(o: RoomMessage) {
        for (listener in ArrayList(listeners)) {
            listener.onPlayerEvent(o)
        }
    }

    open fun requestMove() {
        val request = MoveRequest()
        notifyListeners(request)
        logger.debug("Move requested from $this")
    }

    override fun toString(): String = "%s(%s)".format(team, displayName)
    
    public override fun clone() = Player(team, displayName)
    
    override fun equals(other: Any?) = other is Player && other.team == team && other.displayName == displayName
    
    override fun hashCode(): Int {
        var result = team.hashCode()
        result = 31 * result + displayName.hashCode()
        return result
    }
    
    fun longString() =
            "Player(team=$team, displayName='$displayName', listeners=$listeners, canTimeout=$canTimeout, left=$left, softTimeout=$softTimeout, hardTimeout=$hardTimeout, violationReason=$violationReason)"
    
}
