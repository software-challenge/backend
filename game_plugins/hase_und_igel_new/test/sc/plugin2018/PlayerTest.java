package sc.plugin2018;

import java.util.List;

import junit.framework.Assert;
import sc.plugin2018.Action;
import sc.plugin2018.PlayerColor;
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
		Assert.assertEquals(5, p.getSaladsToEat());
		Assert.assertEquals(68, p.getCarrotsAvailable());
		
		List<Action> actions = p.getActions();
		Assert.assertTrue(actions.contains(Action.EAT_SALAD));
		Assert.assertTrue(actions.contains(Action.HURRY_AHEAD));
		Assert.assertTrue(actions.contains(Action.FALL_BACK));
		Assert.assertTrue(actions.contains(Action.TAKE_OR_DROP_CARROTS));
	}
}
