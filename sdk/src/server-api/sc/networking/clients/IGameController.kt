package sc.networking.clients

interface IGameController {
    fun step()
    fun pause(pause: Boolean = true)
    fun unpause() = pause(false)
    fun cancel()
}