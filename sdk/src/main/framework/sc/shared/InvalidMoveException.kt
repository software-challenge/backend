package sc.shared

import sc.api.plugins.IMove

/**
 * Wird bei Zügen geworfen, die nicht regelkonform ausgeführt werden können.
 *
 * @param mistake ist die Art der Regelverletzung.
 * @param move ist der Zug, der den Fehler verursacht hat.
 */
data class InvalidMoveException @JvmOverloads constructor(
        val mistake: IMoveMistake,
        val move: IMove? = null):
        RuntimeException("${mistake.message}${move?.let { " bei '$it'"}.orEmpty()}")