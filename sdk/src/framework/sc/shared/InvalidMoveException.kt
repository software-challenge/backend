package sc.shared

import sc.api.plugins.IMove
import kotlin.RuntimeException

/**
 * Wird bei Zügen geworfen, die nicht regelkonform ausgeführt werden können.
 *
 * @param mistake ist die Art der Regelverletzung.
 * @param move ist der Zug, der den Fehler verursacht hat.
 */
class InvalidMoveException @JvmOverloads constructor(
        @JvmField val mistake: IMoveMistake,
        @JvmField val move: IMove? = null):
        RuntimeException("${mistake.message}${move?.let { " bei Zug '$it'"}.orEmpty()}")