package sc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Application
{
	private static final Logger	logger	= LoggerFactory
												.getLogger(Application.class);

	public static void main(String[] params) throws InterruptedException
	{
		logger.info("Server is starting up...");
		addShutdownHook();
		long start = System.currentTimeMillis();

		final Server server = new Server();

		long end = System.currentTimeMillis();
		logger.info("Server has been initialized in {} ms.", end - start);

		ServiceManager.createService(server, false).start();
	}

	public static void addShutdownHook()
	{
		try
		{
			Thread shutdown = new Thread(new Runnable() {
				@Override
				public void run()
				{
					logger.info("Shutting down...");
					ServiceManager.killAll();
					logger.info("Exiting");
				}
			});
			
			shutdown.setName("ShutdownHook");
			Runtime.getRuntime().addShutdownHook(shutdown);
		}
		catch (Exception e)
		{
			logger.warn("Could not install ShutdownHook");
		}
	}
}
