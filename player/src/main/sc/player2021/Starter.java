package sc.player2021;

import jargs.gnu.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.player2021.logic.Logic;
import sc.player.PlayerClient;
import sc.player.IGameHandler;
import sc.shared.SharedConfiguration;

import java.io.File;

/**
 * Hauptklasse des Clients, die über Konsolenargumente gesteuert werden kann.
 * Sie veranlasst eine Verbindung zum Spielserver.
 */
public class Starter {
  private static final Logger logger = LoggerFactory.getLogger(Starter.class);

  public Starter(String host, int port, String reservation, String roomId) {
    // Strategie zuweisen
    IGameHandler logic = new Logic();
    PlayerClient client = new PlayerClient(host, port, logic);

    // einem Spiel beitreten
    if (reservation != null && !reservation.isEmpty()) {
      client.joinPreparedGame(reservation);
    } else if (roomId != null) {
      client.joinGameRoom(roomId);
    } else {
      client.joinAnyGame();
    }
  }

  public static void main(String[] args) {
    System.setProperty("file.encoding", "UTF-8");

    // Parameter definieren
    CmdLineParser parser = new CmdLineParser();
    CmdLineParser.Option hostOption = parser.addStringOption('h', "host");
    CmdLineParser.Option portOption = parser.addIntegerOption('p', "port");
    CmdLineParser.Option reservationOption = parser.addStringOption('r', "reservation");
    CmdLineParser.Option roomOption = parser.addStringOption("room");

    try {
      // Parameter auslesen
      parser.parse(args);
    } catch (CmdLineParser.OptionException e) {
      // bei einem Fehler die Hilfe anzeigen
      showHelp(e.getMessage());
      System.exit(2);
    }

    // Parameter laden
    String host = (String) parser.getOptionValue(hostOption, "localhost");
    int port = (Integer) parser.getOptionValue(portOption, SharedConfiguration.DEFAULT_PORT);
    String reservation = (String) parser.getOptionValue(reservationOption);
    String room = (String) parser.getOptionValue(roomOption);

    // einen neuen client erzeugen
    try {
      new Starter(host, port, reservation, room);
    } catch (Exception e) {
      logger.error("Beim Starten den Clients ist ein Fehler aufgetreten:", e);
      e.printStackTrace();
    }

  }

  private static void showHelp(String errorMsg) {
    String jarName = new File(Starter.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getName();
    System.out.println("\n" + errorMsg);
    System.out.println("\nFolgende Parameter sind erlaubt: \n"
            + "java -jar " + jarName + " [{-h,--host} hostname]\n"
            + "                               [{-p,--port} port]\n"
            + "                               [{-r,--reservation} reservierung]\n"
            + "                               [--room raumnummer]\n");
    System.out.println("Beispiel: \n"
            + "java -jar " + jarName + " --host 127.0.0.1 --port 10500 --reservation 1234\n");
  }

}
