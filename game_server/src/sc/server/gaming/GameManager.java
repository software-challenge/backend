package sc.server.gaming;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import sc.server.ServiceManager;
import sc.server.plugins.GamePluginManager;

/**
 * The GameManager is responsible to keep all games alive and kill them once
 * they are done. Additionally the GameManger has to detect and kill games,
 * which seem to be dead-locked or have caused a timeout.
 * 
 * @author mja
 * @author rra
 */
public class GameManager implements Runnable
{
	private Collection<GameInstance>	games				= new LinkedList<GameInstance>();
	private final GamePluginManager		gamePluginManager	= new GamePluginManager();
	private GamePluginApi				pluginApi			= new GamePluginApi();

	public GameManager()
	{
		this.gamePluginManager.reload();
		this.gamePluginManager.activateAllPlugins(this.pluginApi);
	}

	/**
	 * Adds an active game to the <code>GameManager</code>
	 * 
	 * @param game
	 */
	public void add(GameInstance game)
	{
		this.games.add(game);
	}

	public void start()
	{
		ServiceManager.createService(this.getClass().getSimpleName(), this,
				false).start();
	}

	@Override
	public void run()
	{
		List<GameInstance> toRemove = new LinkedList<GameInstance>();

		while (!Thread.interrupted())
		{
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				return; // silently return
			}

			for (GameInstance game : this.games)
			{
				if (game.isOver())
				{
					toRemove.add(game);
				}
			}

			this.games.removeAll(toRemove);
			toRemove.clear();
		}
	}
}
