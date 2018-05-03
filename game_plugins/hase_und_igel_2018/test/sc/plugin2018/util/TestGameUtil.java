package sc.plugin2018.util;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author rra
 * @since Jul 4, 2009
 *
 */
public class TestGameUtil
{
	/**
	 * Überprüft die Berechnungen der <code>calculateCarrots()</code> Hilfsfunktion
	 */
	@Test
	public void testCalculateCarrots()
	{
		Assert.assertEquals(1, GameRuleLogic.calculateCarrots(1));
		Assert.assertEquals(55, GameRuleLogic.calculateCarrots(10));
	}
	
	/**
	 * Überprüft die Berechnung der <code>calculateMoveableFields()</code> Hilfsfunktion
	 */
	@Test
	public void testCalculateMoveableFields() {
		Assert.assertEquals(0, GameRuleLogic.calculateMoveableFields(0));
		Assert.assertEquals(1, GameRuleLogic.calculateMoveableFields(1));
		Assert.assertEquals(2, GameRuleLogic.calculateMoveableFields(5));
		Assert.assertEquals(3, GameRuleLogic.calculateMoveableFields(6));
		Assert.assertEquals(3, GameRuleLogic.calculateMoveableFields(7));
	}

}
