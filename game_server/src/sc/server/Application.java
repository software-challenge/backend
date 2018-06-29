package sc.server;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.shared.SharedConfiguration;

import java.io.File;
import java.io.IOException;

public final class Application {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);
  private static final Object SYNCOBJ = new Object();
  static {
    logger.debug("Loading logback config from {}", System.getProperty("user.dir")+File.separator+"logback.xml");
    System.setProperty("logback.configurationFile", System.getProperty("user.dir")+File.separator+"logback.xml");
  }

  public static void main(String[] params) {
    // setup server
    System.setProperty("file.encoding", "UTF-8");
    try {
      parseArguments(params);
    } catch (IllegalOptionValueException e) {
      logger.error("Illegal option value: " + e.getMessage());
      e.printStackTrace();
      return;
    } catch (UnknownOptionException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
      return;
    }
    logger.info("Server is starting up...");

    // register crtl + c
    addShutdownHook();
    long start = System.currentTimeMillis();

    Configuration.loadServerProperties();

    final Lobby server = new Lobby();
    try {
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    long end = System.currentTimeMillis();
    logger.debug("Server has been initialized in {} ms.", end - start);

    synchronized (SYNCOBJ) {
      try {
        SYNCOBJ.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void parseArguments(String[] params)
          throws IllegalOptionValueException, UnknownOptionException {
    CmdLineParser parser = new CmdLineParser();
    CmdLineParser.Option pluginDirectory = parser.addStringOption(Configuration.PLUGINS_OPTION);
    CmdLineParser.Option loadGameFileOption = parser.addStringOption(Configuration.GAMELOADFILE_OPTION);
    CmdLineParser.Option turnToLoadOption = parser.addIntegerOption(Configuration.TURN_OPTION);
    CmdLineParser.Option saveReplayOption = parser.addBooleanOption(Configuration.SAVE_REPLAY);
    CmdLineParser.Option portOption = parser.addIntegerOption('p', Configuration.PORT_KEY);
    parser.parse(params);

    String path = (String) parser.getOptionValue(pluginDirectory, null);
    String loadGameFile = (String) parser.getOptionValue(loadGameFileOption, null);
    Integer turnToLoad = (Integer) parser.getOptionValue(turnToLoadOption, 0);
    Boolean saveReplay = (Boolean) parser.getOptionValue(saveReplayOption, false);
    String port = parser.getOptionValue(portOption, SharedConfiguration.DEFAULT_PORT).toString();

    Configuration.set(Configuration.PORT_KEY, port);
    if (loadGameFile != null) {
      Configuration.set(Configuration.GAMELOADFILE, loadGameFile);
      if (turnToLoad != 0) {
        Configuration.set(Configuration.TURN_TO_LOAD, turnToLoad.toString());
      }
    }

    if (saveReplay) {
      Configuration.set(Configuration.SAVE_REPLAY, saveReplay.toString());
    }

    if (path != null) {
      File f = new File(path);

      if (f.exists() && f.isDirectory()) {
        Configuration.set(Configuration.PLUGIN_PATH_KEY, path);
        logger.info("Loading plugins from {}", f.getAbsoluteFile());
      } else {
        logger.warn("Could not find {} to load plugins from", f.getAbsoluteFile());
      }
    }
  }

  public static void addShutdownHook() {
    logger.debug("Registering ShutdownHook...");

    try {
      Thread shutdown = new Thread(() -> {
        ServiceManager.killAll();
        // continues the main-method of this class
        synchronized (SYNCOBJ) {
          SYNCOBJ.notifyAll();
          logger.info("Exiting application...");
        }
      });
      shutdown.setName("ShutdownHook");
      Runtime.getRuntime().addShutdownHook(shutdown);
    } catch (Exception e) {
      logger.warn("Registering the ShutdownHook failed!", e);
    }
  }

}
