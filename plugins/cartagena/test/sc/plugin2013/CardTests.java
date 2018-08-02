package sc.plugin2013;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.plugin2013.util.Constants;

public class CardTests {
	GameState gs;
	
	@Before
	public void beforeEveryTest() throws RescueableClientException {
		gs = new GameState(false);
	}
	
	@Test
	public void initTest(){
		Assert.assertEquals(gs.getCardStack().size(), Constants.CARDS_PER_SYMBOL * Constants.SYMBOLS -12);
		Assert.assertEquals(gs.getUsedStack().size(), 0);
		Assert.assertEquals(gs.getOpenCards().size(), 12);
	}
	
	/* Überprüft ob der Kartenstapel richtig gemischt wird, wenn alle Karten aufgebraucht wurden.
	 * 
	 */
	@Test
	public void allCardsUsed(){
		
	}

}
