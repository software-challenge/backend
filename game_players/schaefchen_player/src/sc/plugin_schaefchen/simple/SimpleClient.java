package sc.plugin_schaefchen.simple;

import java.io.IOException;

import sc.plugin_schaefchen.AbstractClient;
import sc.plugin_schaefchen.IGameHandler;
import sc.shared.SharedConfiguration;
import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

public class SimpleClient extends AbstractClient
{

	public SimpleClient(String host, int port, String reservation, String strategy)
			throws IOException
	{
		// client starten
		super(host, port);

		// strategie auswaehlen und zuweisen
		IGameHandler logic;
		if (strategy != null && strategy.toUpperCase().equals("GREEDY"))
		{
			logic = new GreedyLogic(this);
		}
		else if (strategy != null && strategy.toUpperCase().equals("RANDOM"))
		{
			logic = new RandomLogic(this);
		}
		else
		{
			// wenn keine andere strategie angegeben wurde
			logic = new GreedyLogic(this);
		}
		setHandler(logic);
		
		// einem spiel beitreten
		if (reservation == null || reservation.isEmpty())
		{
			joinAnyGame();
		}
		else
		{
			joinPreparedGame(reservation);
		}

	}

	public static void main(String[] args) throws IllegalOptionValueException,
			UnknownOptionException, IOException
	{
		System.setProperty("file.encoding", "UTF-8");

		// parameter definieren
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option hostOption = parser.addStringOption('h', "host");
		CmdLineParser.Option portOption = parser.addIntegerOption('p', "port");
		CmdLineParser.Option strategyOption = parser.addStringOption('s',
				"strategy");
		CmdLineParser.Option reservationOption = parser.addStringOption('r',
				"reservation");

		try
		{
			// Parameter auslesen
			parser.parse(args);
		}
		catch (CmdLineParser.OptionException e)
		{ // Bei Fehler die Hilfe anzeigen
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
		new SimpleClient(host, port, reservation, strategy);
		
	}

	private static void showHelp(String errorMsg)
	{
		System.err.println();
		System.err.println(errorMsg);
		System.err.println();
		System.err
				.println("Bitte das Programm mit folgenden Parametern (optional) aufrufen: \n"
						+ "java -jar schaefchen_player.jar [{-h,--host} hostname]\n"
						+ "                                [{-p,--port} port]\n"
						+ "                                [{-r,--reservation} reservierung]\n"
						+ "                                [{-s,--strategy} strategie]");
		System.err.println();
		System.err
				.println("Beispiel: \n"
						+ "java -jar schaefchen_player.jar --host 127.0.0.1 --port 10500 --reservation SCHAEFCHEN --strategy RANDOM");
		System.err.println();
	}
}
