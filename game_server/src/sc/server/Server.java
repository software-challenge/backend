package sc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements Runnable
{
	protected static final Logger	logger	= LoggerFactory
													.getLogger(Server.class);
	Lobby							lobby	= new Lobby();

	@Override
	public void run()
	{
		lobby.start();

		while (true)
		{
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				logger.warn("Failed to sleep.", e);
			}
		}
	}

	public void close()
	{
		lobby.close();
	}
}
