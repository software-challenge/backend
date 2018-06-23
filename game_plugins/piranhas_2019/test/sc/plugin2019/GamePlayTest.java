package sc.plugin2019;

import org.junit.Before;
import sc.shared.*;

public class GamePlayTest
{
	private Game game;
	private GameState state;
	private Player red;
	private Player blue;

	@Before
	public void beforeEveryTest() {
		game = new Game();
		state = game.getGameState();
		red = state.getPlayer(PlayerColor.RED);
		red = state.getPlayer(PlayerColor.BLUE);
	}
}
