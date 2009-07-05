package sc.plugin2010;

import org.junit.Assert;
import org.junit.Test;

import sc.plugin2010.Board;
import sc.plugin2010.Player;
import sc.plugin2010.Board.FieldTyp;
import sc.plugin2010.Player.FigureColor;

/**
 * @author rra
 * @since Jul 4, 2009
 */
public class BoardTest
{
	/**
	 * Überpüft ob <code>isOccupied()</code> Felder korrekt als belegt/ frei markiert.
	 */
	@Test
	public void testIsOccupied()
	{
		Player p = new Player(FigureColor.WHITE);
		p.setPosition(1);

		Board b = Board.create(3);
		b.addPlayer(p);

		// Der Spieler belegt das angegebene Spielfeld
		Assert.assertTrue(b.isOccupied(p.getPosition()));

		// Alle anderen Felder sind frei
		Assert.assertFalse(b.isOccupied(0));
		Assert.assertFalse(b.isOccupied(2));
	}

	/**
	 * Überprüft ob der korrekte Spieler von der <code>getPlayerAt()</code>-Methode
	 * zurückgegeben wird.
	 */
	@Test
	public void testGetPlayerAt()
	{
		Player w = new Player(FigureColor.WHITE, 0);
		Player r = new Player(FigureColor.RED, 1);
		Player g = new Player(FigureColor.GREEN, 2);
		
		Board b = Board.create(3);
		b.addPlayer(w);
		b.addPlayers(new Player[]{r,g});
		
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
		Board b = Board.create(5);

		// Die Länge ist richtig
		Assert.assertEquals(FieldTyp.INVALID, b.getTypeAt(-1));
		Assert.assertEquals(FieldTyp.INVALID, b.getTypeAt(5));

		// Das Spielfeld beginnt und endet korrekt
		Assert.assertEquals(FieldTyp.START, b.getTypeAt(0));
		Assert.assertEquals(FieldTyp.GOAL, b.getTypeAt(4));
	}
}
