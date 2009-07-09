package sc.plugin2010;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sc.plugin2010.Board.FieldTyp;
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
		Player p = new Player(FigureColor.WHITE);
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
		Player w = new Player(FigureColor.WHITE, 0);
		Player r = new Player(FigureColor.RED, 1);
		Player g = new Player(FigureColor.GREEN, 2);

		Board b = Board.create();
		b.addPlayer(w);
		b.addPlayers(new Player[] { r, g });

		Assert.assertEquals(w, b.getPlayerAt(w.getPosition()));
		Assert.assertEquals(r, b.getPlayerAt(r.getPosition()));
		Assert.assertEquals(g, b.getPlayerAt(g.getPosition()));
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
		hedgehogsAt.addAll(Arrays.asList(new Integer[]{ 11, 15, 19, 24, 30, 37, 43, 50, 56 }));
		for (int i = 0; i < 65; i++)
		{
			Assert.assertEquals(hedgehogsAt.contains(i), b.getTypeAt(i).equals(FieldTyp.HEDGEHOG));
		}

		// Die Salatfelder liegen richtig
		List<Integer> saladsAt = new LinkedList<Integer>();
		saladsAt.addAll(Arrays.asList(new Integer[]{ 10, 22, 42, 57 }));
		for (int i = 0; i < 65; i++)
		{
			Assert.assertEquals(saladsAt.contains(i), b.getTypeAt(i).equals(FieldTyp.SALAD));
		}
		
		for(int i = 0; i < 65; i++)
			Assert.assertNotSame(FieldTyp.INVALID, b.getTypeAt(i));
	}
}
