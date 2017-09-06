package sc.server;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Application
{
	private static final Logger	logger	= LoggerFactory
												.getLogger(Application.class);
	private static final Object	waitObj	= new Object();

	public static void main(String[] params)
	{
		
		// Setup Server
		System.setProperty( "file.encoding", "UTF-8" );
		try {
			parseArguments(params);
		} catch (IllegalOptionValueException e){
			logger.error("Options could not be parsed");
			e.printStackTrace();
			return;
		} catch (UnknownOptionException e){
      logger.error("Unknown option");
      e.printStackTrace();
      return;
		}
		logger.info("Server is starting up...");

		// register crtl + c
		addShutdownHook();
		long start = System.currentTimeMillis();

    try {
      logger.error("loading server.properties");
      Configuration.load(new FileReader("server.properties"));
    } catch (IOException e) {
      logger.error("Could not find server.properties");
      e.printStackTrace();
      return;
    }
    final Lobby server = new Lobby();
    try {
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    long end = System.currentTimeMillis();
		logger.info("Server has been initialized in {} ms.", end - start);

		synchronized (waitObj)
		{
      try {
        waitObj.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
	}

	public static void parseArguments(String[] params)
			throws IllegalOptionValueException, UnknownOptionException
	{
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option debug = parser.addBooleanOption('d', "debug");
		CmdLineParser.Option pluginDirectory = parser
				.addStringOption("plugins");
		CmdLineParser.Option loadGameFileOption = parser.addStringOption("loadGameFile");
    CmdLineParser.Option turnOfLoadOption = parser.addIntegerOption("turn");
		parser.parse(params);

		Boolean debugMode = (Boolean) parser.getOptionValue(debug, false);
		String path = (String) parser.getOptionValue(pluginDirectory, null);
		String loadGameFile = (String) parser.getOptionValue(loadGameFileOption, null);
		Integer turnOfLoad = (Integer) parser.getOptionValue(turnOfLoadOption, 0);
		if (loadGameFile != null) {
			Configuration.set("loadGameFile", loadGameFile);
			if (turnOfLoad != 0) {
			  Configuration.set("turnOfLoad", turnOfLoad.toString());
      }
		}

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
