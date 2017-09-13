package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value="observed")
public class ObservationProtocolMessage extends ProtocolMessage
{
	@XStreamAsAttribute
	private String	roomId;

        /**
         * might be needed by XStream
         */
        public ObservationProtocolMessage() {
        }


	public ObservationProtocolMessage(String roomId)
	{
		this.roomId = roomId;
	}

	public String getRoomId()
	{
		return this.roomId;
	}
}
