package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

@XStreamAlias("join")
public class JoinRoomRequest extends ProtocolMessage implements ILobbyRequest
{
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

	public String getGameType()
	{
		return this.gameType;
	}
}
