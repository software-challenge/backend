package sc.plugin2013;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.plugin2013.util.InvalidMoveException;

public class GamePlayTest {

	Game g;
	GameState gs;
	Board b;
	Player red;
	Player blue;

	@Before
	public void beforeEveryTest() throws RescueableClientException {
		g = new Game();
		gs = g.getGameState();
		b = gs.getBoard();
		red = (Player) g.onPlayerJoined();
		blue = (Player) g.onPlayerJoined();
	}

	@Test
	public void firstRound() {
		Assert.assertEquals(PlayerColor.RED, red.getPlayerColor());
		Assert.assertEquals(PlayerColor.BLUE, blue.getPlayerColor());

		Assert.assertTrue(b.hasPirates(0, PlayerColor.RED));
		Assert.assertTrue(b.hasPirates(0, PlayerColor.BLUE));

		g.start();
		Assert.assertEquals(PlayerColor.RED, gs.getCurrentPlayerColor());
	}

	/** Testet ob ein negativer FeldIndex bestraft wird
	 * @throws InvalidMoveException
	 */
	@Test(expected = InvalidMoveException.class)
	public void negativeFieldMoveForward() throws InvalidMoveException {
		g.start();
		Move wrongMove = new ForwardMove(-1, SymbolType.BOTTLE);
		MoveContainer mContainer = new MoveContainer(wrongMove);
		mContainer.perform(gs, gs.getCurrentPlayer());
	}
	
	/** Testet ob ein zu großer Feldindex abgefangen wird
	 * @throws InvalidMoveException
	 */
	@Test(expected = InvalidMoveException.class)
	public void toHighFieldMoveForward() throws InvalidMoveException {
		g.start();
		Move wrongMove = new ForwardMove(b.size(), SymbolType.BOTTLE);
		MoveContainer mContainer = new MoveContainer(wrongMove);
		mContainer.perform(gs, gs.getCurrentPlayer());
	}
	
	/** Testet ob ein negativer FeldIndex bestraft wird
	 * @throws InvalidMoveException
	 */
	@Test(expected = InvalidMoveException.class)
	public void negativeFieldMoveBackward() throws InvalidMoveException {
		g.start();
		Move wrongMove = new BackwardMove(-5);
		MoveContainer mContainer = new MoveContainer(wrongMove);
		mContainer.perform(gs, gs.getCurrentPlayer());
	}
	
	/** Testet ob ein zu großer Feldindex abgefangen wird
	 * @throws InvalidMoveException
	 */
	@Test(expected = InvalidMoveException.class)
	public void toHighFieldMoveBackward() throws InvalidMoveException {
		g.start();
		Move wrongMove = new BackwardMove(b.size());
		MoveContainer mContainer = new MoveContainer(wrongMove);
		mContainer.perform(gs, gs.getCurrentPlayer());
	}
	/**
	 * Soll testen ob ein Move der nicht eines der exisitierenden Felder beschreibt eine Exception zurückgibt.
	 * (Nur interessant für Clients mit Implementierung eines eigenen xml Protokolls
	 * Hier nicht möglich, da Enum nicht extendable
	 * @throws InvalidMoveException
	 */
	@Test(expected = InvalidMoveException.class)
	public void notExistingSymbol() throws InvalidMoveException {
//		TODO
//		g.start();
//		Move wrongMove = new ForwardMove(0,"lala");
//		MoveContainer mContainer = new MoveContainer(wrongMove);
//		mContainer.perform(gs, gs.getCurrentPlayer());
	}
}
