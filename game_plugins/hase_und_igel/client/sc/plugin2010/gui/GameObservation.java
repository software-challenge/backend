/**
 * 
 */
package sc.plugin2010.gui;

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

	private Client	client;

	public GameObservation(Client administrativeClient)
	{
		client = administrativeClient;
	}

	@Override
	public void addGameEndedListener(IGameEndedListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addNewTurnListener(INewTurnListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addReadyListener(IReadyListener listener)
	{
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void removeGameEndedListener(IGameEndedListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeNewTurnListener(INewTurnListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeReadyListener(IReadyListener listener)
	{
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}
}
