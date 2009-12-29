package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("observe")
public class ObservationRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	private String	roomId;
	
	@XStreamAsAttribute
	private String	passphrase;

	public ObservationRequest(String roomId, String passphrase)
	{
		this.roomId = roomId;
		this.passphrase = passphrase;
	}
	
	public String getRoomId()
	{
		return this.roomId;
	}
	
	public String getPassphrase()
	{
		return this.passphrase;
	}

}
