package sc;

import com.thoughtworks.xstream.XStream;
import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.networking.INetworkInterface;
import sc.networking.TcpNetwork;
import sc.networking.UnprocessedPacketException;
import sc.networking.clients.XStreamClient;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.server.Configuration;
import sc.protocol.LobbyProtocol;
import sc.shared.GameResult;
import sc.shared.Score;
import sc.shared.SharedConfiguration;
import sc.shared.SlotDescriptor;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to start testing. Enables testMode on startUp. Uses displayNames player1 and player2 as default.
 * Client default location is "./../simple_client/hase_und_igel_player_new/jar/hase_und_igel_player_new.jar"
 * canTimeOut is true per default. Starts per default on localhost 13050 if not specified.
 */
public class TestClient extends XStreamClient {

  private static String gameType = "swc_2018_hase_und_igel";
  private static String displayName1 = "player1";
  private static String displayName2 = "player2";
  private static boolean canTimeout1;
  private static boolean canTimeout2;
  private static String p1;
  private static String p2;
  // the current host
  private String host;
  // the current port
  private int port;

  private int numberOfTests;

  private int currentTests;

  private boolean terminateWhenPossible = false;
  private int gotLastPlayerScores = 0;

  private List<Score> scores;

  private Process proc1;
  private Process proc2;

  private static final Logger logger = LoggerFactory
          .getLogger(TestClient.class);

  public TestClient(XStream xstream, Collection<Class<?>> protocolClasses,
                    String host, int port, int numberOfTests) throws IOException {
    super(xstream, createTcpNetwork(host, port));
    scores = new LinkedList<>();
    LobbyProtocol.registerMessages(xstream);
    LobbyProtocol.registerAdditionalMessages(xstream, protocolClasses);
    this.host = host;
    this.port = port;
    this.numberOfTests = numberOfTests;
    start();
    logger.info("Authenticate as administrator");
    send(new AuthenticateRequest(Configuration.getAdministrativePassword()));
    logger.info("Enabling TestMode");
    send(new TestModeRequest(true));
    logger.info("Waiting for input of displayName to print players current Score");
  }

  public static void main(String[] args) throws IllegalOptionValueException,
          UnknownOptionException, IOException {
    System.setProperty("file.encoding", "UTF-8");
    // define parameters
    CmdLineParser parser = new CmdLineParser();
    CmdLineParser.Option hostOption = parser.addStringOption('h', "host");
    CmdLineParser.Option portOption = parser.addIntegerOption('p', "port");
    CmdLineParser.Option numberOfTestsOption = parser.addIntegerOption("tests");
    CmdLineParser.Option p1Option = parser.addStringOption("player1");
    CmdLineParser.Option p2Option = parser.addStringOption("player2");
    CmdLineParser.Option name1Option = parser.addStringOption("name1");
    CmdLineParser.Option name2Option = parser.addStringOption("name2");
    CmdLineParser.Option p1CanTimeoutOption = parser.addBooleanOption("timeout1");
    CmdLineParser.Option p2CanTimeoutOption = parser.addBooleanOption("timeout2");

    try {
      parser.parse(args);
    } catch (CmdLineParser.OptionException e) {
      System.exit(2);
    }
    // TODO make static method in sdk for methods like this
    try {
      logger.error("loading server.properties");
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
    int numberOfTests = (Integer) parser.getOptionValue(numberOfTestsOption, 100);
    canTimeout1 = (Boolean) parser.getOptionValue(p1CanTimeoutOption, true);
    canTimeout2 = (Boolean) parser.getOptionValue(p2CanTimeoutOption, true);
    displayName1 = (String) parser.getOptionValue(name1Option, "player1");
    displayName2 = (String) parser.getOptionValue(name2Option, "player2");
    p1 = (String) parser.getOptionValue(p1Option, "./../simple_client/hase_und_igel_player_new/jar/hase_und_igel_player_new.jar");
    p2 = (String) parser.getOptionValue(p2Option, "./../simple_client/hase_und_igel_player_new/jar/hase_und_igel_player_new.jar");
    // einen neuen client erzeugen
    try {
      new TestClient(Configuration.getXStream(), sc.plugin2018.util.Configuration.getClassesToRegister(), host, port, numberOfTests);
    } catch (Exception e) {
      logger.error("Error on startup:");
      e.printStackTrace();
    }
  }

  @Override
  protected void onObject(ProtocolMessage o) throws UnprocessedPacketException {
    if (o == null) {
      logger.warn("Received null object.");
      return;
    }

    if (o instanceof TestModeMessage) { // for handling testing
      boolean testMode = (((TestModeMessage) o).testMode);
      logger.info("TestMode was set to {}, starting clients", testMode);
      startNewClients();
    }
    if (o instanceof RoomPacket) {
      RoomPacket packet = (RoomPacket) o;
      if (packet.getData() instanceof GameResult) {
        logger.warn("Received game result");
        this.currentTests++;
        send(new GetScoreForPlayerRequest(displayName1));
        send(new GetScoreForPlayerRequest(displayName2));
        proc1.destroyForcibly();
        proc2.destroyForcibly();
        if (this.currentTests == this.numberOfTests) {
          terminateWhenPossible = true;
        }

        // start new clients (if currentTests is even first player starts)
        startNewClients();
      }
    } else if (o instanceof PlayerScorePacket) {
      if (terminateWhenPossible) {
        gotLastPlayerScores++;
      }
      Score score = ((PlayerScorePacket) o).getScore();
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
                      ", \u2205 Karotten " + score.getScoreValues().get(2).getValue() + " after " + currentTests +  " of " + numberOfTests + " tests");
      if (gotLastPlayerScores == 2) {
        send(new CloseConnection());
      }
    } else if (o instanceof PrepareGameProtocolMessage) {
      logger.info("Trying to start clients");
      PrepareGameProtocolMessage pgm = (PrepareGameProtocolMessage) o;
      send(new ObservationRequest(pgm.getRoomId()));
      try {
        logger.info("Trying first client {}", TestClient.p1);
        String startClient1 = "java -jar " + TestClient.p1 + " -r " + pgm.getReservations().get(currentTests % 2) + " -h " + host + " -p " + port;
        String startClient2 = "java -jar " + TestClient.p2 + " -r " + pgm.getReservations().get((currentTests + 1) % 2) + " -h " + host + " -p " + port;
        proc1 = Runtime.getRuntime().exec(startClient1);
        logger.info("Trying second client {}", TestClient.p2);
        proc2 = Runtime.getRuntime().exec(startClient2);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      logger.info("Received packet I am not interested in");
    }
  }

  private static INetworkInterface createTcpNetwork(String host, int port)
          throws IOException {
    logger.info("Creating TCP Network for {}:{}", host, port);
    return new TcpNetwork(new Socket(host, port));
  }

  private void startNewClients() {
    if (currentTests == numberOfTests) {
      return;
    }
    SlotDescriptor slot1;
    SlotDescriptor slot2;
    if (currentTests % 2 == 0) {
      slot1 = new SlotDescriptor(TestClient.displayName1, TestClient.canTimeout1, false);
      slot2 = new SlotDescriptor(TestClient.displayName2, TestClient.canTimeout2, false);
    } else {
      slot1 = new SlotDescriptor(TestClient.displayName2, TestClient.canTimeout2, false);
      slot2 = new SlotDescriptor(TestClient.displayName1, TestClient.canTimeout1, false);
    }
    send(new PrepareGameRequest(TestClient.gameType, slot1, slot2));
  }
}
