package sc.plugin2010.shared;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameListener;
import sc.api.plugins.IPlayer;
import sc.api.plugins.TooManyPlayersException;
import sc.plugin2010.core.GamePlugin;
import sc.plugin2010.shared.Player.FigureColor;

/**
 * @author rra
 * @since Jul 4, 2009
 *
 */
public class Game implements IGameInstance
{
	private Board				board;

	private List<Player>		players;

	private int					activePlayerId;

	private Set<IGameListener>	listeners;
	
	private List<FigureColor>	availableColors;

	public Game()
	{
		players = new LinkedList<Player>();
		listeners = new HashSet<IGameListener>();
		
		availableColors = new LinkedList<FigureColor>();
		availableColors.addAll(Arrays.asList(FigureColor.values()));
	}
	
	@Override
	public void actionReceived(IPlayer fromPlayer, Serializable data)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void addGameListener(IGameListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized IPlayer playerJoined() throws TooManyPlayersException
	{
		Player p = null;
		if (players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();

		p = new Player(availableColors.remove(0));
		players.add(p);
		
		return p;
	}

	@Override
	public void playerLeft(IPlayer player)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGameListener(IGameListener listener)
	{
		listeners.remove(listener);
	}

}
