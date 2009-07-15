package sc.protocol.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import sc.protocol.IRequest;
import sc.protocol.responses.PrepareGameResponse;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("prepare")
public class PrepareGameRequest implements ILobbyRequest,
		IRequest<PrepareGameResponse>
{
	@XStreamAsAttribute
	private final int			playerCount;

	@XStreamAsAttribute
	private final String		gameType;

	@XStreamImplicit
	private final List<String>	displayNames	= new LinkedList<String>();

	public PrepareGameRequest(String gameType, int playerCount)
	{
		this(gameType, playerCount, new String[0]);
	}

	public PrepareGameRequest(String gameType, int playerCount,
			String... displayNames)
	{
		this(gameType, playerCount, displayNames != null ? Arrays
				.asList(displayNames) : null);
	}

	public PrepareGameRequest(String gameType, int playerCount,
			List<String> displayNames)
	{
		this.playerCount = playerCount;
		this.gameType = gameType;

		if (displayNames != null)
		{
			this.displayNames.addAll(displayNames);
		}

		if (this.displayNames.size() > playerCount)
		{
			throw new IllegalArgumentException(
					"The amount of DisplayNames must not be larger than the PlayerCount.");
		}

		for (int i = this.displayNames.size(); i < playerCount; i++)
		{
			this.displayNames.add("Player " + String.valueOf(i + 1));
		}
	}

	public String getGameType()
	{
		return this.gameType;
	}

	public int getPlayerCount()
	{
		return this.playerCount;
	}

	public List<String> getDisplayNames()
	{
		return this.displayNames;
	}
}
