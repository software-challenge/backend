package sc.plugin2010;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Überprüft einige Methoden des Spielbretts
 * 
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
		p.setFieldNumber(1);

		Board b = Board.create();
		b.addPlayer(p);
		b.addPlayer(new Player(FigureColor.RED));

		Assert.assertTrue(b.isOccupied(p.getFieldNumber()));
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

		Assert.assertEquals(r, b.getPlayerAt(r.getFieldNumber()));
		Assert.assertEquals(g, b.getPlayerAt(g.getFieldNumber()));
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
		p1.setFieldNumber(10);
		b.addPlayer(p1);

		Player p2 = new Player(FigureColor.BLUE);
		p2.setFieldNumber(5);
		b.addPlayer(p2);

		Assert.assertTrue(b.isFirst(p1));
		Assert.assertFalse(b.isFirst(p2));
		
		p1.setFieldNumber(64);
		p2.setFieldNumber(64);
		p1.setCarrotsAvailable(4);
		p2.setCarrotsAvailable(6);
		
		Assert.assertTrue(b.isFirst(p1));
		Assert.assertFalse(b.isFirst(p2));
	}
}
