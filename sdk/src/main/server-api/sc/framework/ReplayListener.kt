package sc.framework

import sc.networking.XStreamProvider
import java.io.IOException
import java.io.Writer

class ReplayListener<T>(private val history: MutableList<T> = ArrayList()) {
    
    fun addMessage(message: T) =
            history.add(message)
    
    /** Write replay of game to a writer.  */
    @Throws(IOException::class)
    fun saveReplay(writer: Writer) {
        val xStream = XStreamProvider.allPlugins()
        writer.write("<protocol>\n")
        for (element in history) {
            // TODO do we need to save RoomPackets?
            writer.write("${xStream.toXML(element)}\n")
            writer.flush()
        }
        writer.write("</protocol>")
        writer.close()
    }
}