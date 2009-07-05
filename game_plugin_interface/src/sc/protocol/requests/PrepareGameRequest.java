package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("prepare")
public class PrepareGameRequest implements ILobbyRequest
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
