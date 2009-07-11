package sc.plugin2010;

import junit.framework.Assert;

import org.junit.Test;

import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.plugin2010.Move.MoveTyp;

public class GameTest
{
	/**
	 * Überprüft ob sich ein Spieler auf dem Spielbrett noch bewegen kann.
	 * @throws TooManyPlayersException 
	 */
	@Test
	public void testPlayerCanMove() throws TooManyPlayersException
	{
		Game g = new Game();
		g.initialize();
		Player p1 = (Player) g.onPlayerJoined();
		Player p2 = (Player) g.onPlayerJoined();
		g.start();
		
		Assert.assertTrue(g.playerCanMove(p1));
		Assert.assertTrue(g.playerCanMove(p2));
		
		// TODO mehr tests!
	}

	/**
	 * @throws TooManyPlayersException
	 */
	@Test(expected = TooManyPlayersException.class)
	public void testOnPlayerJoined() throws TooManyPlayersException
	{
		Game g = new Game();
		g.initialize();
		
		Player p1 = (Player) g.onPlayerJoined();
		Player p2 = (Player) g.onPlayerJoined();
		
		Assert.assertNotSame(p1, p2);
		
		g.onPlayerJoined();
	}

	/**
	 * Überprüft ob die Spiellogik korrekt implementiert wurde
	 * 
	 * @throws TooManyPlayersException
	 */
	@Test
	public void testUpdateBoardWithMove() throws TooManyPlayersException
	{
		Game g = new Game();
		g.initialize();

		Player p1 = (Player) g.onPlayerJoined();
		Player p2 = (Player) g.onPlayerJoined();
		
		g.start();
		
		Assert.assertEquals(0, p1.getPosition());
		Assert.assertEquals(0, p2.getPosition());
		
		g.onAction(p1, new Move(MoveTyp.MOVE, 10));
		
		Assert.assertEquals(10, p1.getPosition());
		Assert.assertEquals(0, p2.getPosition());
		
		g.onAction(p2, new Move(MoveTyp.MOVE, 9));
		
		Assert.assertEquals(10, p1.getPosition());
		Assert.assertEquals(9, p2.getPosition());
	}
}
