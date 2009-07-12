/**
 * 
 */
package sc.plugin2010.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.helpers.ReplayBuilder;
import sc.plugin2010.GameState;
import sc.protocol.IControllableGame;
import sc.protocol.clients.IUpdateListener;

/**
 * @author ffi
 * 
 */
public class Observation implements IObservation, IUpdateListener
{
	private IControllableGame			conGame;

	private List<IGameEndedListener>	gameEndedListeners	= new LinkedList<IGameEndedListener>();
	private List<INewTurnListener>		newTurnListeners	= new LinkedList<INewTurnListener>();
	private List<IReadyListener>		readyListeners		= new LinkedList<IReadyListener>();

	public Observation(IControllableGame conGame)
	{
		this.conGame = conGame;
		conGame.addListener(this);
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
		conGame.previous();
	}

	@Override
	public void cancel()
	{
		// conGame.; TODO
	}

	@Override
	public void next()
	{
		conGame.next();
	}

	@Override
	public void pause()
	{
		conGame.pause();
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
	public void saveReplayToFile(String filename) throws IOException
	{
		ReplayBuilder.saveReplay(conGame, filename);
	}

	@Override
	public void start()
	{
		conGame.unpause();
	}

	@Override
	public void unpause()
	{
		conGame.unpause();
		// TODO switch in rendererfacade
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

	@Override
	public void onUpdate(Object sender)
	{
		assert sender == conGame;
		GameState state = (GameState)conGame.getCurrentState();
		
		// TODO: propagate state to listeners
	}
}
