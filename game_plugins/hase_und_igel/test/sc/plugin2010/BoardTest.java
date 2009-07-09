package sc.plugin2010;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sc.plugin2010.Board.FieldTyp;
import sc.plugin2010.Move.MoveTyp;
import sc.plugin2010.Player.Action;
import sc.plugin2010.Player.FigureColor;

/**
 * @author rra
 * @since Jul 4, 2009
 */
public class BoardTest
{
	/**
	 * Überpüft ob <code>isOccupied()</code> Felder korrekt als belegt/ frei
	 * markiert.
	 */
	@Test
	public void testIsOccupied()
	{
		Player p = new Player(FigureColor.BLUE);
		p.setPosition(1);

		Board b = Board.create();
		b.addPlayer(p);

		// Der Spieler belegt das angegebene Spielfeld
		Assert.assertTrue(b.isOccupied(p.getPosition()));

		// Alle anderen Felder sind frei
		Assert.assertFalse(b.isOccupied(0));
		Assert.assertFalse(b.isOccupied(2));
	}

	/**
	 * Überprüft ob der korrekte Spieler von der <code>getPlayerAt()</code>
	 * -Methode
	 * zurückgegeben wird.
	 */
	@Test
	public void testGetPlayerAt()
	{
		Player r = new Player(FigureColor.RED, 1);
		Player g = new Player(FigureColor.BLUE, 2);

		Board b = Board.create();
		b.addPlayer(r);
		b.addPlayer(g);

		Assert.assertEquals(r, b.getPlayerAt(r.getPosition()));
		Assert.assertEquals(g, b.getPlayerAt(g.getPosition()));
	}

	/**
	 * Überprüft die <code>isValid()</code> Methode des Spielbretts auf Bewegungen
	 */
	@Test
	public void testIsValidMove()
	{
		Board b = Board.create();
		Player p = new Player(FigureColor.RED);
		b.addPlayer(p);
		
		// Ein Zug über 10 Felder, der Spieler hat genug Karotten
		Assert.assertTrue(b.isValid(new Move(MoveTyp.MOVE, 10), p));
		
		// Ein Zug über 25 Felder, dem Spieler fehlen Karotten
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 25), p));
		
		// Ein Zug auf ein belegtes Feld
		Player p2 = new Player(FigureColor.BLUE);
		p2.setPosition(10);
		b.addPlayer(p2);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 10), p));
		
		// Ein Zug ins Ziel mit mehr als 10 Karotten
		p.setPosition(62);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 2), p));
		
		// Ein Zug hinter das Ziel
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 3), p));
		
		// Ein Zug ins Ziel mit genau 10 Karotten
		p.setPosition(63);
		p.setCarrotsAvailable(11);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.MOVE, 1), p));
		
		// Ein Zug ins Ziel mit < 10 Karotten
		p.setCarrotsAvailable(10);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.MOVE, 1), p));
		
		// Ein Zug mit negativem Wert
		p.setPosition(1);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, -1), p));
		
		// Ein Zug ohne Wert
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 0), p));
		
		// Ein Zug auf ein Salatfeld mit > 0 Salaten übrig
		p.setSaladsToEat(1);
		p.setPosition(20);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.MOVE, 2), p));
		
		// Ein Zug auf ein Salatfeld mit = 0 Salaten übrig
		p.setSaladsToEat(0);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 2), p));
		
		// Ein Zug auf ein Hasenfeld mit > 1 Hasenkarten übrig
		p.setActions(Arrays.asList(new Action []{Action.TAKE_20_CARROTS}));
		int nextRabbitField = b.getNextFieldByTyp(FieldTyp.RABBIT, 1);
		p.setPosition(nextRabbitField-1);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.MOVE, 1), p));
		
		// Ein Zug auf ein Hasenfeld mit = 0 Hasenkarten übrig
		p.setActions(Arrays.asList(new Action []{}));
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 1), p));
	}

	/**
	 * Überprüft von <code>initialize()</code> erstellte Rennstrecken und die
	 * <code>getTypeAt()</code>-Methode
	 */
	@Test
	public void testInitialize()
	{
		Board b = Board.create();

		// Die Länge ist richtig
		Assert.assertEquals(FieldTyp.INVALID, b.getTypeAt(-1));
		Assert.assertEquals(FieldTyp.INVALID, b.getTypeAt(66));

		// Das Spielfeld beginnt und endet korrekt
		Assert.assertEquals(FieldTyp.START, b.getTypeAt(0));
		Assert.assertEquals(FieldTyp.GOAL, b.getTypeAt(64));

		// Die Igelfelder liegen richtig
		List<Integer> hedgehogsAt = new LinkedList<Integer>();
		hedgehogsAt.addAll(Arrays.asList(new Integer[] { 11, 15, 19, 24, 30,
				37, 43, 50, 56 }));
		for (int i = 0; i < 65; i++)
		{
			Assert.assertEquals(hedgehogsAt.contains(i), b.getTypeAt(i).equals(
					FieldTyp.HEDGEHOG));
		}

		// Die Salatfelder liegen richtig
		List<Integer> saladsAt = new LinkedList<Integer>();
		saladsAt.addAll(Arrays.asList(new Integer[] { 10, 22, 42, 57 }));
		for (int i = 0; i < 65; i++)
		{
			Assert.assertEquals(saladsAt.contains(i), b.getTypeAt(i).equals(
					FieldTyp.SALAD));
		}

		for (int i = 0; i < 65; i++)
			Assert.assertNotSame(FieldTyp.INVALID, b.getTypeAt(i));
	}
}
