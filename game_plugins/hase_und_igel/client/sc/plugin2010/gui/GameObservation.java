/**
 * 
 */
package sc.plugin2010.gui;

import java.util.LinkedList;
import java.util.List;

import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.plugin2010.Client;

/**
 * @author ffi
 * 
 */
public class GameObservation implements IObservation
{
	private Client						client;

	private List<IGameEndedListener>	gameEndedListeners	= new LinkedList<IGameEndedListener>();
	private List<INewTurnListener>		newTurnListeners	= new LinkedList<INewTurnListener>();
	private List<IReadyListener>		readyListeners		= new LinkedList<IReadyListener>();

	public GameObservation(Client administrativeClient)
	{
		client = administrativeClient;
	}

	@Override
	public void addGameEndedListener(IGameEndedListener listener)
	{
		gameEndedListeners.add(listener);
	}

	@Override
	public void addNewTurnListener(INewTurnListener listener)
	{
		newTurnListeners.add(listener);
	}

	@Override
	public void addReadyListener(IReadyListener listener)
	{
		readyListeners.add(listener);
	}

	@Override
	public void back()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void next()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void pause()
	{

	}

	@Override
	public void removeGameEndedListener(IGameEndedListener listener)
	{
		gameEndedListeners.remove(listener);
	}

	@Override
	public void removeNewTurnListener(INewTurnListener listener)
	{
		newTurnListeners.remove(listener);
	}

	@Override
	public void removeReadyListener(IReadyListener listener)
	{
		readyListeners.remove(listener);
	}

	@Override
	public void saveReplayToFile(String filename)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void start()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void unpause()
	{
		// TODO
	}

	/**
	 * 
	 */
	public void ready()
	{
		for (IReadyListener list : readyListeners)
		{
			list.ready();
		}
	}

	/**
	 * 
	 */
	public void gameEnded()
	{
		for (IGameEndedListener list : gameEndedListeners)
		{
			list.gameEnded();
		}
	}

	/**
	 * 
	 */
	public void newTurn(String info)
	{
		for (INewTurnListener list : newTurnListeners)
		{
			list.newTurn(info);
		}
	}
}
