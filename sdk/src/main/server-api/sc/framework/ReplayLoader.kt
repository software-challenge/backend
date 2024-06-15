package sc.framework

import org.slf4j.LoggerFactory
import sc.api.plugins.IGameState
import sc.api.plugins.Team
import sc.networking.XStreamProvider
import sc.protocol.room.MementoMessage
import sc.protocol.room.RoomPacket
import sc.shared.GameResult
import java.io.EOFException
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 * Loads game information from any XML file (usually a replay).
 *
 * Only usable once!
 */
class ReplayLoader(inputStream: InputStream) {
    constructor(file: File): this(
        if(file.extension == "gz")
            GZIPInputStream(file.inputStream())
        else
            file.inputStream()
    )
    
    val stream = XStreamProvider.currentPlugin().createObjectInputStream(inputStream)
    
    fun loadHistory(exitCondition: (IGameState) -> Boolean = { false }): Pair<List<IGameState>, GameResult?> {
        val history: MutableList<IGameState> = ArrayList(50)
        var result: GameResult? = null
        try {
            while(true) {
                val message = stream.readObject()
                logger.trace("Adding packet to replay: {}", message)
                if(message !is RoomPacket)
                    throw IOException("Can't extract replay from $message")
                when(val msg = message.data) {
                    is MementoMessage -> {
                        history.add(msg.state)
                        if(exitCondition(msg.state))
                            break
                    }
                    
                    is GameResult -> result = msg
                    else -> logger.warn("Unknown message in replay: {}", msg)
                }
            }
        } catch(e: EOFException) {
            logger.info("Replay fully loaded")
        } finally {
            stream.close()
        }
        return history to result
    }
    
    fun getTurn(turn: Int): IGameState =
        loadHistory { it.turn >= turn }.first.last().takeIf { it.turn >= turn }
        ?: throw NoSuchElementException("No GameState of turn $turn")
    
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        
        fun invert(replay: String) =
            replay.replace(Regex("(ONE|TWO)")) {
                Team.valueOf(it.value).opponent().name
            }
    }
}