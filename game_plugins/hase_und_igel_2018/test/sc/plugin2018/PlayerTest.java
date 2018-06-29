package sc.plugin2018;

import junit.framework.Assert;
import org.junit.Test;
import sc.shared.PlayerColor;

/**
 * @author rra
 * @since Jul 4, 2009
 */
public class PlayerTest
{
	/** Überprüft das ein Spieler mit den richtigen Anfangswerten erstellt wird. */
	@Test
	public void testPlayer()
	{
		Player red = new Player(PlayerColor.RED);
		Assert.assertEquals(PlayerColor.RED, red.getPlayerColor());
		Assert.assertEquals(68, red.getCarrots());
		Assert.assertEquals(5, red.getSalads());
		Assert.assertEquals(false, red.inGoal());
		Assert.assertEquals(false, red.mustPlayCard());
		Assert.assertEquals(0, red.getFieldIndex());
		Assert.assertEquals(true, red.ownsCardOfType(CardType.EAT_SALAD));
    Assert.assertEquals(true, red.ownsCardOfType(CardType.HURRY_AHEAD));
    Assert.assertEquals(true, red.ownsCardOfType(CardType.FALL_BACK));
    Assert.assertEquals(true, red.ownsCardOfType(CardType.TAKE_OR_DROP_CARROTS));
	}

}
