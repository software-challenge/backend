package sc.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sc.api.plugins.IPlayer;
import sc.server.gaming.GameInstance;
import sc.server.gaming.GameManager;
import sc.server.network.Client;
import sc.server.network.ClientManager;
import sc.server.plugins.GamePluginInstance;


/**
 * The lobby will help clients find a open game or create new games to play with
 * another client.
 * 
 * @author mja
 * @author rra
 */
public class Lobby
{
	private Map<IPlayer, Client>			players			= new HashMap<IPlayer, Client>();
	private GameManager						gameManager		= new GameManager();
	private ClientManager					clientManager	= new ClientManager();

	public Collection<GameInstance> listOpenGames()
	{
		return listOpenGames(null);
	}

	public Collection<GameInstance> listOpenGames(String pluginName)
	{
		return new ArrayList<GameInstance>(0);
	}

	public Client resolvePlayer(IPlayer player)
	{
		return this.players.get(player);
	}

	public void start()
	{
		gameManager.start();
		clientManager.start();
	}
}
