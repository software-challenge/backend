package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("observe")
public class ObservationRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	private String	roomId;
        /**
         * might be needed by XStream
         */
        public ObservationRequest() {
        }



	public ObservationRequest(String roomId)
	{
		this.roomId = roomId;
	}
	
	public String getRoomId()
	{
		return this.roomId;
	}


}
