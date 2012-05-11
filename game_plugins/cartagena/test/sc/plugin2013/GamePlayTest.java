package sc.plugin2013;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sc.api.plugins.exceptions.RescueableClientException;

public class GamePlayTest {
	
	Game g;
	GameState gs;
	Board b;
	Player red;
	Player blue;
	
	
	@Before
	public void beforeEveryTest() throws RescueableClientException
	{
		g = new Game();
		gs = g.getGameState();
		b = gs.getBoard();
		red = (Player) g.onPlayerJoined();
		blue = (Player) g.onPlayerJoined();
	}
	
	@Test
	public void firstRound(){
		Assert.assertEquals(PlayerColor.RED, red.getPlayerColor());
		Assert.assertEquals(PlayerColor.BLUE, blue.getPlayerColor());
		
		Assert.assertTrue(b.hasPirates(0, PlayerColor.RED));
		Assert.assertTrue(b.hasPirates(0, PlayerColor.BLUE));
		
		g.start();
		Assert.assertEquals(PlayerColor.RED, gs.getCurrentPlayerColor());
	}
}
