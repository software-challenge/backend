/**
 * 
 */
package sc.plugin2010.framework;

import java.io.IOException;

import sc.plugin2010.Board;
import sc.plugin2010.Client;
import sc.plugin2010.EPlayerId;
import sc.plugin2010.Player;

/**
 * @author ffi
 * 
 */
public abstract class SpielClient implements IGameUpdateObserver
{
	private Spielbrett	spielbrett;
	private Spieler		spieler;
	private Spieler		gegner;	// TODO kara wegkapseln?

	public SpielClient(String ip, int port, String spielreservierung)
	{

		try
		{
			// verbinde beim starten
			Client client = new Client(ip, port, EPlayerId.PLAYER_ONE);
			Logik logik = new Logik(this, client);
			spieler.setLogik(logik);
			client.setHandler(logik);
			client.joinPreparedGame(spielreservierung);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void spiellbrettAktualisiert(Board board, int round)
	{
		spielbrett.update(board, round);
	}

	@Override
	public void spielerAktualisiert(Player player, boolean own)
	{
		if (own)
		{
			spieler.update(player);
		}
		else
		{
			gegner.update(player);
		}
	}

	/**
	 * @param gegner
	 */
	public void setzeGegner(Spieler gegner)
	{
		this.gegner = gegner;
	}

	/**
	 * @param spieler
	 */
	public void setzeSpieler(Spieler spieler)
	{
		this.spieler = spieler;
	}

	/**
	 * @param spielbrett
	 */
	public void setzeSpielbrett(Spielbrett spielbrett)
	{
		this.spielbrett = spielbrett;
	}
}
