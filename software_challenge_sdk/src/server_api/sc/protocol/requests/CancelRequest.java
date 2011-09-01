package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("cancel")
public class CancelRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	public String	roomId;

        /**
         * might be needed by XStream
         */
        public CancelRequest() {
        }

	public CancelRequest(String roomId)
	{
		this.roomId = roomId;
	}
}
