package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Is sent by one party immediately before this party closes the communication
 * connection and should make the receiving party also close the connection.
 */
@XStreamAlias(value="close")
public class CloseConnection extends ProtocolMessage
{

}
