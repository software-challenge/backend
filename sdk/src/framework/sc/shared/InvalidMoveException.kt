package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IMove
import kotlin.RuntimeException

/** NOTE: It is important to have information about the move in the reason
 *  because that is the only place where the invalid move is logged */
class InvalidMoveException @JvmOverloads constructor(
        @JvmField val mistake: IMoveMistake,
        @JvmField val move: IMove? = null):
        RuntimeException("$mistake${move?.let { "; move was $it"}.orEmpty()}")
