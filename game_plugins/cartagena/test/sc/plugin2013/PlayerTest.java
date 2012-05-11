package sc.plugin2013;
import junit.framework.Assert;

import org.junit.Test;


public class PlayerTest {
	@Test
	public void testPlayer(){
		Player p = new Player(PlayerColor.RED);
		
		Assert.assertEquals(PlayerColor.RED, p.getPlayerColor());
		Assert.assertEquals(0, p.getPoints());
	}
}
