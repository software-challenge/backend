package sc.player2010;

import sc.shared.SharedConfiguration;
import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;

public class Starter
{
	/**
	 * Diese Methode wird beim Start des Programmes aufgerufen. Die Parameter
	 * werden für das automatische Verbinden benötigt.
	 * 
	 * @param args
	 * @throws UnknownOptionException
	 * @throws IllegalOptionValueException
	 */
	public static void main(String[] args) throws IllegalOptionValueException,
			UnknownOptionException
	{
		System.setProperty( "file.encoding", "UTF-8" );
		
		// Parameter definieren
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option hostOption = parser.addStringOption('h', "host");
		CmdLineParser.Option portOption = parser.addIntegerOption('p', "port");
		CmdLineParser.Option reservationOption = parser.addStringOption('r',
				"reservation");

		try
		{
			// Parameter auslesen
			parser.parse(args);
		}
		catch (CmdLineParser.OptionException e)
		{
			// Bei Fehler die Hilfe anzeigen
			zeigeHilfe(e.getMessage());
			System.exit(2);
		}

		// Parameter laden
		String host = (String) parser.getOptionValue(hostOption, "localhost");
		int port = (Integer) parser.getOptionValue(portOption, SharedConfiguration.DEFAULT_PORT);
		String reservierung = (String) parser.getOptionValue(reservationOption,
				"");

		// Client starten
		new SimpleClient(host, port, reservierung);
	}

	private static void zeigeHilfe(String fehlerNachricht)
	{
		System.err.println();
		System.err.println(fehlerNachricht);
		System.err.println();
		System.err
				.println("Bitte das Programm mit folgenden Parametern (optional) aufrufen: \n"
						+ "java -jar HaseUndIgelSC.jar [{-h,--host} hostname]\n"
						+ "                            [{-p,--port} port]\n"
						+ "                            [{-r,--reservation} reservierung]");
		System.err.println();
		System.err
				.println("Beispiel: \n"
						+ "java -jar HaseUndIgelSC.jar --host 127.0.0.1 --port 10500 --reservation HASEUNDIGEL");
		System.err.println();
	}
}
