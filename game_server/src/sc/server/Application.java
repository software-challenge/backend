package sc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class Application
{
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	
	public static void main(String[] params) throws InterruptedException
	{
		logger.info("Server is starting up...");
		long start = System.currentTimeMillis();

		final Server server = new Server();

		long end = System.currentTimeMillis();
		logger.info("Server has been initialized in {} ms.", end - start);

		final Thread serverThread = new Thread(server);
		serverThread.start();
		serverThread.join();

		logger.info("Server is shutting down...");
	}
}
