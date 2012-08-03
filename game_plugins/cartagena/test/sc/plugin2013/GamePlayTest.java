package sc.plugin2013;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.plugin2013.util.Configuration;
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

	/**
	 * Testet ob ein negativer FeldIndex bestraft wird
	 * 
	 * @throws InvalidMoveException
	 */
	@Test(expected = InvalidMoveException.class)
	public void negativeFieldMoveForward() throws InvalidMoveException {
		g.start();
		Move wrongMove = new ForwardMove(-1, SymbolType.BOTTLE);
		MoveContainer mContainer = new MoveContainer(wrongMove);
		mContainer.perform(gs, gs.getCurrentPlayer());
	}

	/**
	 * Testet ob ein zu großer Feldindex abgefangen wird
	 * 
	 * @throws InvalidMoveException
	 */
	@Test(expected = InvalidMoveException.class)
	public void toHighFieldMoveForward() throws InvalidMoveException {
		g.start();
		Move wrongMove = new ForwardMove(b.size(), SymbolType.BOTTLE);
		MoveContainer mContainer = new MoveContainer(wrongMove);
		mContainer.perform(gs, gs.getCurrentPlayer());
	}

	/**
	 * Testet ob ein negativer FeldIndex bestraft wird
	 * 
	 * @throws InvalidMoveException
	 */
	@Test(expected = InvalidMoveException.class)
	public void negativeFieldMoveBackward() throws InvalidMoveException {
		g.start();
		Move wrongMove = new BackwardMove(-5);
		MoveContainer mContainer = new MoveContainer(wrongMove);
		mContainer.perform(gs, gs.getCurrentPlayer());
	}

	/**
	 * Testet ob ein zu großer Feldindex abgefangen wird
	 * 
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
	 * Überprüft ob ein Vorwärtszug, welcher einen falschen Symbolnamen enthält,
	 * erkannt und bestraft wird
	 * 
	 * @throws InvalidMoveException
	 */
	@Test(expected = InvalidMoveException.class)
	public void notExistingSymbol() throws InvalidMoveException {
		g.start();
		XStream xstream = Configuration.getXStream();
		String wrongForwardMove = "<data class=\"cartagena:forwardMove\" fieldIndex=\"0\" symbol=\"GADDER\"/>";
		ForwardMove fw = (ForwardMove) xstream.fromXML(wrongForwardMove);
		fw.perform(gs, red);
	}
}
