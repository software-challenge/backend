package sc.plugin2013;

import junit.framework.Assert;

import org.junit.Test;

import sc.plugin2013.util.Constants;

public class BoardTest {

	private int numFields = Constants.SEGMENTS * Constants.SYMBOLS + 2;
	Board b = new Board();

	/**
	 * Testet ob ein Board nach erstellen die erforderlichen Anfangszustände
	 * besitzt
	 * 
	 */
	@Test
	public void testBoard() {
		// Board b = new Board();
		Assert.assertEquals(numFields, b.size());

		Assert.assertEquals(FieldType.START, b.getField(0).type);
		Assert.assertEquals(FieldType.FINISH,
				b.getField(Constants.SEGMENTS * Constants.SYMBOLS + 1).type);
		Assert.assertEquals(Constants.PIRATES * 2, b.getPirates(0).size());
	}

	/**
	 * Testet ob für jeden möglichen index Ein Field Object zurückgegeben wird
	 * 
	 */
	@Test
	public void testGetField() {
		// Board b = new Board();

		for (int i = 0; i < numFields; i++) {
			Assert.assertNotNull(b.getField(i));
			Assert.assertEquals(Field.class, b.getField(i).getClass());
		}
	}

	/** Testet ob getNextField den richtigen index zurückgibt
	 * 
	 */
	@Test
	public void testGetNextField() {
		int searchIndex = -1;
		int index = 1;
		for (SymbolType symbol : SymbolType.values()) {
			searchIndex = -1;
			index = 1;
			while (searchIndex == -1) {
				if (b.getField(index).symbol.equals(symbol)) {
					searchIndex = index;
				}
				index++;
			}
			Assert.assertEquals(searchIndex, b.getNextField(0, symbol));
		}
	}

	@Test
	public void testGetPirates() {
		Assert.assertEquals(0, b.getPirates(numFields-1).size());
		Assert.assertNotNull(b.getPirates(0));
	}

	@Test
	public void testGetPreviousField() {
		b.movePirate(0, 4, PlayerColor.RED);
		Assert.assertEquals(0, b.getPreviousField(4));
	}

	@Test
	public void testHasPirates() {
		Assert.assertTrue(b.hasPirates(0, PlayerColor.RED));
		Assert.assertTrue(b.hasPirates(0, PlayerColor.BLUE));
	}

	@Test
	public void testMovePirate() {
		b.movePirate(0, 1, PlayerColor.RED);
		Assert.assertEquals(PlayerColor.RED, b.getPirates(1).get(0).getOwner());
	}

	@Test
	public void testSize() {
		Assert.assertEquals(numFields, b.size());
	}

}
