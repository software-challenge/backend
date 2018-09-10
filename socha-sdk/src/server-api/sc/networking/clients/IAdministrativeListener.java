package sc.networking.clients;

import sc.framework.plugins.Player;

public interface IAdministrativeListener
{
	void onGamePaused(String roomId, Player nextPlayer);
}
