package sc.plugin2010.core;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.IGamePluginHost;
import sc.api.plugins.PluginDescriptor;
import sc.plugin2010.shared.Board;
import sc.plugin2010.shared.Game;
import sc.plugin2010.shared.Move;
import sc.plugin2010.shared.Player;

/**
 * Die Beschreibung des Hase- und Igel Plugins der Software-Challenge 2010.
 * 
 * @author rra
 * @since Jul 4, 2009
 */
@PluginDescriptor(name = "Hase und Igel", uuid = GamePlugin.PLUGIN_UUID)
public class GamePlugin implements IGamePlugin
{
	public static final String PLUGIN_UUID = "swc_2010_hase_und_igel";
	
	// 2 Computer (default), 2 Menschen
	public static final int MAX_PLAYER_COUNT = 4;
	
	@Override
	public IGameInstance createGame()
	{
		Game g = new Game();
		return g;
	}

	@Override
	public int getMaximumPlayerCount()
	{
		return MAX_PLAYER_COUNT;
	}

	@Override
	public void initialize(IGamePluginHost host)
	{
		host.registerProtocolClass(Player.class);
		host.registerProtocolClass(Move.class);		
		host.registerProtocolClass(Board.class);		
	}

	@Override
	public void unload()
	{
		// TODO Auto-generated method stub
		
	}

}
