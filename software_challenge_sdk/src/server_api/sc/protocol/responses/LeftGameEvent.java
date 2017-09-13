package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value="left")
public class LeftGameEvent extends ProtocolMessage
{
	@XStreamAsAttribute
	private String	roomId;

        /**
         * might be needed by XStream
         */
        public LeftGameEvent() {
        }

	public LeftGameEvent(String id)
	{
		this.roomId = id;
	}

	public String getRoomId()
	{
		return this.roomId;
	}
}
