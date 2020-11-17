package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.shared.SlotDescriptor

/** Request to prepare a game of [gameType] with two reserved slots according to [slotDescriptors]. */
@XStreamAlias("prepare")
data class PrepareGameRequest(
    @XStreamAsAttribute
    val gameType: String,
    @XStreamImplicit(itemFieldName = "slot")
    val slotDescriptors: Array<SlotDescriptor>,
    @XStreamAsAttribute
    val pause: Boolean
): AdminLobbyRequest {
    
    /**
     * Create a prepared game with descriptors for each player.
     * The player descriptors default to "Player1" and "Player2".
     *
     * @param gameType type of the game (plugin id)
     * @param pause whether the game should start paused - default true
     */
    @JvmOverloads
    constructor(
        gameType: String,
        descriptor1: SlotDescriptor = SlotDescriptor("Player1"),
        descriptor2: SlotDescriptor = SlotDescriptor("Player2"),
        pause: Boolean = true
    ): this(gameType, arrayOf(descriptor1, descriptor2), pause)
    
    override fun equals(other: Any?): Boolean =
        other is PrepareGameRequest && gameType == other.gameType && pause == other.pause && slotDescriptors.contentDeepEquals(other.slotDescriptors)
    
    override fun hashCode(): Int {
        var result = gameType.hashCode()
        result = 31 * result + slotDescriptors.contentHashCode()
        result = 31 * result + pause.hashCode()
        return result
    }
    
}
