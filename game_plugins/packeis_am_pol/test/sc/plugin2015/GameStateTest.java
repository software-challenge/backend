package sc.plugin2015;

import static org.junit.Assert.*;

import org.junit.Test;

public class GameStateTest {

	@Test
	public void testGameState() {
		GameState gameState = new GameState();
		assertTrue(gameState.getCurrentMoveType() == MoveType.SET);
		assertTrue(gameState.getCurrentPlayerColor() == PlayerColor.RED);
		assertTrue(gameState.getOtherPlayerColor() == PlayerColor.BLUE);

	}

	@Test
	public void testAddPlayer() {

	}

	@Test
	public void testGetCurrentPlayer() {

	}

	@Test
	public void testGetOtherPlayer() {

	}

	@Test
	public void testGetStartPlayer() {

	}

	@Test
	public void testGetRound() {

	}

	@Test
	public void testGetLastMove() {

	}

	@Test
	public void testGetPlayerStats() {

	}

	@Test
	public void testGetGameStats() {

	}

	@Test
	public void testGetPlayerNames() {

	}

	@Test
	public void testEndGame() {

	}

	@Test
	public void testGameEnded() {

	}

	@Test
	public void testWinner() {

	}

	@Test
	public void testWinningReason() {

	}

	@Test
	public void testDrawStone() {

	}

	@Test
	public void testPrepareNextTurn() {

	}

}
