package sc.shared

import sc.api.plugins.IMove
import kotlin.RuntimeException

/** NOTE: It is important to have information about the move in the reason
 *  because that is the only place where the invalid move is logged */
class InvalidMoveException @JvmOverloads constructor(reason: String, @JvmField val move: IMove? = null):
        RuntimeException(reason + (if (move != null) "; move was $move" else ""))
