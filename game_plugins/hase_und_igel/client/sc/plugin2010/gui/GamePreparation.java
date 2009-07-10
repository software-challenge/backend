/**
 * 
 */
package sc.plugin2010.gui;

import java.util.LinkedList;
import java.util.List;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.IReadyListener;
import sc.guiplugin.interfaces.ISlot;
import sc.plugin2010.Client;

/**
 * @author ffi
 * 
 */
public class GamePreparation implements IGamePreparation
{
	private Client		administrativeClient;
	private List<ISlot>	slots	= new LinkedList<ISlot>();

	public GamePreparation(Client client)
	{
		this.administrativeClient = client;
	}

	@Override
	public List<ISlot> getSlots()
	{
		return this.slots;
	}

	@Override
	public IObservation observe()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addReadyListener(IReadyListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeReadyListener(IReadyListener listener)
	{
		// TODO Auto-generated method stub

	}
}
