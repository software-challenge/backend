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
		Assert.assertEquals(1, GameUtil.calculateCarrots(1));
		Assert.assertEquals(55, GameUtil.calculateCarrots(10));
	}
	
	/**
	 * Überprüft die Berechnung der <code>calculateMoveableFields()</code> Hilfsfunktion
	 */
	@Test
	public void testCalculateMoveableFields() {
		Assert.assertEquals(0, GameUtil.calculateMoveableFields(0));
		Assert.assertEquals(1, GameUtil.calculateMoveableFields(1));
		Assert.assertEquals(2, GameUtil.calculateMoveableFields(5));
		Assert.assertEquals(3, GameUtil.calculateMoveableFields(6));
		Assert.assertEquals(3, GameUtil.calculateMoveableFields(7));
	}
}
