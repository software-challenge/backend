package sc.protocol;

import sc.protocol.requests.ILobbyRequest;

public class ObservationRequest implements ILobbyRequest
{
	private String	gameId;
	private String	passphrase;

	public ObservationRequest(String gameId, String passphrase)
	{
		this.gameId = gameId;
		this.passphrase = passphrase;
	}
	
	public String getGameId()
	{
		return this.gameId;
	}
	
	public String getPassphrase()
	{
		return this.passphrase;
	}

}
