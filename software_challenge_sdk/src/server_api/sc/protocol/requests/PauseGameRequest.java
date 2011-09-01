package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("pause")
public class PauseGameRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	public String	roomId;
	
	@XStreamAsAttribute
	public boolean	pause;

        /**
         * might be needed by XStream
         */
        public PauseGameRequest() {
        }

	public PauseGameRequest(String roomId, boolean pause)
	{
		this.roomId = roomId;
		this.pause = pause;
	}
}
