/**
 * 
 */
package sc.plugin2010.gui;

import sc.guiplugin.interfaces.ISlot;

/**
 * @author ffi
 * 
 */
public class Slot implements ISlot
{
	private String	roomId;
	private String	reservation;

	public Slot(String roomId, String reservation)
	{
		this.reservation = reservation;
		this.roomId = roomId;
	}

	@Override
	public String[] asClient()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void asHuman()
	{
		// TODO Auto-generated method stub

	}

}
