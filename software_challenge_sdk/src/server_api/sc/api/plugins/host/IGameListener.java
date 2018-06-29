package sc.api.plugins.host;

import java.util.Map;

import sc.framework.plugins.AbstractPlayer;
import sc.networking.InvalidScoreDefinitionException;
import sc.shared.PlayerScore;

public interface IGameListener
{
	public void onGameOver(Map<AbstractPlayer, PlayerScore> results) throws InvalidScoreDefinitionException;

	public void onStateChanged(Object data);

	public void onPaused(AbstractPlayer nextPlayer);
}
