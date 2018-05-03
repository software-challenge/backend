package sc.plugin2018.util;

import sc.plugin2018.*;

public class GameRuleLogic
{

	private GameRuleLogic()
	{
		throw new IllegalStateException("Can't be instantiated.");
	}

	/**
	 * Berechnet wie viele Karotten für einen Zug der Länge
	 * <code>moveCount</code> benötigt werden.
	 * 
	 * @param moveCount Anzahl der Felder, um die bewegt wird
	 * @return Anzahl der benötigten Karotten
	 */
	public static int calculateCarrots(int moveCount)
	{
		return (moveCount * (moveCount + 1)) / 2;
	}

	/**
	 * Berechnet, wie viele Züge mit <code>carrots</code> Karotten möglich sind.
	 * 
	 * @param carrots maximal ausgegebene Karotten
	 * @return Felder um die maximal bewegt werden kann
	 */
	public static int calculateMoveableFields(int carrots)
	{
		if (carrots >=990) {
      return 44;
    }
    if (carrots < 1) {
      return 0;
    }
    return (int)(Math.sqrt(((double) 2* carrots) + 0.25) - 0.48); //-0.48 anstelle von -0.5 um Rundungsfehler zu vermeiden
	}

	/**
	 * Überprüft <code>Advance</code> Aktionen auf ihre Korrektheit. Folgende
	 * Spielregeln werden beachtet:
	 * 
	 * - Der Spieler muss genügend Karotten für den Zug besitzen
   * - Wenn das Ziel erreicht wird, darf der Spieler nach dem Zug maximal 10 Karotten übrig haben
   * - Man darf nicht auf Igelfelder ziehen
   * - Salatfelder dürfen nur betreten werden, wenn man noch Salate essen muss
   * - Hasenfelder dürfen nur betreten werden, wenn man noch Karte ausspielen kann
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
		valid = valid && (requiredCarrots <= player.getCarrots());

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
        state2.getCurrentPlayer().changeCarrotsBy(-requiredCarrots);
				valid = valid && canPlayAnyCard(state2);
				break;
			case GOAL:
				int carrotsLeft = player.getCarrots() - requiredCarrots;
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
   * Überprüft, ob ein Spieler einen Zug (keinen Aussetzug)
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
		int fields = calculateMoveableFields(state.getCurrentPlayer().getCarrots());
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
	 * @return true, falls ein Salat gegessen werden darf
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
   * Überprüft ab der derzeitige Spieler im nächsten Zug einen Vorwärtszug machen muss.
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
				// the player has to leave a hare field in next turn
				if (((Card)lastAction).getType() == CardType.EAT_SALAD)
				{
					return true;
				}
				else if (((Card)lastAction).getType() == CardType.TAKE_OR_DROP_CARROTS) // the player has to leave the hare field
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
			if (player.getCarrots() >= 10)
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
		boolean valid = !playerMustAdvance(state) && state.isOnHareField()
				&& state.isFirst(player);

		valid = valid && player.ownsCardOfType(CardType.FALL_BACK);

		final Player o = state.getOpponent(player);
		int nextPos = o.getFieldIndex() - 1;
		if (nextPos == 0) {
			return false;
		}

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
        state2.setLastAction(new Card(CardType.FALL_BACK));
        state2.getCurrentPlayer().setFieldIndex(nextPos);
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
		boolean valid = !playerMustAdvance(state) && state.isOnHareField()
				&& !state.isFirst(player);
		valid = valid && player.ownsCardOfType(CardType.HURRY_AHEAD);

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
        state2.getCurrentPlayer().setFieldIndex(nextPos);
				state2.getCurrentPlayer().setCards(player.getCardsWithout(CardType.HURRY_AHEAD));
				valid = valid && canPlayAnyCard(state2);
				break;
			case GOAL:
				valid = valid && canEnterGoal(state);
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
		boolean valid = !playerMustAdvance(state) && state.isOnHareField()
				&& player.ownsCardOfType(CardType.TAKE_OR_DROP_CARROTS);

		valid = valid && (n == 20 || n == -20 || n == 0);
		if (n < 0)
		{
			valid = valid && ((player.getCarrots() + n) >= 0);
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
		return !playerMustAdvance(state) && state.isOnHareField()
				&& player.ownsCardOfType(CardType.EAT_SALAD) && player.getSalads() > 0;
	}

  /**
   * Überprüft ob der derzeitige Spieler irgendeine Karte spielen kann.
   * TAKE_OR_DROP_CARROTS wird nur mit 20 überprüft
   * @param state GameState
   * @return true, falls das Spielen einer Karte möglich ist
   */
	private static boolean canPlayAnyCard(GameState state)
	{
		for (final CardType card : state.getCurrentPlayer().getCards()) {
			if(canPlayCard(state, card))
				return true;
		}

		return false;
	}

	private static boolean canPlayCard(GameState state, CardType card) {
		switch (card)
		{
			case EAT_SALAD:
				return isValidToPlayEatSalad(state);
			case FALL_BACK:
				return isValidToPlayFallBack(state);
			case HURRY_AHEAD:
				return isValidToPlayHurryAhead(state);
			case TAKE_OR_DROP_CARROTS:
				return isValidToPlayTakeOrDropCarrots(state, 20);
			default:
				throw new IllegalArgumentException("Unknown CardType " + card);
		}
	}

	/**
   * Überprüft ob der derzeitige Spieler die Karte spielen kann.
   * @param state derzeitiger GameState
   * @param c Karte die gespielt werden soll
   * @param n Wert fuer TAKE_OR_DROP_CARROTS
   * @return true, falls das Spielen der entsprechenden Karte möglich ist
   */
	public static boolean isValidToPlayCard(GameState state, CardType c, int n)
	{
		if(c == CardType.TAKE_OR_DROP_CARROTS)
			return isValidToPlayTakeOrDropCarrots(state, n);
		else
			return canPlayCard(state, c);
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
   * Gibt zurück, ob der derzeitige Spieler eine Karte spielen kann.
   * @param state derzeitiger GameState
   * @return true, falls eine Karte gespielt werden kann
   */
	public static boolean canPlayCard(GameState state)
	{
		return state.fieldOfCurrentPlayer() == FieldType.HARE && canPlayAnyCard(state);
	}

	/**
	 * Überprüft ob eine Karte gespielt werden muss. Sollte nach einem
	 * Zug eines Spielers immer false sein, ansonsten ist Zug ungültig.
	 * @param state derzeitiger GameState
	 */
	public static boolean mustPlayCard(GameState state) {
		return state.getCurrentPlayer().mustPlayCard();
	}


	/**
	 * Überprüft ob ein der derzeitige Spieler das Ziel betreten darf
	 * @param state GameState
	 * @return Gibt zurück, ob ein Spieler das Ziel betreten darf
	 */
	public static boolean canEnterGoal(GameState state)
	{
		Player player = state.getCurrentPlayer();
		return player.getCarrots() <= 10
						&& player.getSalads() == 0;
	}

}
