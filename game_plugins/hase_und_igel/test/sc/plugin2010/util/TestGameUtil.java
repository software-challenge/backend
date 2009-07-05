package sc.plugin2010.util;

import junit.framework.Assert;

import org.junit.Test;

import sc.plugin2010.util.GameUtil;

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
		Assert.assertEquals(253, GameUtil.calculateCarrots(22));
		Assert.assertEquals(990, GameUtil.calculateCarrots(44));
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
