package sc;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGamePlugin;
import sc.framework.plugins.Player;
import sc.networking.InvalidScoreDefinitionException;
import sc.networking.clients.XStreamClient;
import sc.protocol.ProtocolPacket;
import sc.protocol.room.RoomPacket;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.server.Configuration;
import sc.shared.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntToDoubleFunction;

import static java.lang.Math.pow;
import static sc.Util.factorial;

/**
 * A simple command-line application to test clients. Enables TestMode on startup.
 * <p>
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

  private static final IGamePlugin plugin = IGamePlugin.loadPlugin();
  private static final ClientPlayer[] players = {new ClientPlayer(), new ClientPlayer()};
  private static final File logDir = new File("log").getAbsoluteFile();

  private static TestClient testclient;
  private static Double significance;
  private static int minTests;

  private static final String classpath = System.getProperty("java.class.path");

  public static void main(String[] args) {
    System.setProperty("file.encoding", "UTF-8");

    // define commandline options
    CmdLineParser parser = new CmdLineParser();
    Option loglevelOption = parser.addStringOption("loglevel");
    Option serverOption = parser.addBooleanOption("start-server");
    Option serverLocationOption = parser.addStringOption("server");
    Option hostOption = parser.addStringOption('h', "host");
    Option portOption = parser.addIntegerOption('p', "port");

    Option numberOfTestsOption = parser.addIntegerOption('t', "tests");
    Option minTestsOption = parser.addIntegerOption("min-tests");
    Option significanceOption = parser.addDoubleOption("significance");

    Option noTimeoutOption = parser.addBooleanOption("no-timeout");
    Option[] execOptions = {parser.addStringOption("player1"), parser.addStringOption("player2")};
    Option[] nameOptions = {parser.addStringOption("name1"), parser.addStringOption("name2")};
    Option[] noTimeoutOptions = {parser.addBooleanOption("no-timeout1"), parser.addBooleanOption("no-timeout2")};

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
    if (loglevel != null) {
      Level level = Level.toLevel(loglevel, null);
      if (level == null)
        logger.warn(loglevel + " is not a valid LogLevel!");
      else
        logger.setLevel(level);
    }

    boolean startServer = (boolean) parser.getOptionValue(serverOption, false);
    String host = (String) parser.getOptionValue(hostOption, "localhost");
    int port = (int) parser.getOptionValue(portOption, SharedConfiguration.DEFAULT_TESTSERVER_PORT);

    int numberOfTests = (int) parser.getOptionValue(numberOfTestsOption, 100);
    significance = (Double) parser.getOptionValue(significanceOption);
    if (significance != null) {
      minTests = (int) parser.getOptionValue(minTestsOption, 20);
      if (numberOfTests > 170) {
        logger.error("With significance testing the number of tests must not exceed 170!");
        exit(2);
      }
    }

    boolean noTimeout = (boolean) parser.getOptionValue(noTimeoutOption, false);
    for (int i = 0; i < 2; i++) {
      players[i].canTimeout = !(noTimeout || (boolean) parser.getOptionValue(noTimeoutOptions[i], false));
      players[i].name = (String) parser.getOptionValue(nameOptions[i], "player" + (i + 1));
      players[i].executable = new File((String) parser.getOptionValue(execOptions[i], "./defaultplayer.jar"));
      players[i].isJar = Util.isJar(players[i].executable);
    }
    if (players[0].name.equals(players[1].name)) {
      logger.warn("Both players have the same name, adding suffixes!");
      players[0].name = players[0].name + "-1";
      players[1].name = players[1].name + "-2";
    }
    logger.info("Players: " + Arrays.toString(players));

    try {
      if (startServer) {
        File serverLocation = findInClasspath((File) parser.getOptionValue(serverLocationOption, new File("server.jar")));
        logger.info("Starting server from {}", serverLocation);
        ProcessBuilder builder = new ProcessBuilder("java", "-classpath", classpath, "-Dfile.encoding=UTF-8", "-jar", serverLocation.getPath(), "--port", String.valueOf(port));
        logDir.mkdirs();
        builder.redirectOutput(new File(logDir, "server_port" + port + ".log"));
        builder.redirectError(new File(logDir, "server_port" + port + "-err.log"));
        Process server = builder.start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::destroyForcibly));
        Thread.sleep(1000);
      }
      testclient = new TestClient(host, port, numberOfTests);
      Runtime.getRuntime().addShutdownHook(new Thread(testclient::printScores));
    } catch (Exception e) {
      logger.error("Error while initializing: " + e.toString());
      e.printStackTrace();
      exit(2);
    }
  }

  private static File findInClasspath(File location) {
    if (!location.exists()) {
      final StringTokenizer strTokenizer = new StringTokenizer(classpath, File.pathSeparator);
      while (strTokenizer.hasMoreTokens()) {
        final File item = new File(strTokenizer.nextToken());
        if (item.exists() && item.getName().equals(location.getName())) {
          return item;
        }
      }
    }
    return location;
  }

  private final String host;
  private final int port;

  private final ExecutorService waiter = Executors.newSingleThreadExecutor();

  /** total number of tests that should be executed */
  private final int totalTests;

  /** number of tests that have already been run */
  private int finishedTests;

  private boolean terminateWhenPossible = false;
  private int playerScores = 0;
  private int irregularGames = 0;

  public TestClient(String host, int port, int totalTests) throws IOException {
    super(createTcpNetwork(host, port));

    this.host = host;
    this.port = port;
    this.totalTests = totalTests;
    start();
    logger.debug("Authenticating as administrator");
    send(new AuthenticateRequest(Configuration.getAdministrativePassword()));
    logger.info("Starting clients");
    prepareNewClients();
  }

  private boolean gameProgressing = false;

  @Override
  protected void onObject(@NotNull ProtocolPacket message) {
    logger.trace("Received {}", message);
    if (message instanceof RoomPacket) {
      RoomPacket packet = (RoomPacket) message;
      if (packet.getData() instanceof GameResult) {
        if (gameProgressing) {
          gameProgressing = false;
          System.out.println();
        }
        GameResult result = (GameResult) packet.getData();
        if (!result.isRegular())
          irregularGames++;
        StringBuilder log = new StringBuilder("Game {} ended " +
            (result.isRegular() ? "regularly -" : "abnormally!") + " Winner: ");
        if (result.getWinners() != null)
          for (Player winner : result.getWinners())
            log.append(winner.getDisplayName()).append(", ");
        logger.warn(log.substring(0, log.length() - 2), finishedTests);

        finishedTests++;
        ScoreDefinition scoreDefinition = result.getDefinition();
        StringBuilder scoreUpdate = new StringBuilder(String.format("New scores after %s of %s games:", finishedTests, totalTests));
        for (int player = 0; player < players.length; player++) {
          PlayerScore score = result.getScores().get(player);
          ScoreValue[] scoreValues = players[player].score;
          for (int scoreIndex = 0; scoreIndex < scoreDefinition.getSize(); scoreIndex++) {
            ScoreValue value = scoreValues[scoreIndex];
            if (!result.getDefinition().get(scoreIndex).equals(value.getFragment())) {
              logger.error("Could not add current game result to scores. Score definition of player and result do not match.");
              throw new InvalidScoreDefinitionException("ScoreDefinition of player does not match expected score definition");
            }
            if(value.getFragment().getAggregation() == ScoreAggregation.AVERAGE)
              value.setValue(updateAverage(value.getValue(), finishedTests, score.getValues().get(scoreIndex)));
            else
              value.setValue(value.getValue().add(score.getValues().get(scoreIndex)));
          }
          scoreUpdate.append(String.format("%s: Siegpunkte %s, \u2205Wert 1 %5.2f",
              players[player].name, scoreValues[0].getValue(), scoreValues[1].getValue()));
        }
        logger.info(scoreUpdate.toString());

        if (isSignificant() || terminateWhenPossible) {
          printScores();
          exit(0);
        }

        try {
          for (ClientPlayer player : players)
            player.proc.waitFor(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
        for (ClientPlayer player : players)
          if (player.proc.isAlive()) {
            logger.warn("ClientPlayer {} is not responding anymore. Killing...", player.name);
            player.proc.destroyForcibly();
          }

        if (finishedTests == totalTests)
          terminateWhenPossible = true;
        else
          prepareNewClients();
      } else {
        if (logger.isInfoEnabled() && !logger.isTraceEnabled()) {
          if (!gameProgressing) {
            System.out.print("Game progress: ");
            gameProgressing = true;
          }
          System.out.print("#");
        }
      }
    } else if (message instanceof GamePreparedResponse) {
      logger.debug("Received PrepareGame - starting clients");
      playerScores = 0;
      GamePreparedResponse pgm = (GamePreparedResponse) message;
      send(new ObservationRequest(pgm.getRoomId()));
      try {
        for (int i = 0; i < 2; i++)
          startPlayer(i, pgm.getReservations().get((finishedTests + i) % 2));

        waiter.execute(() -> {
          int tests = finishedTests;
          int slept = 0;
          while (tests == finishedTests) {
            // Detect failed clients
            for (ClientPlayer player : players)
              if (!player.proc.isAlive()) {
                logger.error("{} crashed, look into {}", player.name, logDir);
                exit(2);
              }
            if (slept > plugin.getGameTimeout()) {
              logger.error("The game seems to hang, exiting!");
              exit(2);
            }
            try {
              Thread.sleep(1000);
              slept++;
            } catch (InterruptedException ignored) {
              break;
            }
          }
        });

      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (message instanceof ObservationResponse) {
      logger.debug("Successfully joined GameRoom as Observer");
    } else {
      logger.debug("Received uninteresting " + message.getClass().getSimpleName());
    }
  }

  private final int BIG_DECIMAL_SCALE = 6;
  /** Calculates a new average value: average = oldAverage * ((#amount - 1)/ #amount) + newValue / #amount */
  private BigDecimal updateAverage(BigDecimal oldAverage, int amount, BigDecimal newValue) {
    BigDecimal decAmount = new BigDecimal(amount);
    return oldAverage.multiply(decAmount.subtract(BigDecimal.ONE).divide(decAmount, BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP))
        .add(newValue.divide(decAmount, BIG_DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP))
        .round(new MathContext(BIG_DECIMAL_SCALE + 2));
  }

  private void startPlayer(int id, String reservation) throws IOException {
    ClientPlayer player = players[id];
    ProcessBuilder builder;
    if (player.isJar) {
      logger.debug("Invoking client {} with Java", player.name);
      builder = new ProcessBuilder("java", "-jar", "-mx1500m", player.executable.getAbsolutePath(), "-r", reservation, "-h", host, "-p", Integer.toString(port));
    } else {
      logger.debug("Invoking client {}", player.name);
      builder = new ProcessBuilder(player.executable.getAbsolutePath(), "--reservation", reservation, "--host", host, "--port", Integer.toString(port));
    }

    logDir.mkdirs();
    builder.redirectOutput(new File(logDir, players[id].name + "_game" + (finishedTests + 1) + ".log"));
    builder.redirectError(new File(logDir, players[id].name + "_game" + (finishedTests + 1) + "-err.log"));
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
      slots[(finishedTests + i) % 2] = new SlotDescriptor(players[i].name, players[i].canTimeout);
    logger.debug("Prepared client slots: " + Arrays.toString(slots));
    send(new PrepareGameRequest(plugin.getId(), slots[0], slots[1], false));
  }

  private static void exit(int status) {
    if (testclient != null) {
      testclient.stop();
      testclient.waiter.shutdownNow();
    }

    for (ClientPlayer player : players)
      if (player.proc != null)
        player.proc.destroyForcibly();

    if (status != 0)
      logger.warn("Terminating with exit code " + status);
    System.exit(status);
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
          players[0].name, players[0].score[0].getValue(),
          players[1].name, players[1].score[0].getValue()),
          irregularGames, finishedTests);
      scoresPrinted = true;
    } catch (Exception ignored) {
    }
  }

  private boolean isSignificant() {
    if (significance == null || finishedTests < minTests)
      return false;
    int n = finishedTests;
    IntToDoubleFunction binominalPD = (int k) -> pow(0.5, k) * pow(0.5, n - k) * (factorial(n, k) / factorial(n - k));
    players:
    for (int i = 0; i < 2; i++) {
      double binominalCD = 0.0;
      // TODO use global WIN_SCORE constant instead of hardcoding 2
      for (int k = 0; k <= players[i].score[0].getValue().intValue() / 2; k++) {
        binominalCD += binominalPD.applyAsDouble(k);
        if (binominalCD > significance)
          continue players;
      }
      logger.warn(String.format("%s is significantly better! Uncertainty: %.2f%%", players[(i + 1) % 2].name, binominalCD * 100));
      return true;
    }
    return false;
  }

  @Override
  public String shortString() {
    return String.format("TestClient(%d/%d)", finishedTests, totalTests);
  }

  @Override
  public String toString() {
    return String.format("TestClient{port: %d, tests: %d/%d, players: %s}", port, finishedTests, totalTests, Arrays.toString(players));
  }
}

class ClientPlayer {
  String name;
  boolean canTimeout;

  File executable;
  boolean isJar;

  @Override
  public String toString() {
    return String.format("ClientPlayer{name='%s', executable='%s', isJar=%s, canTimeout=%s}", name, executable, isJar, canTimeout);
  }

  Process proc;
  ScoreValue[] score;
}

class Util {

  static boolean isJar(File f) {
    return f.getName().endsWith("jar") && f.exists();
  }

  static double factorial(int n) {
    return n <= 1 ? 1 : factorial(n - 1) * n;
  }

  static double factorial(int n, int downTo) {
    return n <= downTo ? 1 : factorial(n - 1, downTo) * n;
  }

}
