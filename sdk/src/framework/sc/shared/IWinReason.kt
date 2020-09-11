package sc.shared

interface IWinReason {
    val message: String
    
    fun getMessage(playerName: String?): String
}
