package sc.server;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.UnknownOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.shared.SharedConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public final class Application {

  static {
    LoggingKt.logbackFromPWD();
  }

  private static final Logger logger = LoggerFactory.getLogger(Application.class);
  private static final Object SYNCOBJ = new Object();

  public static void main(String[] params) {
    // setup server
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

    try {
      List<String> version = Files.readAllLines(Paths.get("version"));
      logger.info("Running version {}", version);
    } catch(IOException ignored) {
      logger.debug("Could not determine running version");
    }

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

    synchronized(SYNCOBJ) {
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
    Option pluginDirOption = parser.addStringOption(Configuration.PLUGINS_OPTION);
    Option portOption = parser.addIntegerOption('p', Configuration.PORT_KEY);

    Option loadGameFileOption = parser.addStringOption(Configuration.GAMELOADFILE);
    Option turnToLoadOption = parser.addIntegerOption(Configuration.TURN_TO_LOAD);
    Option saveReplayOption = parser.addBooleanOption(Configuration.SAVE_REPLAY);
    parser.parse(params);

    String pluginPath = (String) parser.getOptionValue(pluginDirOption, null);
    String port = parser.getOptionValue(portOption, SharedConfiguration.DEFAULT_PORT).toString();

    String loadGameFile = (String) parser.getOptionValue(loadGameFileOption, null);
    Integer turnToLoad = (Integer) parser.getOptionValue(turnToLoadOption, 0);
    Boolean saveReplay = (Boolean) parser.getOptionValue(saveReplayOption, false);

    Configuration.set(Configuration.PORT_KEY, port);
    if (loadGameFile != null) {
      Configuration.set(Configuration.GAMELOADFILE, loadGameFile);
      if (turnToLoad != 0)
        Configuration.set(Configuration.TURN_TO_LOAD, turnToLoad.toString());
    }

    if (saveReplay)
      Configuration.set(Configuration.SAVE_REPLAY, saveReplay.toString());

    if (pluginPath != null) {
      File pluginDir = new File(pluginPath).getAbsoluteFile();
      if (pluginDir.exists() && pluginDir.isDirectory()) {
        Configuration.set(Configuration.PLUGINS_OPTION, pluginPath);
        logger.info("Loading plugins from {}", pluginDir);
      } else {
        logger.warn("Could not find {} to load plugins from", pluginDir);
      }
    }
  }

  public static void addShutdownHook() {
    logger.debug("Registering ShutdownHook...");

    try {
      Thread shutdown = new Thread(() -> {
        ServiceManager.killAll();
        // continues the main-method of this class
        synchronized(SYNCOBJ) {
          SYNCOBJ.notifyAll();
          logger.info("Exiting Application...");
        }
      });
      shutdown.setName("ShutdownHook");
      Runtime.getRuntime().addShutdownHook(shutdown);
    } catch (Exception e) {
      logger.warn("Registering the ShutdownHook failed!", e);
    }
  }

}
