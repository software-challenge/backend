package sc.server.gaming;

import java.util.Collection;
import java.util.LinkedList;

import sc.server.plugins.GamePluginManager;


/**
 * The GameManager is responsible to keep all games alive and kill
 * them once they are done. Additionally the GameManger has to detect
 * and kill games, which seem to be dead-locked or have caused
 * a timeout.
 * 
 * @author mja
 * @author rra
 */
public class GameManager implements Runnable
{
	private Collection<GameInstance> games = new LinkedList<GameInstance>();
	private final GamePluginManager	gamePluginManager	= new GamePluginManager();
	private GamePluginApi pluginApi = new GamePluginApi();
	
	public GameManager()
	{
		gamePluginManager.reload();
		gamePluginManager.activateAllPlugins(this.pluginApi);
	}
	
	/**
	 * Adds an active game to the <code>GameManager</code>
	 * @param game
	 */
	public void add(GameInstance game)
	{
		this.games.add(game);
	}

	public void start()
	{
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run()
	{
		while(true)
		{
			for(GameInstance game : games)
			{
				Thread.yield();
			}
		}
	}
}
