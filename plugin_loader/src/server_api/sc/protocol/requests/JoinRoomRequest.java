package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("join")
public class JoinRoomRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	private String	roomId		= null;
	
	@XStreamAsAttribute
	private String	gameType	= null;

	protected JoinRoomRequest()
	{
		// nothing to do
	}

	public JoinRoomRequest(String gameType)
	{
		this.gameType = gameType;
	}

	public JoinRoomRequest(String gameType, String roomId)
	{
		this.roomId = roomId;
	}

	public String getGameType()
	{
		return this.gameType;
	}
	
	public String getRoomId()
	{
		return this.roomId;
	}
}
