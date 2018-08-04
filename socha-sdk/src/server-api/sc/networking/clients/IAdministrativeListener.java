package sc.networking.clients;

import sc.framework.plugins.AbstractPlayer;

public interface IAdministrativeListener
{
	void onGamePaused(String roomId, AbstractPlayer nextPlayer);
}
