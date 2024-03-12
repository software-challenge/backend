package sc.shared

import sc.networking.clients.XStreamClient.DisconnectCause

sealed class Violation(override val message: String): IWinReason {
    override val isRegular = false
    
    /** The player left the game early (connection loss).  */
    class LEFT(cause: DisconnectCause): Violation("%s hat das Spiel verlassen: $cause")
    /** The player violated against the games rules.  */
    class RULE_VIOLATION(reason: InvalidMoveException): Violation("Regelverletzung von %s: ${reason.message}")
    /** The player violated against the game rder.  */
    class PROCESS_VIOLATION(reason: String): Violation("Missachtung des Spielablaufs von %s: $reason")
    /** The player took to long to respond to the move request.  */
    class SOFT_TIMEOUT(timeout: Number): Violation("%s hat fuer die Antwort auf die Zugaufforderung laenger als $timeout Sekunden gebraucht.")
    /** The player didn't respond to the move request.  */
    class HARD_TIMEOUT(timeout: Number): Violation("%s hat innerhalb von $timeout Sekunden nach Aufforderung keinen Zug gesendet.")
    /** An error occurred during communication. This could indicate a bug in the server's code.  */
    object UNKNOWN: Violation("")
}
