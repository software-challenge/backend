package sc.plugin2010.framework;

import java.io.IOException;

import sc.networking.clients.LobbyClient;
import sc.plugin2010.Board;
import sc.plugin2010.Client;
import sc.plugin2010.EPlayerId;
import sc.plugin2010.Player;

/**
 * Diese Klasse übernimmt das initalisieren des Spieles und des Netzwerkclients.
 * 
 * @author ffi
 * 
 */
public abstract class SpielClient implements IGameUpdateObserver
{
	private Spielbrett	spielbrett;
	private Spieler		spieler;
	private Spieler		gegner;
	private Logik		logik;
	private String		spielreservierung;
	private Client		client;

	/**
	 * Startet Netzwerkclient und setzt benötigte Referenzen und Klassen
	 * 
	 * @param host
	 *            der Zielhost auf dem der Server läuft. Typischerweise
	 *            localhost
	 * @param port
	 *            der Zielport auf dem der Server hört.
	 * @param spielreservierung
	 *            Falls eine Spielreservierung besteht, dann tritt diesem Spiel
	 *            bei
	 */
	public SpielClient(String host, int port, String spielreservierung)
	{
		if (host == null || host.isEmpty())
		{
			host = LobbyClient.DEFAULT_HOST;
		}

		if (port == 0)
		{
			port = LobbyClient.DEFAULT_PORT;
		}

		try
		{
			// verbinde beim starten
			client = new Client(host, port, EPlayerId.PLAYER_ONE);
			logik = new Logik(this, client);
			client.setHandler(logik);
			this.spielreservierung = spielreservierung;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void spiellbrettAktualisiert(Board board, int round)
	{
		if (spielbrett != null)
		{
			spielbrett.update(board, round);
		}
	}

	public void starteSpiel()
	{
		if (spielreservierung == null || spielreservierung.isEmpty())
		{
			client.joinAnyGame();
		}
		else
		{
			client.joinPreparedGame(spielreservierung);
		}
	}

	@Override
	public void spielerAktualisiert(Player player, Player otherPlayer)
	{
		if (spieler != null && gegner != null)
		{
			spieler.update(player);
			gegner.update(otherPlayer);
		}
	}

	/**
	 * Setzt die Gegner Referenz für das Spiel
	 * 
	 * @param gegner
	 *            der andere Spieler
	 */
	public void setzeGegner(Spieler gegner)
	{
		this.gegner = gegner;
	}

	/**
	 * Setzt die Spieler Referenz für das Spiel
	 * 
	 * @param spieler
	 *            der eigene Spieler
	 */
	public void setzeSpieler(Spieler spieler)
	{
		this.spieler = spieler;
		spieler.setLogik(logik);
	}

	/**
	 * Setzt die Spielbrett Referenz für das Spiel
	 * 
	 * @param spielbrett
	 *            das Spielbrett
	 */
	public void setzeSpielbrett(Spielbrett spielbrett)
	{
		this.spielbrett = spielbrett;
	}
}
