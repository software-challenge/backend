package sc;

import com.thoughtworks.xstream.XStream;
import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.networking.INetworkInterface;
import sc.networking.TcpNetwork;
import sc.networking.clients.XStreamClient;
import sc.protocol.LobbyProtocol;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.server.Configuration;
import sc.shared.GameResult;
import sc.shared.Score;
import sc.shared.SharedConfiguration;
import sc.shared.SlotDescriptor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
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

  private static String gameType = "swc_2018_hase_und_igel";
  
  private static String displayName1 = "player1";
  private static String displayName2 = "player2";
  private static boolean canTimeout1;
  private static boolean canTimeout2;
  private static String p1;
  private static String p2;
  
  /** the current host */
  private String host;
  /** the current port */
  private int port;

  private int currentTests;
  private int numberOfTests;

  private boolean terminateWhenPossible = false;
  private int gotLastPlayerScores = 0;

  private List<Score> scores;

  private Process proc1;
  private Process proc2;

  private static final Logger logger = LoggerFactory.getLogger(TestClient.class);

  public TestClient(XStream xstream, Collection<Class<?>> protocolClasses,
                    String host, int port, int numberOfTests) throws IOException {
    super(xstream, createTcpNetwork(host, port));
    scores = new ArrayList<>(2);
    LobbyProtocol.registerMessages(xstream);
    LobbyProtocol.registerAdditionalMessages(xstream, protocolClasses);
    this.host = host;
    this.port = port;
    this.numberOfTests = numberOfTests;
    start();
    logger.debug("Authenticating as administrator");
    send(new AuthenticateRequest(Configuration.getAdministrativePassword()));
    logger.debug("Enabling TestMode");
    send(new TestModeRequest(true));
    logger.info("Waiting for input of displayName to print players current Score");
  }

  public static void main(String[] args) {
    System.setProperty("file.encoding", "UTF-8");
    // define parameters
    CmdLineParser parser = new CmdLineParser();
    Option hostOption = parser.addStringOption('h', "host");
    Option portOption = parser.addIntegerOption('p', "port");
    Option numberOfTestsOption = parser.addIntegerOption("tests");
    Option p1Option = parser.addStringOption("player1");
    Option p2Option = parser.addStringOption("player2");
    Option name1Option = parser.addStringOption("name1");
    Option name2Option = parser.addStringOption("name2");
    Option p1CanTimeoutOption = parser.addBooleanOption("timeout1");
    Option p2CanTimeoutOption = parser.addBooleanOption("timeout2");
    new File("logs").mkdirs();

    try {
      parser.parse(args);
    } catch (CmdLineParser.OptionException e) {
      logger.error("Invalid option: " + e.getMessage());
      e.printStackTrace();
      System.exit(2);
    }
    
    // TODO make static method in sdk for methods like this
    try {
      logger.info("Loading server.properties");
      Configuration.load(new FileReader("server.properties"));
    } catch (IOException e) {
      logger.error("Could not find server.properties");
      e.printStackTrace();
      return;
    }

    // Parameter laden
    String host = (String) parser.getOptionValue(hostOption, "localhost");
    int port = (Integer) parser.getOptionValue(portOption,
            SharedConfiguration.DEFAULT_PORT);
    int numberOfTests = (Integer) parser.getOptionValue(numberOfTestsOption, 10);
    canTimeout1 = (Boolean) parser.getOptionValue(p1CanTimeoutOption, true);
    canTimeout2 = (Boolean) parser.getOptionValue(p2CanTimeoutOption, true);
    displayName1 = (String) parser.getOptionValue(name1Option, "player1");
    displayName2 = (String) parser.getOptionValue(name2Option, "player2");
    p1 = (String) parser.getOptionValue(p1Option, "./defaultplayer.jar");
    p2 = (String) parser.getOptionValue(p2Option, "./defaultplayer.jar");

    File player1File = new File(p1);
    File player2File = new File(p2);

    if (!player1File.exists()) {
      logger.error("Player1 could not be found ({}).", p1);
      return;
    } else if (!player2File.exists()) {
      logger.error("Player2 could not be found ({}).", p2);
      return;
    }

    try {
      new TestClient(Configuration.getXStream(), sc.plugin2018.util.Configuration.getClassesToRegister(), host, port, numberOfTests);
    } catch (Exception e) {
      logger.error("Error on startup:");
      e.printStackTrace();
    }
  }

  @Override
  protected void onObject(ProtocolMessage message) {
    if (message == null) {
      logger.warn("Received null message");
      return;
    }

    logger.trace("Received " + message);
    if (message instanceof TestModeMessage) { // for handling testing
      boolean testMode = (((TestModeMessage) message).testMode);
      logger.debug("TestMode was set to {} - starting clients", testMode);
      prepareNewClients();
    }
    if (message instanceof RoomPacket) {
      RoomPacket packet = (RoomPacket) message;
      if (packet.getData() instanceof GameResult) {
        logger.warn("Received game result");
        this.currentTests++;
        send(new GetScoreForPlayerRequest(displayName1));
        send(new GetScoreForPlayerRequest(displayName2));

        //Wait until everything is finished and clear
        try {
	        proc1.waitFor();
			    proc2.waitFor();
		    } catch (InterruptedException e) {
			    e.printStackTrace();
		    }
        proc1.destroy();
        proc2.destroy();
        if (this.currentTests == this.numberOfTests) {
          terminateWhenPossible = true;
        }

        prepareNewClients();
      }
    } else if (message instanceof PlayerScorePacket) {
      if (terminateWhenPossible)
        gotLastPlayerScores++;
      Score score = ((PlayerScorePacket) message).getScore();
      logger.warn(score.toString());
      // add score to list
      if (this.scores.size() < 2) {
        scores.add(score);
      } else {
        for (Score s : this.scores) {
          if (s.getDisplayName().equals(score.getDisplayName())) {
            scores.add(score);
            scores.remove(s);
            break;
          }
        }
      }
      logger.warn("Received new score for " + score.getDisplayName() + ": Siegpunkte " + score.getScoreValues().get(0).getValue() +
              ", \u2205 Feldnummer " + score.getScoreValues().get(1).getValue() +
              ", \u2205 Karotten " + score.getScoreValues().get(2).getValue() + " after " + currentTests + " of " + numberOfTests + " tests");
      if (gotLastPlayerScores == 2) {
        if (this.currentTests == this.numberOfTests) {
          logger.warn("End results: \n" +
                  "=============== SCORES ================\n" +
                  displayName1 + ": " + scores.get(0).getScoreValues().get(0).getValue() + "\n" +
                  displayName2 + ": " + scores.get(1).getScoreValues().get(0).getValue() + "\n" +
                  "=======================================");
        }
        send(new CloseConnection());
      }

    } else if (message instanceof PrepareGameProtocolMessage) {
      logger.info("Starting clients");
      PrepareGameProtocolMessage pgm = (PrepareGameProtocolMessage) message;
      send(new ObservationRequest(pgm.getRoomId()));
      try {
        ProcessBuilder builder1;
        if (isJar(TestClient.p1)) {
          logger.info("Running first client {} as java client...", TestClient.p1);
          builder1 = new ProcessBuilder("java", "-jar", TestClient.p1, "-r", pgm.getReservations().get(currentTests % 2), "-h", host, "-p", Integer.toString(port));
        } else {
          logger.info("Running first client {} as non java client...", TestClient.p1);
          builder1 = new ProcessBuilder(TestClient.p1, "--reservation", pgm.getReservations().get(currentTests % 2), "--host", host, "--port", Integer.toString(port));
        }
        builder1.redirectOutput(new File("logs" + File.separator + TestClient.displayName1 + "_Test" + currentTests + ".log"));
        builder1.redirectError(new File("logs" + File.separator + TestClient.displayName1 + "_Test" + currentTests + ".err"));
        proc1 = builder1.start();
        Thread.sleep(100);

        ProcessBuilder builder2;
        if (isJar(TestClient.p2)) {
          logger.info("Running second client {} as java client...", TestClient.p2);
          builder2 = new ProcessBuilder("java", "-jar", TestClient.p2, "-r", pgm.getReservations().get((currentTests + 1) % 2), "-h", host, "-p", Integer.toString(port));
        } else {
          logger.info("Running second client {} as non java client...", TestClient.p2);
          builder2 = new ProcessBuilder(TestClient.p2, "--reservation", pgm.getReservations().get((currentTests + 1) % 2), "--host", host, "--port", Integer.toString(port));
        }
        builder2.redirectOutput(new File("logs" + File.separator + TestClient.displayName2 + "_Test" + currentTests + ".log"));
        builder2.redirectError(new File("logs" + File.separator + TestClient.displayName2 + "_Test" + currentTests + ".err"));
        proc2 = builder2.start();
        Thread.sleep(100);

        if (!proc1.isAlive()) {
          logger.error("{} could not be started", displayName1);
          terminate();
        } else if (!proc2.isAlive()) {
          logger.error("{} could not be started", displayName2);
          terminate();
        }
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    } else {
      logger.debug("Received uninteresting " + message.getClass().getSimpleName());
    }
  }

  private void terminate() {
    logger.warn("TERMINATING");
    proc1.destroyForcibly();
    proc2.destroyForcibly();
    System.exit(1);
  }

  private static INetworkInterface createTcpNetwork(String host, int port) throws IOException {
    logger.info("Creating TCP Network for {}:{}", host, port);
    return new TcpNetwork(new Socket(host, port));
  }

  /** prepares slots for new clients (if {@link #currentTests} is even player1 starts, otherwise player2) */
  private void prepareNewClients() {
    if (currentTests == numberOfTests) {
      return;
    }
    SlotDescriptor slot1;
    SlotDescriptor slot2;
    if (currentTests % 2 == 0) {
      slot1 = new SlotDescriptor(displayName1, canTimeout1, false);
      slot2 = new SlotDescriptor(displayName2, canTimeout2, false);
    } else {
      slot1 = new SlotDescriptor(displayName2, canTimeout2, false);
      slot2 = new SlotDescriptor(displayName1, canTimeout1, false);
    }
    send(new PrepareGameRequest(gameType, slot1, slot2));
  }

  private static boolean isJar(String f) {
    return f.endsWith("jar");
  }

}
