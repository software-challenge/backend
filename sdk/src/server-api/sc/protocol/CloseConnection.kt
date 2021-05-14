package sc.protocol

import com.thoughtworks.xstream.annotations.XStreamAlias

/**
 * Is sent by one party immediately before this party closes the communication
 * connection and should make the receiving party also close the connection.
 *
 * This should not be sent manually, the XStreamClient will automatically send
 * it when stopped.
 */
@XStreamAlias(value = "close")
class CloseConnection: ProtocolPacket {
    override fun toString(): String = javaClass.simpleName
}
