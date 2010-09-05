package sc.player2011.starter;

import jargs.gnu.CmdLineParser;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.plugin2011.AbstractClient;
import sc.shared.SharedConfiguration;
import tkra.classfinder.ClassFinderHelper;

public class ClientStarter {

	private static Logger logger = LoggerFactory.getLogger(ClientStarter.class);

	@SuppressWarnings("unchecked")
	public AbstractHandler getHandler(String name, AbstractClient client)
			throws Exception {

		name = name.toLowerCase();
		logger.info("searching for strategy-class with name '" + name + "'");

		String standardName = null;
		Class<? extends AbstractHandler> standardStrategy = null;
		Map<String, Class<? extends AbstractHandler>> strategies = new HashMap<String, Class<? extends AbstractHandler>>();
		for (Class<?> clazz : ClassFinderHelper.getClasses(ClassFinderHelper
				.getClassNames("sc.player2011", true))) {

			// System.out.println("*** teste klasse " + c.getName());
			if (clazz.getSuperclass() != null
					&& clazz.getSuperclass().equals(AbstractHandler.class)) {
				// System.out.println("*** teste strategieklasse " +
				// c.getName());
				Class<? extends AbstractHandler> handler = (Class<? extends AbstractHandler>) clazz;
				if (clazz.isAnnotationPresent(StrategyIdentifier.class)) {
					StrategyIdentifier annotation = handler
							.getAnnotation(StrategyIdentifier.class);
					logger.info("found strategy-class '"
							+ handler.getCanonicalName() + "' as '"
							+ annotation.name() + "'");

					// strategie hinzufuegen
					if (strategies.containsKey(annotation.name())) {
						logger
								.warn("found multiple strategy-classes with name '"
										+ annotation.name() + "'");
					}
					strategies.put(annotation.name().toLowerCase(), handler);

					// auf standard-attribut pruefen
					if (annotation.standard()) {
						if (standardStrategy != null) {
							logger
									.warn("found multiple standard strategy-classes");
						}
						standardName = annotation.name();
						standardStrategy = handler;
					}

				}
			}

		}

		if (strategies.isEmpty()) {
			logger.error("no strategy-classfound");
			System.exit(1);
		}

		Class<? extends AbstractHandler> strategy = strategies.get(name);
		if (strategy == null && standardStrategy != null) {
			strategy = standardStrategy;
			logger.warn("strategy-class with name '" + name
					+ "' not found. using standard startegy '" + standardName
					+ "'");
		} else if (strategy == null && standardStrategy == null) {
			logger.error("strategy-class with name '" + name
					+ "' not found. no standard strategy present");
			System.exit(1);
		}

		StrategyIdentifier annotation = strategy
				.getAnnotation(StrategyIdentifier.class);
		logger.info("playing with strategy-class '"
				+ strategy.getCanonicalName() + "' as '" + annotation.name()
				+ "'");

		return strategy.getConstructor().newInstance();

	}

	public ClientStarter(String host, int port, String reservation, String name)
			throws Exception {

		AbstractClient client = new AbstractClient(host, port) {
		};

		AbstractHandler handler = getHandler(name, client);
		handler.setClient(client);
		client.setHandler(handler);

		// einem spiel beitreten
		if (reservation == null || reservation.isEmpty()) {
			client.joinAnyGame();
		} else {
			client.joinPreparedGame(reservation);
		}

	}

	public static void main(String[] args) throws Exception {
		System.setProperty("file.encoding", "UTF-8");

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
		new ClientStarter(host, port, reservation, strategy);

	}

	private static void showHelp(String errorMsg) {
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
						+ "java -jar schaefchen_player.jar --host 127.0.0.1 --port 10500 --reservation SCHAEFCHEN --strategy simple");
		System.err.println();
	}
}
