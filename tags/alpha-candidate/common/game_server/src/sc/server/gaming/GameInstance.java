package sc.server.gaming;

import sc.api.plugins.IGameInstance;
import sc.server.plugins.GamePluginInstance;

/**
 * A wrapper for an actual <code>GameInstance</code>. GameInstances are provided
 * by the plugins. Additional mapping data (Client2Player) will be stored here.
 */
public class GameInstance
{
	private GamePluginInstance	provider;
	private IGameInstance		game;

	public GameInstance(GamePluginInstance provider, IGameInstance game)
	{
		this.provider = provider;
		this.game = game;
	}

	public GamePluginInstance getProvider()
	{
		return provider;
	}

	public IGameInstance getGame()
	{
		return game;
	}
}
