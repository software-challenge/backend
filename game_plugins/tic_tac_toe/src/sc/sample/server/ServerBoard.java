package sc.sample.server;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.sample.shared.Board;
import sc.sample.shared.Move;
import sc.sample.shared.Player;

public class ServerBoard extends Board
{
	public void apply(Player p, Move m) throws RescueableClientException
	{
		if (this.getOwner(m.getX(), m.getY()) != null)
		{
			throw new RescueableClientException("Field already occupied");
		}

		this.setOwner(p, m.getX(), m.getY());
	}

	private void setOwner(Player p, int x, int y)
	{
		this.fields[y][x].setOwner(p);
	}
}
