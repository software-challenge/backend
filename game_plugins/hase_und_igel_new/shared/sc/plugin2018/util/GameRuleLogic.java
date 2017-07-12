package sc.plugin2018.util;

import sc.plugin2018.*;

// TODO only state not player as parameter

public class GameRuleLogic
{
	public static final int	HEDGEHOG_CARROT_MULTIPLIER			= 10;
	public static final int	CARROT_BONUS_ON_POSITION_1_FIELD	= 10;
	public static final int	CARROT_BONUS_ON_POSITION_2_FIELD	= 30;
	public static final int	CARROT_BONUS_ON_POSITION_1_SALAD	= 10;
	public static final int	CARROT_BONUS_ON_POSITION_2_SALAD	= 30;

	private GameRuleLogic()
	{
		throw new IllegalStateException("Can't be instantiated.");
	}

	/**
	 * Berechnet wie viele Karotten für einen Zug der länge
	 * <code>moveCount</code> benötigt werden. Entspricht den Veränderungen des
	 * Spieleabends der CAU.
	 * 
	 * @param moveCount Anzahl der Felder, um die bewegt wird
	 * @return Anzahl der benötigten Karotten
	 */
	public static int calculateCarrots(int moveCount)
	{
		return (moveCount * (moveCount + 1)) / 2;
	}

	/**
	 * Berechnet, wieviele Züge mit <code>carrots</code> Karotten möglich sind.
	 * 
	 * @param carrots maximal ausgegebene Karotten
	 * @return Felder um die maximal bewegt werden kann
	 */
	public static int calculateMoveableFields(int carrots)
	{
		int moves = 0;
		while (calculateCarrots(moves) <= carrots)
		{
			moves++;
		}
		return moves - 1;
	}

