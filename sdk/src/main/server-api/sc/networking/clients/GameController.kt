package sc.networking.clients

import sc.protocol.requests.CancelRequest
import sc.protocol.requests.PauseGameRequest
import sc.protocol.requests.StepRequest

class GameController(private val roomId: String, private val client: XStreamClient): IGameController {
    override fun step(force: Boolean) = client.send(StepRequest(roomId, force))
    override fun pause(pause: Boolean) = client.send(PauseGameRequest(roomId, pause))
    override fun cancel() = client.send(CancelRequest(roomId))
}