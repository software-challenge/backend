package sc.server.gaming;

import sc.api.plugins.IGame;
import sc.server.plugins.GamePluginInstance;

/**
 * A wrapper for an actual <code>GameInstance</code>. GameInstances are provided
 * by the plugins. Additional mapping data (Client2Player) will be stored here.
 */
public class GameInstance
{
	private GamePluginInstance	provider;
	private IGame				game;

	public GameInstance(GamePluginInstance provider, IGame game)
	{
		this.provider = provider;
		this.game = game;
	}

	public GamePluginInstance getProvider()
	{
		return provider;
	}

	public IGame getGame()
	{
		return game;
	}
}
