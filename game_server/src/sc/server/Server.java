package sc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.IGamePluginHost;


public class Server implements IGamePluginHost, Runnable
{
	protected static final Logger	logger				= LoggerFactory
																.getLogger(Server.class);
	
	@Override
	public void run()
	{
		Lobby lobby = new Lobby();
		lobby.start();
	}
}
