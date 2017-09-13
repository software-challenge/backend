package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

@XStreamAlias("step")
public class StepRequest extends ProtocolMessage implements ILobbyRequest
{
	@XStreamAsAttribute
	public String	roomId;

	@XStreamAsAttribute
	public boolean	forced;

        /**
         * might be needed by XStream
         */
        public StepRequest() {
        }

	public StepRequest(String roomId)
	{
		this(roomId, false);
	}

	public StepRequest(String roomId, boolean forced)
	{
		this.roomId = roomId;
		this.forced = forced;
	}
}
