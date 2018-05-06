package sc;

import com.thoughtworks.xstream.XStream;
import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.framework.plugins.SimplePlayer;
import sc.networking.INetworkInterface;
import sc.networking.TcpNetwork;
import sc.networking.clients.XStreamClient;
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

/**
 * Class to start testing. Enables testMode on startup.
 * <p/>
 * Defaults:
 * <ul>
 * <li>starts on localhost 13050</li>
 * <li>displayNames: player1, player2</li>
 * <li>client location: ./defaultplayer.jar</li>
 * <li>canTimeout: true</li>
 * </ul>
 */
public class TestClient extends XStreamClient {
  private static final Logger logger = LoggerFactory.getLogger(TestClient.class);

  private static final String gameType = "swc_2018_hase_und_igel";
  private static final Player[] players = {new Player(), new Player()};
  private static final File logDir = new File("logs").getAbsoluteFile();

  private static TestClient testclient;

  public static void main(String[] args) {
    System.setProperty("file.encoding", "UTF-8");

    // define commandline options
    CmdLineParser parser = new CmdLineParser();
    Option serverOption = parser.addBooleanOption("start-server");
    Option hostOption = parser.addStringOption('h', "host");
    Option portOption = parser.addIntegerOption('p', "port");
    Option numberOfTestsOption = parser.addIntegerOption("tests");
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

    // Parameter laden
    boolean startServer = (boolean) parser.getOptionValue(serverOption, false);
    String host = (String) parser.getOptionValue(hostOption, "localhost");
    int port = (int) parser.getOptionValue(portOption, SharedConfiguration.DEFAULT_PORT);
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
        builder.redirectOutput(new File(logDir, "server-" + port + ".log"));
        builder.redirectError(new File(logDir, "server-" + port + ".err"));
        builder.start(); // server will automatically be terminated upon exit since it is a child process
        Thread.sleep(1000);
      }
      testclient = new TestClient(Configuration.getXStream(), sc.plugin2018.util.Configuration.getClassesToRegister(), host, port, numberOfTests);
    } catch (Exception e) {
      logger.error("Error while initializing: " + e.toString());
      e.printStackTrace();
      exit(2);
    }
  }

  private String host;
  private int port;

  /** number of tests that have already been run */
  private int currentTests;
  /** total number of tests that should be executed */
  private int numberOfTests;

  private boolean terminateWhenPossible = false;
  private int gotLastPlayerScores = 0;
  private int irregularGames = 0;

  public TestClient(XStream xstream, Collection<Class<?>> protocolClasses,
                    String host, int port, int numberOfTests) throws IOException {
    super(xstream, createTcpNetwork(host, port));
    LobbyProtocol.registerMessages(xstream);
    LobbyProtocol.registerAdditionalMessages(xstream, protocolClasses);
    this.host = host;
    this.port = port;
    this.numberOfTests = numberOfTests;
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
                (result.isRegular() ? "ended regularly -" : "did not end regularly!") + " Winner: ");
        for (SimplePlayer winner : result.getWinners())
          log.append(winner.getDisplayName()).append(", ");
        logger.info(log.substring(0, log.length() - 2), currentTests);
        currentTests++;
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

        if (currentTests == numberOfTests)
          terminateWhenPossible = true;
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
      logger.warn(String.format("New score for %s: Siegpunkte %s, \u2205Feldnummer %5.2f, \u2205Karotten %5.2f after %s of %s tests",
              score.getDisplayName(), val.get(0).getValue(), val.get(1).getValue(), val.get(2).getValue(), currentTests, numberOfTests));

      if (gotLastPlayerScores == 2) {
        logger.warn(String.format("End results: \n" +
                        "=============== SCORES ================\n" +
                        "%s: %.0f\n%s: %.0f\n" +
                        "=======================================",
                players[0], players[0].score.getScoreValues().get(0).getValue(),
                players[1], players[1].score.getScoreValues().get(0).getValue()));
        logger.warn("{} of {} games did not end regularly!", irregularGames, currentTests);
        exit(0);
      }

    } else if (message instanceof PrepareGameProtocolMessage) {
      logger.debug("Received PrepareGame - starting clients");
      PrepareGameProtocolMessage pgm = (PrepareGameProtocolMessage) message;
      send(new ObservationRequest(pgm.getRoomId()));
      try {
        for (int i = 0; i < 2; i++)
          startPlayer(i, pgm.getReservations().get((currentTests + i) % 2));

        for (int i = 0; i < 2; i++)
          if (!players[i].proc.isAlive()) {
            logger.error("{} could not be started, look into {}", players[0].displayName, logDir);
            exit(2);
          }

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
    builder.redirectOutput(new File(logDir, players[id].displayName + "_Test" + currentTests + ".log"));
    builder.redirectError(new File(logDir, File.separator + players[id].displayName + "_Test" + currentTests + ".err"));
    players[id].proc = builder.start();
    try {
      Thread.sleep(100);
    } catch (InterruptedException ignored) {
    }
  }

  /** prepares slots for new clients (if {@link #currentTests} is even player1 starts, otherwise player2) */
  private void prepareNewClients() {
    if (currentTests == numberOfTests)
      return;
    SlotDescriptor[] slots = new SlotDescriptor[2];
    for (int i = 0; i < 2; i++)
      slots[(currentTests + i) % 2] = new SlotDescriptor(players[i].displayName, players[i].canTimeout, false);
    logger.debug("Prepared client slots: " + Arrays.toString(slots));
    send(new PrepareGameRequest(gameType, slots[0], slots[1]));
  }

  private static void exit(int status) {
    if (testclient != null)
      testclient.send(new CloseConnection());

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
