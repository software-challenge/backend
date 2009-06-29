package sc.protocol.requests;

public class PrepareGameRequest implements ILobbyRequest
{
	private int		playerCount;
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
