package sc.protocol;

import sc.api.plugins.IPlayer;

public interface IAdministrativeListener
{
	void onGamePaused(String roomId, IPlayer nextPlayer);
}
