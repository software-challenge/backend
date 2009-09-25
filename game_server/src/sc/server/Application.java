package sc.server;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Application
{
	private static final Logger	logger	= LoggerFactory
												.getLogger(Application.class);
	private static final Object	waitObj	= new Object();

	public static void main(String[] params) throws IOException,
			InterruptedException, IllegalOptionValueException,
			UnknownOptionException
	{
		parseArguments(params);

		logger.info("Server is starting up...");

		addShutdownHook();
		long start = System.currentTimeMillis();

		final Lobby server = new Lobby();
		server.start();

		long end = System.currentTimeMillis();
		logger.info("Server has been initialized in {} ms.", end - start);

		synchronized (waitObj)
		{
			waitObj.wait();
		}
	}

	/**
	 * Starts a new Server. This is meant to be used by the GUI Application,
	 * which comes with an internal server for offline-play. TODO: use this in
	 * GUI app
	 * 
	 * @param port
	 * @return the lobby
	 */
	public static Lobby startServer(final Integer port) throws IOException
	{
		logger.info("Starting the Server on port {}", port);
		Configuration.set(Configuration.PORT_KEY, port.toString());
		final Lobby server = new Lobby();
		server.start();
		return server;
	}

	public static void parseArguments(String[] params)
			throws IllegalOptionValueException, UnknownOptionException
	{
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option debug = parser.addBooleanOption('d', "debug");
		CmdLineParser.Option pluginDirectory = parser
				.addStringOption("plugins");
		parser.parse(params);

		Boolean debugMode = (Boolean) parser.getOptionValue(debug, false);
		String path = (String) parser.getOptionValue(pluginDirectory, null);

		if (debugMode)
		{
			logger.info("Running in DebugMode now.");
		}

		if (path != null)
		{
			File f = new File(path);

			if (f.exists() && f.isDirectory())
			{
				Configuration.set(Configuration.PLUGIN_PATH_KEY, path);
				logger.info("Loading plugins from {}", f.getAbsoluteFile());
			}
			else
			{
				logger.warn("Could not find {} to load plugins from", f
						.getAbsoluteFile());
			}
		}
	}

	public static void addShutdownHook()
	{
		logger.info("Registering ShutdownHook (Ctrl+C)...");

		try
		{
			Thread shutdown = new Thread(new Runnable() {
				@Override
				public void run()
				{
					ServiceManager.killAll();
					// continues the main-method of this class
					synchronized (waitObj)
					{
						waitObj.notify();
						logger.info("Exiting application...");
					}
				}
			});

			shutdown.setName("ShutdownHook");
			Runtime.getRuntime().addShutdownHook(shutdown);
		}
		catch (Exception e)
		{
			logger.warn("Could not install ShutdownHook", e);
		}
	}
}
