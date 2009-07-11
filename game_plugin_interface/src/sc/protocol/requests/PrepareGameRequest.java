package sc.protocol.requests;

import sc.protocol.IRequest;
import sc.protocol.responses.PrepareGameResponse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("prepare")
public class PrepareGameRequest implements ILobbyRequest, IRequest<PrepareGameResponse>
{
	@XStreamAsAttribute
	private int		playerCount;
	
	@XStreamAsAttribute
	private String	gameType;

	public PrepareGameRequest(String gameType, int playerCount)
	{
		this.playerCount = playerCount;
		this.gameType = gameType;
	}

	public String getGameType()
	{
		return this.gameType;
	}

	public int getPlayerCount()
	{
		return this.playerCount;
	}
}
