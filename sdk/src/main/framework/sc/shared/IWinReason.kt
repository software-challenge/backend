package sc.shared

interface IWinReason {
    val isRegular: Boolean
    val message: String
    
    fun getMessage(playerName: String?): String =
        message.format(playerName)
}

open class WinReason(override val message: String, override val isRegular: Boolean = true): IWinReason {
    override fun equals(other: Any?) = other is IWinReason && other.message == this.message
}

object WinReasonTie: WinReason("Beide Teams sind gleichauf")