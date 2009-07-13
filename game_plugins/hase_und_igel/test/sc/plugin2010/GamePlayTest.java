package sc.plugin2010;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.plugin2010.Player.FigureColor;

public class GamePlayTest
{
	private Game	g;
	private Board	b;
	private Player	red;
	private Player	blue;

	@Before
	public void beforeTest() throws TooManyPlayersException
	{
		g = new Game();
		b = g.getBoard();
		red = (Player) g.onPlayerJoined();
		blue = (Player) g.onPlayerJoined();
	}

	@Test
	public void firstRound()
	{
		Assert.assertEquals(red.getColor(), FigureColor.RED);
		Assert.assertEquals(blue.getColor(), FigureColor.BLUE);

		Assert.assertEquals(0, red.getFieldNumber());
		Assert.assertEquals(0, blue.getFieldNumber());
	}

	/**
	 * Wenn beide Spieler am Start stehen,
	 */
	@Test
	public void noFallBackInFirstRound()
	{
		
		
	}
}
