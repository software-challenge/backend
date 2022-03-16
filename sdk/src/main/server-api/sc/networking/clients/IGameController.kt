package sc.networking.clients

interface IGameController {
    fun step(force: Boolean = false)
    fun pause(pause: Boolean = true)
    fun unpause() = pause(false)
    fun cancel()
}