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
		b.addPlayer(new Player(FigureColor.RED));

		// Der Spieler belegt das angegebene Spielfeld
		Assert.assertTrue(b.isOccupied(p.getPosition()));
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
	 * Überprüft die <code>isValid()</code> Methode des Spielbretts auf das
	 * Ausspielen einer Karte
	 */
	@Test
	public void testIsValidToPlayCard()
	{
		Board b = Board.create();
		Player p = new Player(FigureColor.RED);
		b.addPlayer(p);

		//
		// Action.DROP_20_CARROTS
		//
		// drop 20
		Assert.assertTrue(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, -20), p));
		
		// drop 15
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, -15), p));
		
		// drop 0
		Assert.assertTrue(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, 0), p));
		
		//
		// Action.TAKE_20_CARROTS
		//
		// take 20
		Assert.assertTrue(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, 20), p));
		
		// take 15
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, 15), p));
		
		// take 0
		Assert.assertTrue(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.TAKE_OR_DROP_CARROTS, 20), p));

		//
		// Action.EAT_SALAD
		//
		// == 0 Salate
		p.setSaladsToEat(0);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.EAT_SALAD), p));

		// > 0 Salate
		p.setSaladsToEat(1);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.EAT_SALAD), p));

		//
		// Action.FALL_BACK
		//
		// anderer Spieler am Start
		Player p2 = new Player(FigureColor.BLUE);
		b.addPlayer(p2);
		p.setPosition(5);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK), p));

		// anderer Spieler auf 1. Feld
		p2.setPosition(1);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK), p));

		// anderer Spieler direkt hinter Igelfeld
		p2.setPosition(12);
		p.setPosition(14);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK), p));

		// anderer Spieler weiter hinter Igelfeld
		p2.setPosition(13);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK), p));
		
		//
		// Action.HURRY_AHEAD
		//
		// als erster
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD), p));

		// als zweiter, erster im Ziel
		p.setPosition(64);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD), p2));
		
		// als zweiter, erster vor Igelfeld
		p.setPosition(14);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD), p2));
		
		// anderer direkt vorm Ziel, mehr als 10 Karotten, 0 Salaten
		p2.setCarrotsAvailable(11);
		p2.setSaladsToEat(0);
		p.setPosition(63);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD), p2));
		
		// anderer direkt vorm Ziel, mehr als 10 Karotten, mehr als 0 Salate
		p2.setSaladsToEat(1);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD), p2));
		
		// anderer direkt vorm Ziel, weniger als oder genau 10 Karotten, 0 Salate
		p2.setCarrotsAvailable(10);
		p2.setSaladsToEat(0);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD), p2));
		
		// anderer direkt vorm Ziel, weniger als oder genau 10 Karotten, mehr als 0 Salate
		p2.setSaladsToEat(1);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD), p2));
		
		// Ausspielen einer Karte, die nicht auf der Hand ist
		p.setActions(Arrays.asList(new Action[]{Action.EAT_SALAD}));
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK), p));
		Assert.assertFalse(b.isValid(new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD), p));
	}

	/**
	 * Überprüft die <code>isValid()</code> Methode des Spielbretts auf das
	 * Zurückfallen.
	 */
	@Test
	public void testIsValidToFallBack()
	{
		Board b = Board.create();
		Player p = new Player(FigureColor.RED);
		b.addPlayer(p);
		b.addPlayer(new Player(FigureColor.BLUE));

		// Fallback vom Start (es gibt keinen Igel dahinter!)
		p.setPosition(0);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.FALL_BACK), p));

		// Fallback auf freies Igelfeld
		p.setPosition(12);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.FALL_BACK), p));

		// Fallback auf besetzes Igelfeld
		Player p2 = new Player(FigureColor.BLUE);
		p2.setPosition(11);
		b.addPlayer(p2);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.FALL_BACK), p));
	}

	/**
	 * Überprüft die <code>isValid()</code> Methode des Spielbretts auf den
	 * Verzehr von Salaten.
	 */
	@Test
	public void testIsValidToEat()
	{
		Board b = Board.create();
		Player p = new Player(FigureColor.RED);
		b.addPlayer(p);

		// Verzehre Salat auf nicht-Salatfeld
		int nextCarrot = b.getNextFieldByTyp(FieldTyp.CARROT, 1);
		p.setPosition(nextCarrot);

		Assert.assertFalse(b.isValid(new Move(MoveTyp.EAT), p));

		// Verzehre Salat auf Salatfeld
		int nextSaladField = b.getNextFieldByTyp(FieldTyp.SALAD, 1);
		p.setPosition(nextSaladField);

		Assert.assertTrue(b.isValid(new Move(MoveTyp.EAT), p));

		// Verzehre zu viele Salate
		p.setSaladsToEat(0);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.EAT), p));
	}

	/**
	 * Überprüft die <code>isValid()</code> Methode des Spielbretts auf
	 * Bewegungen.
	 */
	@Test
	public void testIsValidToMove()
	{
		Board b = Board.create();
		Player p = new Player(FigureColor.RED);
		Player p2 = new Player(FigureColor.BLUE);
		b.addPlayer(p);
		b.addPlayer(p2);

		// Ein Zug über 10 Felder, der Spieler hat genug Karotten
		Assert.assertTrue(b.isValid(new Move(MoveTyp.MOVE, 10), p));

		// Ein Zug über 25 Felder, dem Spieler fehlen Karotten
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 25), p));

		// Ein Zug auf ein belegtes Feld
		p2.setPosition(10);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 10), p));

		// Ein Zug ins Ziel mit mehr als 10 Karotten
		p.setPosition(62);
		p.setSaladsToEat(0);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 2), p));
		p.setSaladsToEat(1);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 2), p));

		// Ein Zug hinter das Ziel
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 3), p));

		// Ein Zug ins Ziel mit genau 10 Karotten
		p.setPosition(63);
		p.setSaladsToEat(0);
		p.setCarrotsAvailable(11);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.MOVE, 1), p));
		p.setSaladsToEat(1);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 1), p));

		// Ein Zug ins Ziel mit < 10 Karotten
		p.setCarrotsAvailable(10);
		p.setSaladsToEat(0);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.MOVE, 1), p));
		p.setSaladsToEat(1);
		Assert.assertFalse(b.isValid(new Move(MoveTyp.MOVE, 1), p));

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
		p.setActions(Arrays.asList(new Action[] { Action.TAKE_OR_DROP_CARROTS }));
		int nextRabbitField = b.getNextFieldByTyp(FieldTyp.RABBIT, 1);
		p.setPosition(nextRabbitField - 1);
		Assert.assertTrue(b.isValid(new Move(MoveTyp.MOVE, 1), p));

		// Ein Zug auf ein Hasenfeld mit = 0 Hasenkarten übrig
		p.setActions(Arrays.asList(new Action[] {}));
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

	/**
	 * Überprüft die <code>getNextFieldByTyp()</code>-Methode des Spielbretts
	 */
	@Test
	public void testGetNextFieldByTyp()
	{
		Board b = Board.create();
		Assert.assertNotSame(-1, b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 0));
		Assert.assertEquals(-1, b.getNextFieldByTyp(FieldTyp.HEDGEHOG, 63));
	}

	/**
	 * Überprüft die <code>getPreviousFieldByTyp()</code>-Methode des
	 * Spielbretts
	 */
	@Test
	public void testGetPreviousFieldByTyp()
	{
		Board b = Board.create();
		Assert
				.assertNotSame(-1, b.getPreviousFieldByTyp(FieldTyp.HEDGEHOG,
						12));
		Assert.assertEquals(-1, b.getPreviousFieldByTyp(FieldTyp.HEDGEHOG, 11));
	}

	/**
	 * Überprüft die <code>isFirst()</code>-Methode des Spielbretts
	 */
	@Test
	public void testIsFirstPlayer()
	{
		Board b = Board.create();
		Player p1 = new Player(FigureColor.RED);
		p1.setPosition(10);
		b.addPlayer(p1);

		Player p2 = new Player(FigureColor.BLUE);
		p2.setPosition(5);
		b.addPlayer(p2);

		Assert.assertTrue(b.isFirst(p1));
		Assert.assertFalse(b.isFirst(p2));
	}
}
