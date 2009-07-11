/**
 * 
 */
package sc.plugin2010.gui;

import java.io.IOException;

import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.protocol.ReplayClient;

/**
 * @author ffi
 * 
 */
public class ReplayObservation implements IObservation
{
	private ReplayClient	repClient;

	/**
	 * @param rep
	 */
	public ReplayObservation(ReplayClient rep)
	{
		repClient = rep;
	}

	@Override
	public void addGameEndedListener(IGameEndedListener listener)
	{
		// TODO
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
		repClient.previous();
	}

	@Override
	public void cancel()
	{
		try
		{
			repClient.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void next()
	{
		repClient.next();
	}

	@Override
	public void pause()
	{
		repClient.pause();
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
		// repClient.
	}

	@Override
	public void start()
	{
		// repClient.
	}

	@Override
	public void unpause()
	{
		repClient.unpause();
	}

}
