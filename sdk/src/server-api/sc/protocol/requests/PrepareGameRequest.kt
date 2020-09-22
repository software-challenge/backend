package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.shared.SlotDescriptor

/** Request to prepare a game of [gameType] with two reserved slots according to [slotDescriptors]. */
@XStreamAlias("prepare")
class PrepareGameRequest(
        @XStreamAsAttribute
        val gameType: String,
        @XStreamImplicit(itemFieldName = "slot")
        val slotDescriptors: Array<SlotDescriptor>
): AdminLobbyRequest {
    
    /**
     * Create a prepared game with descriptors for each player.
     * The player descriptors default to "Player1" and "Player2".
     *
     * @param gameType    type of the game (plugin id)
     * @param descriptor1 descriptor for Player 1
     * @param descriptor2 descriptor for Player 2
     */
    @JvmOverloads constructor(
            gameType: String,
            descriptor1: SlotDescriptor = SlotDescriptor("Player1"),
            descriptor2: SlotDescriptor = SlotDescriptor("Player2")
    ): this(gameType, arrayOf(descriptor1, descriptor2))
    
    override fun toString() =
            "PrepareGameRequest(gameType='$gameType', slotDescriptors=${slotDescriptors.contentToString()})"
    
}