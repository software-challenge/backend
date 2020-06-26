package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * An additional note to be appended to a move.
 * These notes can then be displayed in the GUI if debugging is enabled.
 *
 * This enables faster debugging of clients and easier configuring of strategies as
 * important information gets displayed together with the corresponding move (instead of the CLI)
 * ===
 * Ein Debughinweis ist ein Container für einen String, der einem Zug beigefuegt
 * werden kann. Beigefuegte Debughints werden direkt in der grafischen
 * Oberflaeche des Plugins angezeigt, wenn die Debugansicht gewaehlt wurde.
 *
 * Dies ermöglicht das schnellere Debuggen von Clients und besseres
 * Konfigurieren von Strategien, denn es müssen keine Konsolenausgaben gesucht
 * werden und die Hinweise werden immer zum passenden Zug angezeigt.
 */
class DebugHint(@XStreamAsAttribute val content: String = "") : Cloneable {
    /**
     * This constructs a hint of the form "key = value"
     */
    constructor(key: String, value: String) : this("$key = $value")
}

