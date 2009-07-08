/**
 * 
 */
package sc.plugin2010.gui;

import java.util.List;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.ISlot;
import sc.plugin2010.Client;

/**
 * @author ffi
 * 
 */
public class GamePreparation implements IGamePreparation
{
	private Client	client;

	public GamePreparation(Client client)
	{
		this.client = client;
	}

	@Override
	public void cancel()
	{

	}

	@Override
	public List<ISlot> getSlots()
	{
		return null;

	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void start()
	{
		client.joinAnyGame(); // TODO
	}

}
