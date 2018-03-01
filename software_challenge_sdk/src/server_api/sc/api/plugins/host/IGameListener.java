package sc.api.plugins.host;

import java.util.Map;

import sc.framework.plugins.SimplePlayer;
import sc.networking.InvalidScoreDefinitionException;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.PlayerScore;

public interface IGameListener
{
	public void onGameOver(Map<SimplePlayer, PlayerScore> results) throws InvalidScoreDefinitionException;

	public void onStateChanged(Object data);

	public void onPaused(SimplePlayer nextPlayer);
}
