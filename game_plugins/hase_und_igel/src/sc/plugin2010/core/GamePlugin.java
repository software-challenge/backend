package sc.plugin2010.core;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.IGamePluginHost;
import sc.api.plugins.PluginDescriptor;

/**
 * Die Beschreibung des Hase- und Igel Plugins der Software-Challenge 2010.
 * 
 * @author rra
 * @since Jul 4, 2009
 */
@PluginDescriptor(name = "Hase und Igel", uuid = "swc_2010_hase_und_igel")
public class GamePlugin implements IGamePlugin
{
	// 2 Computer (default), 2 Menschen
	public static final int MAX_PLAYER_COUNT = 4;
	
	@Override
	public IGameInstance createGame()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaximumPlayerCount()
	{
		return MAX_PLAYER_COUNT;
	}

	@Override
	public void initialize(IGamePluginHost host)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload()
	{
		// TODO Auto-generated method stub
		
	}

}
