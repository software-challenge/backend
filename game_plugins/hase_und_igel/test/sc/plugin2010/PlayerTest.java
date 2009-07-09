package sc.plugin2010;

import junit.framework.Assert;

import org.junit.Test;

import sc.plugin2010.Player;
import sc.plugin2010.Player.FigureColor;

/**
 * @author rra
 * @since Jul 4, 2009
 * 
 */
public class PlayerTest
{
	/**
	 * Überprüft das ein Spieler mit den richtigen Anfangswerten erstellt wird.
	 */
	@Test
	public void testPlayer()
	{
		Player p = new Player(FigureColor.WHITE);

		Assert.assertEquals(FigureColor.WHITE, p.getColor());
		Assert.assertEquals(0, p.getPosition());
		Assert.assertEquals(0, p.getSaladsEaten());
		Assert.assertEquals(60, p.getCarrotsAvailable());
	}
}