	/**
	 * Überprüft <code>Advance</code> Aktionen auf ihre Korrektheit. Folgende
	 * Spielregeln werden beachtet:
	 * 
	 * - Der Spieler muss genügend Karotten für den Zug besitzen
   * - Wenn das Ziel erreicht wird, darf der Spieler nach dem Zug maximal 10 Karotten übrig haben
   * - Man darf nicht auf Igelfelder ziehen
   * - Salatfelder dürfen nur betreten werden, wenn man noch Salate essen muss
   * - Hasenfelder dürfen nur betreten werden, wenn man noch Hasenkarten ausspielen kann
	 * 
	 * @param state GameState
	 * @param distance relativer Abstand zur aktuellen Position des Spielers
	 * @return true, falls ein Vorwärtszug möglich ist
	 */
	public static boolean isValidToAdvance(GameState state, int distance)
	{
		if (distance <= 0)
		{
			return false;
		}
    Player player = state.getCurrentPlayer();
    if (mustEatSalad(state)) {
      return false;
    }
		boolean valid = true;
		int requiredCarrots = GameRuleLogic.calculateCarrots(distance);
		valid = valid && (requiredCarrots <= player.getCarrotsAvailable());

		int newPosition = player.getFieldIndex() + distance;
		valid = valid && !state.isOccupied(newPosition);
		FieldType type = state.getBoard().getTypeAt(newPosition);
		switch (type)
		{
			case INVALID:
				valid = false;
				break;
			case SALAD:
				valid = valid && player.getSalads() > 0;
				break;
			case HARE:
				GameState state2 = null;
        try {
          state2 = state.clone();
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
        state2.setLastAction(new Advance(distance));
        state2.getCurrentPlayer().setFieldIndex(newPosition);
        state2.getCurrentPlayer().changeCarrotsAvailableBy(-requiredCarrots);
				valid = valid && canPlayAnyCard(state2);
				break;
			case GOAL:
				int carrotsLeft = player.getCarrotsAvailable() - requiredCarrots;
				valid = valid && carrotsLeft <= 10;
				valid = valid && player.getSalads() == 0;
				break;
			case HEDGEHOG:
				valid = false;
				break;
			case CARROT:
			case POSITION_1:
			case START:
			case POSITION_2:
				break;
			default:
				throw new IllegalStateException("Unknown Type " + type);

		}
		return valid;
	}

  /**
   * Überprüft, ob ein Spieler aussetzen darf. Er darf dies, wenn kein anderer Zug möglich ist.
   * @param state GameState
   * @return true, falls der derzeitige Spieler keine andere Aktion machen kann.
   */
	public static boolean isValidToSkip(GameState state)
	{
		return !canDoAnything(state);
	}

  /**
   * Überpürft, ob ein Spieler einen Zug (keinen Aussetzug)
   * @param state GameState
   * @return true, falls ein Zug möglich ist.
   */
	private static boolean canDoAnything(GameState state)
	{
		return canPlayAnyCard(state) || isValidToFallBack(state)
				|| isValidToExchangeCarrots(state, 10)
				|| isValidToExchangeCarrots(state, -10)
				|| isValidToEat(state) || canAdvanceToAnyField(state);
	}

  /**
   * Überprüft ob der derzeitige Spieler zu irgendeinem Feld einen Vorwärtszug machen kann.
   * @param state GameState
   * @return true, falls der Spieler irgendeinen Vorwärtszug machen kann
   */
	private static boolean canAdvanceToAnyField(GameState state)
	{
		int fields = calculateMoveableFields(state.getCurrentPlayer().getCarrotsAvailable());
		for (int i = 0; i <= fields; i++)
		{
			if (isValidToAdvance(state, i))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Überprüft <code>EatSalad</code> Züge auf Korrektheit. Um einen Salat
	 * zu verzehren muss der Spieler sich:
	 * 
	 * - auf einem Salatfeld befinden
   * - noch mindestens einen Salat besitzen
   * - vorher kein Salat auf diesem Feld verzehrt wurde
	 * 
	 * @param state GameState
	 * @return true, falls ein Salad gegessen werden darf
	 */
	public static boolean isValidToEat(GameState state)
	{
	  Player player = state.getCurrentPlayer();
		boolean valid = true;
		FieldType currentField = state.getTypeAt(player.getFieldIndex());

		valid = valid && (currentField.equals(FieldType.SALAD));
		valid = valid && (player.getSalads() > 0);
		valid = valid && !playerMustAdvance(state);

		return valid;
	}

  /**
   * Überpürft ab der derzeitige Spieler im nächsten Zug einen Vorwärtszug machen muss.
   * @param state GameState
   * @return true, falls der derzeitige Spieler einen Vorwärtszug gemacht werden muss
   */
	public static boolean playerMustAdvance(GameState state)
	{
	  Player player = state.getCurrentPlayer();
		FieldType type = state.getTypeAt(player.getFieldIndex());

		if (type == FieldType.HEDGEHOG || type == FieldType.START)
		{
			return true;
		}

		Action lastAction = state.getLastNonSkipAction(player);

		if (lastAction != null)
		{
			if (lastAction instanceof EatSalad)
			{
				return true;
			}
			else if (lastAction instanceof Card)
			{
				// the player has to leave a rabbit field in next turn
				if (((Card)lastAction).getType() == CardType.EAT_SALAD)
				{
					return true;
				}
				else if (((Card)lastAction).getType() == CardType.TAKE_OR_DROP_CARROTS) // the player has to leave the rabbit field
				{
					return true;
				}
			}
		}

		return false;
	}

  /**
   * Überprüft ob der derzeitige Spieler 10 Karotten nehmen oder abgeben kann.
   * @param state GameState
   * @param n 10 oder -10 je nach Fragestellung
   * @return true, falls die durch n spezifizierte Aktion möglich ist.
   */
	public static boolean isValidToExchangeCarrots(GameState state, int n)
	{
	  Player player = state.getCurrentPlayer();
		boolean valid = state.getTypeAt(player.getFieldIndex()).equals(FieldType.CARROT);
		if (n == 10)
		{
			return valid;
		}
		if (n == -10)
		{
			if (player.getCarrotsAvailable() >= 10)
			{
				return valid;
			}
			else
			{
				return false;
			}
		}
		return false;
	}

	/**
	 * Überprüft <code>FallBack</code> Züge auf Korrektheit
	 * 
	 * @param state GameState
	 * @return true, falls der currentPlayer einen Rückzug machen darf
	 */
	public static boolean isValidToFallBack(GameState state)
  {
    if (mustEatSalad(state)) {
      return false;
    }
		boolean valid = true;
		int newPosition = state.getPreviousFieldByType(FieldType.HEDGEHOG, state.getCurrentPlayer()
				.getFieldIndex());
		valid = valid && (newPosition != -1);
		valid = valid && !state.isOccupied(newPosition);
		return valid;
	}

  /**
   * Überprüft ob der derzeitige Spieler die <code>FALL_BACK</code> Karte spielen darf.
   * @param state GameState
   * @return true, falls die <code>FALL_BACK</code> Karte gespielt werden darf
   */
	public static boolean isValidToPlayFallBack(GameState state)
	{
	  Player player = state.getCurrentPlayer();
		boolean valid = !playerMustAdvance(state) && state.isOnRabbitField()
				&& state.isFirst(player);

		valid = valid && player.ownsCardOfTyp(CardType.FALL_BACK);

		final Player o = state.getOpponent(player);
		int nextPos = o.getFieldIndex() - 1;

		FieldType type = state.getTypeAt(nextPos);
		switch (type)
		{
			case INVALID:
			case HEDGEHOG:
				valid = false;
				break;
			case START:
				break;
			case SALAD:
				valid = valid && player.getSalads() > 0;
				break;
			case HARE:
        GameState state2 = null;
        try {
          state2 = state.clone();
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
        state2.setLastAction(new Card(CardType.HURRY_AHEAD));
				state2.getCurrentPlayer().setCards(player.getCardsWithout(CardType.FALL_BACK));
				valid = valid && canPlayAnyCard(state2);
				break;
			case CARROT:
			case POSITION_1:
			case POSITION_2:
				break;
			default:
				throw new IllegalStateException("Unknown Type " + type);
		}

		return valid;
	}

  /**
   * Überprüft ob der derzeitige Spieler die <code>HURRY_AHEAD</code> Karte spielen darf.
   * @param state GameState
   * @return true, falls die <code>HURRY_AHEAD</code> Karte gespielt werden darf
   */
	public static boolean isValidToPlayHurryAhead(final GameState state)
	{
	  Player player = state.getCurrentPlayer();
		boolean valid = !playerMustAdvance(state) && state.isOnRabbitField()
				&& !state.isFirst(player);
		valid = valid && player.ownsCardOfTyp(CardType.HURRY_AHEAD);

		final Player o = state.getOpponent(player);
		int nextPos = o.getFieldIndex() + 1;

		FieldType type = state.getTypeAt(nextPos);
		switch (type)
		{
			case INVALID:
			case HEDGEHOG:
				valid = false;
				break;
			case SALAD:
				valid = valid && player.getSalads() > 0;
				break;
			case HARE:
        GameState state2 = null;
        try {
          state2 = state.clone();
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
        state2.setLastAction(new Card(CardType.HURRY_AHEAD));
				state2.getCurrentPlayer().setCards(player.getCardsWithout(CardType.HURRY_AHEAD));
				valid = valid && canPlayAnyCard(state2);
				break;
			case GOAL:
				valid = valid && state.canEnterGoal(player);
				break;
			case CARROT:
			case POSITION_1:
			case POSITION_2:
			case START:
				break;
			default:
				throw new IllegalStateException("Unknown Type " + type);
		}

		return valid;
	}

  /**
   * Überprüft ob der derzeitige Spieler die <code>TAKE_OR_DROP_CARROTS</code> Karte spielen darf.
   * @param state GameState
   * @param n 20 für nehmen, -20 für abgeben, 0 für nichts tun
   * @return true, falls die <code>TAKE_OR_DROP_CARROTS</code> Karte gespielt werden darf
   */
	public static boolean isValidToPlayTakeOrDropCarrots(GameState state, int n)
	{
	  Player player = state.getCurrentPlayer();
		boolean valid = !playerMustAdvance(state) && state.isOnRabbitField()
				&& player.ownsCardOfTyp(CardType.TAKE_OR_DROP_CARROTS);

		valid = valid && (n == 20 || n == -20 || n == 0);
		if (n < 0)
		{
			valid = valid && ((player.getCarrotsAvailable() + n) >= 0);
		}
		return valid;
	}

  /**
   * Überprüft ob der derzeitige Spieler die <code>EAT_SALAD</code> Karte spielen darf.
   * @param state GameState
   * @return true, falls die <code>EAT_SALAD</code> Karte gespielt werden darf
   */
	public static boolean isValidToPlayEatSalad(GameState state)
	{
	  Player player = state.getCurrentPlayer();
		return !playerMustAdvance(state) && state.isOnRabbitField()
				&& player.ownsCardOfTyp(CardType.EAT_SALAD) && player.getSalads() > 0;
	}

  /**
   * Überprüft ob der derzeitige Spieler irgendeine Karte spielen kann.
   * TAKE_OR_DROP_CARROTS wird nur mit 20 überprüft
   * @param state GameState
   * @return true, falls das Spielen einer Karte möglich ist
   */
	private static boolean canPlayAnyCard(GameState state)
	{
		boolean valid = false;
		Player player = state.getCurrentPlayer();

		for (final CardType a : player.getCards())
		{
			switch (a)
			{
				case EAT_SALAD:
					valid = valid || isValidToPlayEatSalad(state);
					break;
				case FALL_BACK:
					valid = valid || isValidToPlayFallBack(state);
					break;
				case HURRY_AHEAD:
					valid = valid || isValidToPlayHurryAhead(state);
					break;
				case TAKE_OR_DROP_CARROTS:
					valid = valid || isValidToPlayTakeOrDropCarrots(state, 20);
					break;
				default:
					throw new IllegalArgumentException("Unknown CardType " + a);
			}
		}

		return valid;
	}

  /**
   * Überprüft ob der derzeitige Spieler die Karte spielen kann.
   * @param state
   * @param c Karte die gespielt werden soll
   * @param n Parameter mit dem TAKE_OR_DROP_CARROTS überprüft wird
   * @return true, falls das Spielen der entsprechenden karte möglich ist
   */
	public static boolean isValidToPlayCard(GameState state, CardType c, int n)
	{
		boolean valid;
		switch (c)
		{
			case EAT_SALAD:
				valid = isValidToPlayEatSalad(state);
				break;
			case FALL_BACK:
				valid = isValidToPlayFallBack(state);
				break;
			case HURRY_AHEAD:
				valid = isValidToPlayHurryAhead(state);
				break;
			case TAKE_OR_DROP_CARROTS:
				valid = isValidToPlayTakeOrDropCarrots(state, n);
				break;
			default:
				throw new IllegalArgumentException("Unknown CardType " + c);
		}
		return valid;
	}

	public static boolean mustEatSalad(GameState state) {
    Player player = state.getCurrentPlayer();
    // check whether player just moved to salad field and must eat salad
    FieldType field = state.getBoard().getTypeAt(player.getFieldIndex());
    if (field == FieldType.SALAD) {
      if (player.getLastNonSkipAction() instanceof Advance) {
        return true;
      } else if (player.getLastNonSkipAction() instanceof Card) {
        if (((Card) player.getLastNonSkipAction()).getType() == CardType.FALL_BACK ||
                ((Card) player.getLastNonSkipAction()).getType() == CardType.HURRY_AHEAD) {
          return true;
        }
      }

    }
    return false;
  }

  /**
   * TODO difference isValidToPlayCard
   * @param state
   * @return
   */
	public static boolean canPlayCard(GameState state)
	{
	  Player player = state.getCurrentPlayer();
		boolean canPlayCard = state.getTypeAt(player.getFieldIndex()).equals(
				FieldType.HARE);
		for (CardType a : player.getCards())
		{
			canPlayCard = canPlayCard || isValidToPlayCard(state, a, 0);
		}
		return canPlayCard;
	}

  /**
   * TODO difference isVAlidTOMove
   * @param state
   * @return
   */
	public static boolean canMove(GameState state)
	{
		boolean canMove = false;
		int maxDistance = GameRuleLogic.calculateMoveableFields(state.getCurrentPlayer().getCarrotsAvailable());
		for (int i = 1; i <= maxDistance; i++)
		{
			canMove = canMove || isValidToAdvance(state, i);
		}
		return canMove;
	}

	/**
	 * Überprüft ob eine Karte gespielt werden muss. Sollte nach einem
	 * Zug eines Spielers immer false sein, ansonsten ist Zug ungültig.
	 * @param state derzeitiger GameState
	 */
	public static boolean mustPlayerCard(GameState state) {
		return state.getCurrentPlayer().mustPlayCard();
	}
}
