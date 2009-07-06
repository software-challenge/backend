package sc.plugin2010;

import java.io.IOException;
import java.util.List;

import sc.framework.plugins.protocol.MoveRequest;
import sc.protocol.LobbyClient;

import com.thoughtworks.xstream.XStream;

/**
 * Der Client für das Hase- und Igel Plugin.
 * 
 * @author rra
 * @since Jul 5, 2009
 * 
 */
public abstract class Client extends LobbyClient
{
	// Das aktuelle Spielbrett
	private Board			board;
	
	// Der eigene Spieler
	private Player			player;
	
	// Alle anderen Spieler
	private List<Player>	players;

	public Client(String gameType, XStream xstream, String host, int port)
			throws IOException
	{
		super(gameType, xstream, host, port);
	}

	@Override
	protected void onRoomMessage(String roomId, Object data)
	{
		if (data instanceof BoardUpdated)
		{
			board = ((BoardUpdated)data).getBoard();
			
			onUpdate();
		}
		else if (data instanceof PlayerUpdated)
		{
			final Player p = ((PlayerUpdated) data).getPlayer();
			
			if (((PlayerUpdated) data).isOwnPlayer())
			{
				// Aktualisiere den eigenen Spieler
				player = p;
			} else {
				// Aktualisiere einen anderen Spieler
				for(final Player pl : players)
				{
					if (pl.getColor().equals(p.getColor()))
					{
						players.remove(pl);
					}
				}
				
				players.add(p);
			}
			
			onUpdate();
		}
		else if (data instanceof MoveRequest)
		{
			this.sendMessageToRoom(roomId, doMove());
		}
	}

	/**
	 * Das für die aktuelle Runde gültige Spielbrett
	 * 
	 * @return
	 */
	public final Board getBoard()
	{
		return board;
	}

	/**
	 * Der aktuelle Zustand des eigenen Spielers
	 * 
	 * @return
	 */
	public final Player getPlayer()
	{
		return player;
	}

	/**
	 * Berechnet den Zug des Clienten.
	 * 
	 * @return
	 */
	abstract Move doMove();
	
	/**
	 * Spieler oder Spielbrett aktualisiert.
	 */
	abstract void onUpdate();
}
