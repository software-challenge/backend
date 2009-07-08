/**
 * 
 */
package sc.plugin2010.framework;

import java.io.IOException;

import sc.plugin2010.Board;
import sc.plugin2010.Client;

import com.thoughtworks.xstream.XStream;

/**
 * @author ffi
 * 
 */
public abstract class SpielClient implements IGameUpdateObserver
{

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
	public void spiellbrettAktualisiert(Board board)
	{
		// TODO Auto-generated method stub

	}
}
