package sc.networking.clients

import org.slf4j.LoggerFactory
import sc.api.plugins.IGameState
import sc.networking.FileSystemInterface
import sc.protocol.ProtocolPacket
import sc.protocol.room.MementoMessage
import sc.protocol.room.RoomPacket
import sc.shared.GameResult
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * This client serves the purpose to load a game state from
 * any XML file (a replay for example).
 * It is used to load a given board to play on.
 */
class GameLoaderClient(inputStream: InputStream): XStreamClient(FileSystemInterface(inputStream)) {
    constructor(file: File): this(file.inputStream())
    
    private val history: MutableList<IGameState> = ArrayList(50)
    var result: GameResult? = null
        private set
    
    override fun onObject(message: ProtocolPacket) {
        logger.trace("Adding packet to replay: {}", message)
        if (message !is RoomPacket)
            throw IOException("Can't extract replay from $message")
        when (val msg = message.data) {
            is MementoMessage -> history.add(msg.state)
            is GameResult -> result = msg
            else -> logger.debug("Unknown message in replay: {}", msg)
        }
    }
    
    fun getHistory(): List<IGameState> {
        start()
        while (!isClosed)
            Thread.sleep(100)
        return history
    }
    
    fun getTurn(turn: Int) =
        getHistory().first {
            it.turn >= turn
        }
    
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}