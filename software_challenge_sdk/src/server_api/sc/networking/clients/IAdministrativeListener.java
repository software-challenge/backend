package sc.networking.clients;

import sc.api.plugins.IPlayer;

public interface IAdministrativeListener
{
	void onGamePaused(String roomId, IPlayer nextPlayer);
}
