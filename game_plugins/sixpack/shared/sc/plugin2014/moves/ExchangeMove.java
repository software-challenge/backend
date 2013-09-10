package sc.plugin2014.moves;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sc.plugin2014.GameState;
import sc.plugin2014.converters.ExchangeMoveConverter;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.PlayerColor;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.exceptions.InvalidMoveException;
import sc.plugin2014.exceptions.StoneBagIsEmptyException;
import sc.plugin2014.laylogic.PointsCalculator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Repräsentiert einen Tauschzug.
 * 
 * @author ffi
 * 
 */
@XStreamAlias(value = "exchangemove")
@XStreamConverter(ExchangeMoveConverter.class)
public class ExchangeMove extends Move implements Cloneable {

	private final List<Stone> stones;

	/**
	 * Erzeugt ein neues ExchangeMove Objekt. Initialisiert die Liste der
	 * Steine.
	 */
	public ExchangeMove() {
		stones = new ArrayList<Stone>();
	}

	/**
	 * Erzeugt ein neues ExchangeMove Objekt mit übergebener Liste von
	 * Spielsteinen.
	 * 
	 * @param stones
	 *            Liste mit Spielsteinen, welche getauscht werden sollen.
	 */
	public ExchangeMove(List<Stone> stones) {
		this.stones = stones;
	}

	/**
	 * Liefert die Liste mit Steinen, welche getauscht werden sollen.
	 * 
	 * @return
	 */
	public List<Stone> getStonesToExchange() {
		return stones;
	}

	@Override
	public void perform(GameState state, Player player)
			throws InvalidMoveException, StoneBagIsEmptyException {
		super.perform(state, player);

		checkAtLeastOneStone();

		checkIfStonesAreFromPlayerHand(getStonesToExchange(), player);

		checkIfPlayerHasStoneAmountToExchange(player);

		int points = PointsCalculator.getPointsForExchangeMove(
				getStonesToExchange(), state.getBoard());

		player.addPoints(points);

		List<Integer> freePositions = new ArrayList<Integer>();
		List<Stone> putAsideStones = new ArrayList<Stone>();

		int stonesToExchangeSize = getStonesToExchange().size();

		for (int i = 0; i < stonesToExchangeSize; i++) {
			Stone stoneExchange = getStonesToExchange().get(i);

			freePositions.add(player.getStonePosition(stoneExchange));
			player.removeStone(stoneExchange);
			putAsideStones.add(stoneExchange);
		}

		for (int i = 0; i < stonesToExchangeSize; i++) {
			Stone stone = state.drawStone();
			if (stone != null) {
				player.addStone(stone, freePositions.get(i));
			} else {
				state.updateStonesInBag();
				throw new StoneBagIsEmptyException("Der Beutel ist leer.");
			}
		}

		for (Stone stone : putAsideStones) {
			state.putBackStone(stone);
		}

		state.updateStonesInBag();
	}
	
	public void perform(GameState state, PlayerColor color)
			throws InvalidMoveException, StoneBagIsEmptyException {
		super.perform(state, color);
		Player player;
		if (color == PlayerColor.BLUE) {
			player = state.getBluePlayer();
		} else {
			player = state.getRedPlayer();
		}
		this.perform(state, player);
	}
	
	public void perform(GameState state) throws InvalidMoveException,
	StoneBagIsEmptyException {
		super.perform(state);
		this.perform(state, state.getCurrentPlayer());
	}

	/**
	 * Überprüft ob ein übergebener Spieler genug Spielsteine auf der Hand hat
	 * um diese zu tauschen.
	 * 
	 * @param player
	 * @throws InvalidMoveException
	 */
	private void checkIfPlayerHasStoneAmountToExchange(Player player)
			throws InvalidMoveException {
		if (getStonesToExchange().size() > player.getStones().size()) {
			throw new InvalidMoveException(
					"Nicht ausreichend Steine auf der Hand um "
							+ getStonesToExchange().size()
							+ " Steine zu tauschen");
		}
	}

	/**
	 * Überprüft ob dieser Tauschzug mindestens einen Spielstein in seiner Liste
	 * hat.
	 * 
	 * @throws InvalidMoveException
	 */
	private void checkAtLeastOneStone() throws InvalidMoveException {
		if (getStonesToExchange().isEmpty()) {
			throw new InvalidMoveException(
					"Es muss mindestens 1 Stein getauscht werden");
		}
	}

	/** Klont dieses Objekt. (deep-copy)
	 * @see sc.plugin2014.moves.Move#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		ExchangeMove clone = new ExchangeMove();
		for (Stone stone : stones) {
			clone.stones.add((Stone) stone.clone());
		}
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ExchangeMove) {
			ExchangeMove em = (ExchangeMove) obj;
			for (Stone stone : stones) {
				if (!em.stones.contains(stone)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

}
