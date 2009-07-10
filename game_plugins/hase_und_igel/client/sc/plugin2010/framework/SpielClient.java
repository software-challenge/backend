/**
 * 
 */
package sc.plugin2010.framework;

import java.io.IOException;

import sc.plugin2010.BoardUpdated;
import sc.plugin2010.Client;
import sc.plugin2010.PlayerUpdated;

import com.thoughtworks.xstream.XStream;

/**
 * @author ffi
 * 
 */
public abstract class SpielClient implements IGameUpdateObserver
{
	private Spielbrett	spielbrett;
	private Spieler		spieler;
	private Gegner		gegner;

	public SpielClient(String ip, int port)
	{
		// verbinde beim starten
		try
		{
			Client client = new Client("Hase und Igel", new XStream(), ip, port);
			Logik logik = new Logik(this);
			client.setHandler(logik);
			client.joinAnyGame();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void spiellbrettAktualisiert(BoardUpdated bu)
	{
		spielbrett.update(bu);
	}

	public void spielerAktualisiert(PlayerUpdated pu)
	{
		spieler.update(pu);
	}
}
