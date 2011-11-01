package sc.api.plugins.host;

import java.util.Map;

import sc.api.plugins.IPlayer;
import sc.shared.PlayerScore;

public interface IGameListener
{
	public void onGameOver(Map<IPlayer, PlayerScore> results);

	public void onStateChanged(Object data);

	public void onPaused(IPlayer nextPlayer);
}
