package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * Request by administrative client to pause or unpause a game specified by given roomId.
 *
 * A game will only be paused immediately if there is no pending MoveRequest,
 * otherwise the game will be paused next turn.
 *
 * When the game is paused no GameState or MoveRequest will be sent to the players (and all other observers)
 * until an AdminClient sends a StepRequest or resumes the game.
 */
@XStreamAlias("pause")
data class PauseGameRequest(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamAsAttribute
        val pause: Boolean
): AdminLobbyRequest
