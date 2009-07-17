package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


public class ObservationRequest implements ILobbyRequest
{
	@XStreamAsAttribute
	private String	gameId;
	
	@XStreamAsAttribute
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
