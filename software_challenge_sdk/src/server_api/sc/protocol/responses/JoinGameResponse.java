package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * example of such a response:
 * <protocol>
 * 	<joined roomId="7dc299b1-dcd5-4854-8a02-90510754b943"/>
 *
 */
@XStreamAlias(value="joined")
public class JoinGameResponse
{
	@XStreamAsAttribute
	private String	roomId;

        /**
         * might be needed by XStream
         */
        public JoinGameResponse() {
        }

	public JoinGameResponse(String id)
	{
		this.roomId = id;
	}

	public String getRoomId()
	{
		return this.roomId;
	}
}
