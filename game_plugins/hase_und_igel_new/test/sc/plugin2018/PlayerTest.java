package sc.plugin2018;

import java.util.List;

import junit.framework.Assert;
import sc.plugin2018.CardAction;
import sc.shared.PlayerColor;
import sc.plugin2018.Player;

import org.junit.Test;

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
		Player p = new Player(PlayerColor.RED);

		Assert.assertEquals(PlayerColor.RED, p.getPlayerColor());
		Assert.assertEquals(0, p.getFieldIndex());
		Assert.assertEquals(5, p.getSalads());
		Assert.assertEquals(68, p.getCarrotsAvailable());
		
		List<CardAction> actions = p.getActions();
		Assert.assertTrue(actions.contains(CardAction.EAT_SALAD));
		Assert.assertTrue(actions.contains(CardAction.HURRY_AHEAD));
		Assert.assertTrue(actions.contains(CardAction.FALL_BACK));
		Assert.assertTrue(actions.contains(CardAction.TAKE_OR_DROP_CARROTS));
	}
}
