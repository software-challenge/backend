package sc.networking.clients;

import sc.framework.plugins.SimplePlayer;

public interface IAdministrativeListener
{
	void onGamePaused(String roomId, SimplePlayer nextPlayer);
}
