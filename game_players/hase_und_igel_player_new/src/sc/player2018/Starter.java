package sc.player2018;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.framework.plugins.SimplePlayer;
import sc.player2018.logic.RandomLogic;
import sc.plugin2018.AbstractClient;
import sc.plugin2018.IGameHandler;
import sc.shared.SharedConfiguration;

import java.io.IOException;

/**
 * Hauptklasse des Clients, die ueber Konsolenargumente gesteuert werden kann.
 * Sie veranlasst eine Verbindung zum Spielserver und waehlt eine Strategie.
 *
 */
public class Starter extends AbstractClient {

	private static final Logger	logger = LoggerFactory
			.getLogger(Starter.class);
	
	public Starter(String host, int port, String reservation, String strategy)
			throws Exception {
		// client starten
		super(host, port);

		// strategie auswaehlen und zuweisen
		IGameHandler logic;

		// Versuche für den strategy-Parameter eine passende Logik zu
		// instanzieren, sonst verwende Standard
		try {
			logic = LogicFactory.valueOf(strategy.toUpperCase()).getInstance(
					this);
		} catch (IllegalArgumentException e) {
			logic = LogicFactory.DEFAULT.getInstance(this);
		}

		setHandler(logic);

		// einem spiel beitreten
		if (reservation == null || reservation.isEmpty()) {
			joinAnyGame();
		} else {
			joinPreparedGame(reservation);
		}

	}

	public static void main(String[] args) throws IllegalOptionValueException,
			UnknownOptionException, IOException {
		System.setProperty("file.encoding", "UTF-8");

		// XXX only for testing
    // you may use this code to enable debug output:
    Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    ((ch.qos.logback.classic.Logger)rootLogger).setLevel(ch.qos.logback.classic.Level.WARN);
    Logger randomlogiclogger = LoggerFactory.getLogger(RandomLogic.class);
    ((ch.qos.logback.classic.Logger)randomlogiclogger).setLevel(ch.qos.logback.classic.Level.WARN);

		// parameter definieren
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option hostOption = parser.addStringOption('h', "host");
		CmdLineParser.Option portOption = parser.addIntegerOption('p', "port");
		CmdLineParser.Option strategyOption = parser.addStringOption('s',
				"strategy");
		CmdLineParser.Option reservationOption = parser.addStringOption('r',
				"reservation");

		try {
			// Parameter auslesen
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) { // Bei Fehler die Hilfe
			// anzeigen
			showHelp(e.getMessage());
			System.exit(2);
		}

		// Parameter laden
		String host = (String) parser.getOptionValue(hostOption, "localhost");
		int port = (Integer) parser.getOptionValue(portOption,
				SharedConfiguration.DEFAULT_PORT);
		String reservation = (String) parser.getOptionValue(reservationOption,
				"");
		String strategy = (String) parser.getOptionValue(strategyOption, "");

		// einen neuen client erzeugen
		try {
			new Starter(host, port, reservation, strategy);
		} catch (Exception e) {
			logger.error("Beim Starten den Clients ist ein Fehler aufgetreten:");
			//System.err
			//		.println("Beim Starten den Clients ist ein Fehler aufgetreten:");
			e.printStackTrace();
		}

	}

	private static void showHelp(String errorMsg) {
		System.out.println();
		System.out.println(errorMsg);
		System.out.println();
		System.out
				.println("Bitte das Programm mit folgenden Parametern (optional) aufrufen: \n"
						+ "java -jar mississippi_queen_player.jar [{-h,--host} hostname]\n"
						+ "                               [{-p,--port} port]\n"
						+ "                               [{-r,--reservation} reservierung]\n"
						+ "                               [{-s,--strategy} strategie]");
		System.out.println();
		System.out
				.println("Beispiel: \n"
						+ "java -jar mississippi_queen_player.jar --host 127.0.0.1 --port 10500 --reservation MQ --strategy RANDOM");
		System.out.println();
	}

	@Override
	public void onGamePaused(String roomId, SimplePlayer nextPlayer) {

	}

	@Override
  public void onGameObserved(String roomId) {
    // is called when a observation request is acknowledged by the server
    // this is a newly added method, I am not sure if it fits into the architecture
  }
}
