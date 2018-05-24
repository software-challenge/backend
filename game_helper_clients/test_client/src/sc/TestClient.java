package sc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.thoughtworks.xstream.XStream;
import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import org.slf4j.LoggerFactory;
import sc.framework.plugins.SimplePlayer;
import sc.networking.INetworkInterface;
import sc.networking.TcpNetwork;
import sc.networking.clients.XStreamClient;
import sc.plugin2018.util.Constants;
import sc.protocol.LobbyProtocol;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.server.Configuration;
import sc.shared.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to start testing. Enables testMode on startup.
 * <p/>
 * Defaults:
 * <ul>
 * <li>starts on localhost 13051</li>
 * <li>displayNames: player1, player2</li>
 * <li>client location: ./defaultplayer.jar</li>
 * <li>canTimeout: true</li>
 * </ul>
 */
public class TestClient extends XStreamClient {
  private static final Logger logger = (Logger) LoggerFactory.getLogger(TestClient.class);
  
  private static final String gameType = "swc_2018_hase_und_igel";
  private static final Player[] players = {new Player(), new Player()};
  private static final File logDir = new File("logs").getAbsoluteFile();
  
  private static TestClient testclient;
  
  public static void main(String[] args) {
    System.setProperty("file.encoding", "UTF-8");
    
    // define commandline options
    CmdLineParser parser = new CmdLineParser();
    Option loglevelOption = parser.addStringOption("loglevel");
    Option serverOption = parser.addBooleanOption("start-server");
    Option hostOption = parser.addStringOption('h', "host");
    Option portOption = parser.addIntegerOption('p', "port");
    Option numberOfTestsOption = parser.addIntegerOption('t', "tests");
    Option[] execOptions = {parser.addStringOption("player1"), parser.addStringOption("player2")};
    Option[] nameOptions = {parser.addStringOption("name1"), parser.addStringOption("name2")};
    Option[] canTimeoutOptions = {parser.addBooleanOption("timeout1"), parser.addBooleanOption("timeout2")};
    
    try {
      parser.parse(args);
    } catch (CmdLineParser.OptionException e) {
      logger.error(e.toString());
      e.printStackTrace();
      exit(2);
    }
    
    Configuration.loadServerProperties();
    
    // read commandline options
    String loglevel = (String) parser.getOptionValue(loglevelOption, null);
    if (loglevel != null)
      logger.setLevel(Level.toLevel(loglevel));
    
    boolean startServer = (boolean) parser.getOptionValue(serverOption, false);
    String host = (String) parser.getOptionValue(hostOption, "localhost");
    int port = (int) parser.getOptionValue(portOption, SharedConfiguration.DEFAULT_TESTSERVER_PORT);
    int numberOfTests = (int) parser.getOptionValue(numberOfTestsOption, 10);
    for (int i = 0; i < 2; i++) {
      players[i].canTimeout = (Boolean) parser.getOptionValue(canTimeoutOptions[i], true);
      players[i].displayName = (String) parser.getOptionValue(nameOptions[i], "player" + (i + 1));
      players[i].executable = (String) parser.getOptionValue(execOptions[i], "./defaultplayer.jar");
      players[i].isJar = isJar(players[i].executable);
    }
    if (players[0].displayName.equals(players[1].displayName)) {
      logger.warn("Both players have the same name, adding suffixes.");
      players[0].displayName = players[0].displayName + "-1";
      players[1].displayName = players[1].displayName + "-2";
    }
    
    try {
      if (startServer) {
        logger.info("Starting server...");
        ProcessBuilder builder = new ProcessBuilder("java", "-Dfile.encoding=UTF-8", "-jar", "software-challenge-server.jar", "--port", String.valueOf(port));
        logDir.mkdirs();
        builder.redirectOutput(new File(logDir, "server_port" + port + ".log"));
        builder.redirectError(new File(logDir, "server_port" + port + ".err"));
        Process server = builder.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::destroyForcibly));
        Thread.sleep(1000);
      }
      testclient = new TestClient(Configuration.getXStream(), sc.plugin2018.util.Configuration.getClassesToRegister(), host, port, numberOfTests);
      Runtime.getRuntime().addShutdownHook(new Thread(testclient::printScores));
    } catch (Exception e) {
      logger.error("Error while initializing: " + e.toString());
      e.printStackTrace();
      exit(2);
    }
  }
  
  private String host;
  private int port;
  
  /** number of tests that have already been run */
  private int finishedTests;
  /** total number of tests that should be executed */
  private int totalTests;
  
  private boolean terminateWhenPossible = false;
  private int gotLastPlayerScores = 0;
  private int irregularGames = 0;
  
  private ExecutorService waiter = Executors.newSingleThreadExecutor();
  
  public TestClient(XStream xstream, Collection<Class<?>> protocolClasses,
                    String host, int port, int totalTests) throws IOException {
    super(xstream, createTcpNetwork(host, port));
    LobbyProtocol.registerMessages(xstream);
    LobbyProtocol.registerAdditionalMessages(xstream, protocolClasses);
    this.host = host;
    this.port = port;
    this.totalTests = totalTests;
    start();
    logger.debug("Authenticating as administrator, enabling TestMode");
    send(new AuthenticateRequest(Configuration.getAdministrativePassword()));
    send(new TestModeRequest(true));
    logger.info("Waiting for server...");
  }
  
  @Override
  protected void onObject(ProtocolMessage message) {
    if (message == null) {
      logger.warn("Received null message");
      return;
    }
    
    logger.trace("Received {}", message);
    if (message instanceof TestModeMessage) {
      boolean testMode = (((TestModeMessage) message).testMode);
      logger.debug("TestMode was set to {} - starting clients", testMode);
      prepareNewClients();
    } else if (message instanceof RoomPacket) {
      RoomPacket packet = (RoomPacket) message;
      if (packet.getData() instanceof GameResult) {
        GameResult result = (GameResult) packet.getData();
        if (!result.isRegular())
          irregularGames++;
        StringBuilder log = new StringBuilder("Game {} " +
            (result.isRegular() ? "ended regularly -" : "ended abnormally!") + " Winner: ");
        for (SimplePlayer winner : result.getWinners())
          log.append(winner.getDisplayName()).append(", ");
        logger.warn(log.substring(0, log.length() - 2), finishedTests);
        
        finishedTests++;
        for (int i = 0; i < 2; i++)
          send(new GetScoreForPlayerRequest(players[i].displayName));
        
        try {
          for (int i = 0; i < 2; i++)
            players[i].proc.waitFor();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        for (int i = 0; i < 2; i++)
          players[i].proc.destroy();
        
        if (finishedTests == totalTests)
          terminateWhenPossible = true;
        else
          prepareNewClients();
      }
    } else if (message instanceof PlayerScorePacket) {
      if (terminateWhenPossible)
        gotLastPlayerScores++;
      Score score = ((PlayerScorePacket) message).getScore();
      
      for (Player p : players) {
        if (p.displayName.equals(score.getDisplayName())) {
          p.score = score;
          break;
        }
      }
      
      List<ScoreValue> val = score.getScoreValues();
      logger.info(String.format("New score for %s: Siegpunkte %s, \u2205Feldnummer %5.2f, \u2205Karotten %5.2f after %s of %s tests",
          score.getDisplayName(), val.get(0).getValue(), val.get(1).getValue(), val.get(2).getValue(), finishedTests, totalTests));
      
      if (gotLastPlayerScores == 2) {
        printScores();
        exit(0);
      }
      
    } else if (message instanceof PrepareGameProtocolMessage) {
      logger.debug("Received PrepareGame - starting clients");
      PrepareGameProtocolMessage pgm = (PrepareGameProtocolMessage) message;
      send(new ObservationRequest(pgm.getRoomId()));
      try {
        for (int i = 0; i < 2; i++)
          startPlayer(i, pgm.getReservations().get((finishedTests + i) % 2));
        
        waiter.execute(() -> {
          int tests = finishedTests;
          int slept = 0;
          while (tests == finishedTests) {
            if (slept < 10)
              for (int i = 0; i < 2; i++)
                if (!players[i].proc.isAlive()) {
                  logger.error("{} crashed, look into {}", players[i].displayName, logDir);
                  exit(2);
                }
            try {
              Thread.sleep(1000);
              slept++;
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            if (slept > Constants.ROUND_LIMIT * 5) {
              logger.error("The game seems to hang, exiting!");
              exit(2);
            }
          }
        });
        
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      logger.debug("Received uninteresting " + message.getClass().getSimpleName());
    }
  }
  
  private void startPlayer(int id, String reservation) throws IOException {
    Player player = players[id];
    ProcessBuilder builder;
    if (player.isJar) {
      logger.info("Invoking client {} with Java", player);
      builder = new ProcessBuilder("java", "-jar", "-mx1500m", player.executable, "-r", reservation, "-h", host, "-p", Integer.toString(port));
    } else {
      logger.info("Invoking client {}", player);
      builder = new ProcessBuilder(player.executable, "--reservation", reservation, "--host", host, "--port", Integer.toString(port));
    }
    
    logDir.mkdirs();
    builder.redirectOutput(new File(logDir, players[id].displayName + "_Test" + finishedTests + ".log"));
    builder.redirectError(new File(logDir, players[id].displayName + "_Test" + finishedTests + ".err"));
    players[id].proc = builder.start();
    try {
      Thread.sleep(100);
    } catch (InterruptedException ignored) {
    }
  }
  
  /** prepares slots for new clients (if {@link #finishedTests} is even player1 starts, otherwise player2) */
  private void prepareNewClients() {
    SlotDescriptor[] slots = new SlotDescriptor[2];
    for (int i = 0; i < 2; i++)
      slots[(finishedTests + i) % 2] = new SlotDescriptor(players[i].displayName, players[i].canTimeout, false);
    logger.debug("Prepared client slots: " + Arrays.toString(slots));
    send(new PrepareGameRequest(gameType, slots[0], slots[1]));
  }
  
  private static void exit(int status) {
    if (testclient != null) {
      testclient.send(new CloseConnection());
      testclient.waiter.shutdownNow();
    }
    
    for (Player p : players)
      if (p.proc != null)
        p.proc.destroyForcibly();
    
    if (status != 0) {
      logger.warn("Terminating");
      System.exit(status);
    }
  }
  
  private static boolean isJar(String f) {
    return f.endsWith("jar") && new File(f).exists();
  }
  
  private static INetworkInterface createTcpNetwork(String host, int port) throws IOException {
    logger.info("Creating TCP Network for {}:{}", host, port);
    return new TcpNetwork(new Socket(host, port));
  }
  
  private boolean scoresPrinted = false;
  
  private void printScores() {
    if (scoresPrinted) return;
    try {
      logger.warn(String.format("\n" +
              "=============== SCORES ================\n" +
              "%s: %.0f\n" +
              "%s: %.0f\n" +
              "=======================================\n" +
              "{} of {} games ended abnormally!",
          players[0], players[0].score.getScoreValues().get(0).getValue(),
          players[1], players[1].score.getScoreValues().get(0).getValue()),
          irregularGames, finishedTests);
      scoresPrinted = true;
    } catch (Exception ignored) {
    }
  }
  
}

class Player {
  String displayName;
  boolean canTimeout;
  
  String executable;
  boolean isJar;
  
  Process proc;
  Score score;
  
  @Override
  public String toString() {
    return displayName;
  }
}
