package sc.plugin2010;

import java.util.LinkedList;

import junit.framework.Assert;

import org.junit.Test;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IPlayerListener;
import sc.plugin2010.Board.FieldTyp;
import sc.plugin2010.Move.MoveTyp;
import sc.plugin2010.Player.Action;
import sc.plugin2010.util.GameUtil;

public class GameTest
{
	/**
	 * Überprüft ob sich ein Spieler auf dem Spielbrett noch bewegen kann.
	 * 
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

		Assert.assertTrue(GameUtil.canMove(p1, g.getBoard()));
		Assert.assertTrue(GameUtil.canMove(p2, g.getBoard()));

		p2.setFieldNumber(11);
		p1.setFieldNumber(12);
		p1.setCarrotsAvailable(0);
		
		Assert.assertTrue(GameUtil.canMove(p1, g.getBoard()));
		
		p1.setActions(new LinkedList<Action>());
		Assert.assertFalse(GameUtil.canMove(p1, g.getBoard()));
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
	public void testUpdateBoardWithMove() throws RescueableClientException
	{
		Game g = new Game();
		g.initialize();

		Player p1 = (Player) g.onPlayerJoined();
		Player p2 = (Player) g.onPlayerJoined();

		g.start();
		Assert.assertTrue(g.isActive());

		Assert.assertEquals(0, p1.getFieldNumber());
		Assert.assertEquals(0, p2.getFieldNumber());

		g.onAction(p1, new Move(MoveTyp.MOVE, 10));

		Assert.assertEquals(10, p1.getFieldNumber());
		Assert.assertEquals(0, p2.getFieldNumber());

		g.onAction(p2, new Move(MoveTyp.MOVE, 9));

		Assert.assertEquals(10, p1.getFieldNumber());
		Assert.assertEquals(9, p2.getFieldNumber());
	}

	/**
	 * Überprüft den Spielablauf für das Essen eines Salats auf einem Salatfeld
	 * 
	 * @throws TooManyPlayersException
	 */
	@Test
	public void testEatSalatLogic() throws RescueableClientException
	{
		Game g = new Game();
		g.initialize();
		Board b = g.getBoard();

		Player p1 = (Player) g.onPlayerJoined();
		Player p2 = (Player) g.onPlayerJoined();

		g.start();
		// Bewege dich zu einem Salatfeld, Iß einen Salat
		int nextSalatAt = b.getNextFieldByTyp(FieldTyp.SALAD, p1.getFieldNumber());
		Move toSalad = new Move(MoveTyp.MOVE, nextSalatAt);
		Assert.assertTrue(b.isValid(toSalad, p1));
		g.onAction(p1, toSalad);

		g.onAction(p2, new Move(MoveTyp.MOVE, 1));

		Move eatSalad = new Move(MoveTyp.EAT);
		Assert.assertTrue(b.isValid(eatSalad, p1));
		g.onAction(p1, eatSalad);

		g.onAction(p2, new Move(MoveTyp.MOVE, 1));

		// Spieler muss sich jetzt bewegen!
		Assert.assertFalse(b.isValid(new Move(MoveTyp.EAT), p1));
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD,
				Action.EAT_SALAD), p1));
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD,
				Action.TAKE_OR_DROP_CARROTS), p1));
	}

	/**
	 * Überprüft dass ein Spieler das Ziel nur betreten darf, nachdem er alle
	 * Salate gefressen hat. Außerdem 
	 * 
	 * @throws TooManyPlayersException
	 */
	@Test
	public void testEnterGoal() throws RescueableClientException
	{
		Game g = new Game();
		g.initialize();
		Board b = g.getBoard();

		Player p1 = (Player) g.onPlayerJoined();
		Player p2 = (Player) g.onPlayerJoined();

		g.start();
		p1.setFieldNumber(62);
		
		Move toGoal = new Move(MoveTyp.MOVE, 2);
		Assert.assertFalse(b.isValid(toGoal, p1));
		
		p1.setCarrotsAvailable(10);
		Assert.assertFalse(b.isValid(toGoal, p1));
		
		p1.setSaladsToEat(0);
		Assert.assertTrue(b.isValid(toGoal, p1));
		g.onAction(p1, toGoal);
		
		g.onAction(p2, new Move(MoveTyp.MOVE, 1));
		
		Assert.assertFalse(g.isActive());
	}

	/**
	 * Überprüft dass ein Spiel nach 30 Runden wirklich endet.
	 * 
	 * @throws TooManyPlayersException
	 */
	@Test
	public void testRoundLimit() throws RescueableClientException
	{
		Game g = new Game();
		g.initialize();
		Board b = g.getBoard();

		Player p1 = (Player) g.onPlayerJoined();
		Player p2 = (Player) g.onPlayerJoined();

		g.start();
		int turns = 0;
		while (g.isActive())
		{
			Assert.assertEquals(p1, g.getActivePlayer());
			Move m11 = new Move(MoveTyp.FALL_BACK);
			Move m12 = new Move(MoveTyp.MOVE, b.nextFreeFieldFor(p1));
			if (b.isValid(m11, p1))
				g.onAction(p1, m11);
			else
				g.onAction(p1, m12);
			Assert.assertEquals(p2, g.getActivePlayer());
			Move m21 = new Move(MoveTyp.FALL_BACK);
			Move m22 = new Move(MoveTyp.MOVE, b.nextFreeFieldFor(p2));
			if (b.isValid(m21, p2))
				g.onAction(p2, m21);
			else
				g.onAction(p2, m22);
			turns++;
			Assert.assertEquals(turns, g.getTurn());
		}

		Assert.assertEquals(31, g.getTurn());
	}
}
