package sc.plugin2010;

import java.util.LinkedList;
import java.util.List;

import sc.plugin2010.Board.FieldTyp;
import sc.plugin2010.Move.MoveTyp;
import sc.plugin2010.Player.Action;
import sc.plugin2010.Player.Position;

public class GameUtil
{
	public static final int	HEDGEHOG_CARROT_MULTIPLIER			= 10;
	public static final int	CARROT_BONUS_ON_POSITION_1_FIELD	= 10;
	public static final int	CARROT_BONUS_ON_POSITION_2_FIELD	= 30;
	public static final int	CARROT_BONUS_ON_POSITION_1_SALAD	= 10;
	public static final int	CARROT_BONUS_ON_POSITION_2_SALAD	= 30;

	/**
	 * Berechnet wie viele Karotten für einen Zug der länge
	 * <code>moveCount</code> benötigt werden. Entspricht den Veränderungen des
	 * Spieleabends der CAU.
	 * 
	 * @param moveCount
	 *            how many fields to move (must be positive)
	 * @return count of carrots needed
	 */
	public static int calculateCarrots(int moveCount)
	{
		return (moveCount * (moveCount + 1)) / 2;
	}

	/**
	 * Berechnet, wieviele Züge mit <code>carrots</code> Karotten möglich sind.
	 * 
	 * @param carrots
	 *            the carrots you want to spend
	 * @return moves that you can do maximal
	 */
	public static int calculateMoveableFields(int carrots)
	{
		int moves = 0;

		while (calculateCarrots(++moves) <= carrots)
		{
			;
		}

		return moves - 1;
	}

	/**
	 * Überprüft <code>MoveTyp.MOVE</code> Züge auf ihre Korrektheit. Folgende
	 * Spielregeln werden beachtet:
	 * 
	 * - Der Spieler muss genügend Karotten für den Zug besitzen - Wenn das Ziel
	 * erreicht wird, darf der Spieler nach dem Zug maximal 10 Karotten übrig
	 * haben - Man darf nicht auf Igelfelder ziehen - Salatfelder dürfen nur
	 * betreten werden, wenn man noch Salate essen muss - Hasenfelder dürfen nur
	 * betreten werden, wenn man noch Hasenkarten ausspielen kann
	 * 
	 * @param b
	 * @param l
	 * @param p
	 * @return
	 */
	public static boolean isValidToMove(Board b, Player p, int l)
	{
		if (l <= 0)
		{
			return false;
		}

		boolean valid = true;
		int requiredCarrots = GameUtil.calculateCarrots(l);
		valid = valid && (requiredCarrots <= p.getCarrotsAvailable());

		int newPosition = p.getFieldNumber() + l;
		valid = valid && !b.isOccupied(newPosition);
		FieldTyp type = b.getTypeAt(newPosition);
		switch (type)
		{
			case INVALID:
				valid = false;
				break;
			case SALAD:
				valid = valid && p.getSaladsToEat() > 0;
				break;
			case RABBIT:
				Player p2 = p.clone();
				p2.addToHistory(new Move(MoveTyp.MOVE, l));
				p2.setFieldNumber(newPosition);
				p2.changeCarrotsAvailableBy(-requiredCarrots);
				valid = valid && canPlayAnyCard(b, p2);
				break;
			case GOAL:
				int carrotsLeft = p.getCarrotsAvailable() - requiredCarrots;
				valid = valid && carrotsLeft <= 10;
				valid = valid && p.getSaladsToEat() == 0;
				break;
			case HEDGEHOG:
				valid = false;
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

	public static boolean isValidToSkip(Board b, Player p)
	{
		return !canDoAnything(b, p);
	}

	private static boolean canDoAnything(Board b, Player p)
	{
		return canPlayAnyCard(b, p) || isValidToFallBack(b, p)
				|| isValidToTakeOrDrop10Carrots(b, p, 10)
				|| isValidToTakeOrDrop10Carrots(b, p, -10)
				|| isValidToEat(b, p) || canMoveToAnyField(b, p);
	}

	private static boolean canMoveToAnyField(Board b, Player p)
	{
		int fields = calculateMoveableFields(p.getCarrotsAvailable());
		for (int i = 0; i < fields; i++)
		{
			if (isValidToMove(b, p, i))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Überprüft <code>MoveTyp.EAT</code> Züge auf Korrektheit. Um einen Salat
	 * zu verzehren muss der Spieler sich:
	 * 
	 * - auf einem Salatfeld befinden - noch mindestens einen Salat besitzen -
	 * vorher kein Salat auf diesem Feld verzehrt wurde
	 * 
	 * @param b
	 * @param p
	 * @return
	 */
	public static boolean isValidToEat(Board b, Player p)
	{
		boolean valid = true;
		FieldTyp currentField = b.getTypeAt(p.getFieldNumber());

		valid = valid && (currentField.equals(FieldTyp.SALAD));
		valid = valid && (p.getSaladsToEat() > 0);
		valid = valid && !playerMustMove(b, p);

		return valid;
	}

	public static boolean playerMustMove(Board b, Player p)
	{
		FieldTyp type = b.getTypeAt(p.getFieldNumber());

		if (type == FieldTyp.HEDGEHOG || type == FieldTyp.START)
		{
			return true;
		}

		Move lastMove = p.getLastNonSkipMove();

		if (lastMove != null)
		{
			if (lastMove.getTyp() == MoveTyp.EAT)
			{
				return true;
			}
			else if (lastMove.getTyp() == MoveTyp.PLAY_CARD)
			{
				if (lastMove.getCard() == Player.Action.EAT_SALAD)
				{
					return true;
				}
				else if (lastMove.getCard() == Player.Action.TAKE_OR_DROP_CARROTS)
				{
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isValidToTakeOrDrop10Carrots(Board b, Player p, int n)
	{
		boolean valid = b.getTypeAt(p.getFieldNumber()).equals(FieldTyp.CARROT);
		if (n == 10)
		{
			return valid;
		}
		if (n == -10)
		{
			if (p.getCarrotsAvailable() >= 10)
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
	 * Überprüft <code>MoveTyp.FALL_BACK</code> Züge auf Korrektheit
	 * 
	 * @param b
	 * @param p
	 * @return
	 */
	public static boolean isValidToFallBack(Board b, Player p)
	{
		boolean valid = true;
		int newPosition = b.getPreviousFieldByTyp(FieldTyp.HEDGEHOG, p
				.getFieldNumber());
		valid = valid && (newPosition != -1);
		valid = valid && !b.isOccupied(newPosition);
		return valid;
	}

	public static boolean isValidToPlayFallBack(Board b, Player p)
	{
		boolean valid = !playerMustMove(b, p) && isOnRabbitField(b, p)
				&& b.isFirst(p);

		valid = valid && p.ownsCardOfTyp(Action.FALL_BACK);

		final Player o = b.getOtherPlayer(p);
		int nextPos = o.getFieldNumber() - 1;

		FieldTyp type = b.getTypeAt(nextPos);
		switch (type)
		{
			case INVALID:
			case HEDGEHOG:
				valid = false;
				break;
			case START:
				break;
			case SALAD:
				valid = valid && p.getSaladsToEat() > 0;
				break;
			case RABBIT:
				Player p2 = (Player) p.clone();
				p2.setFieldNumber(nextPos);
				p2.addToHistory(new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK));
				p2.setActions(p.getActionsWithout(Action.FALL_BACK));
				valid = valid && canPlayAnyCard(b, p2);
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

	public static boolean isValidToPlayHurryAhead(final Board b, final Player p)
	{
		boolean valid = !playerMustMove(b, p) && isOnRabbitField(b, p)
				&& !b.isFirst(p);
		valid = valid && p.ownsCardOfTyp(Action.HURRY_AHEAD);

		final Player o = b.getOtherPlayer(p);
		int nextPos = o.getFieldNumber() + 1;

		FieldTyp type = b.getTypeAt(nextPos);
		switch (type)
		{
			case INVALID:
			case HEDGEHOG:
				valid = false;
				break;
			case SALAD:
				valid = valid && p.getSaladsToEat() > 0;
				break;
			case RABBIT:
				Player p2 = p.clone();
				p2.setFieldNumber(nextPos);
				p2
						.addToHistory(new Move(MoveTyp.PLAY_CARD,
								Action.HURRY_AHEAD));
				p2.setActions(p.getActionsWithout(Action.HURRY_AHEAD));
				valid = valid && canPlayAnyCard(b, p2);
				break;
			case GOAL:
				valid = valid && b.canEnterGoal(p);
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

	public static boolean isValidToPlayTakeOrDropCarrots(Board b, Player p)
	{
		return !playerMustMove(b, p) && isOnRabbitField(b, p)
				&& p.ownsCardOfTyp(Action.TAKE_OR_DROP_CARROTS);
	}

	public static boolean isValidToPlayEatSalad(Board b, Player p)
	{
		return !playerMustMove(b, p) && isOnRabbitField(b, p)
				&& p.ownsCardOfTyp(Action.EAT_SALAD) && p.getSaladsToEat() > 0;
	}

	private static boolean isOnRabbitField(Board b, Player p)
	{
		return b.getTypeAt(p.getFieldNumber()).equals(FieldTyp.RABBIT);
	}

	public static Position getGameResult(Player relevant, Player o)
	{
		Position ret = null;
		if (o.getFieldNumber() <= relevant.getFieldNumber())
			ret = Position.FIRST;
		if (relevant.inGoal() && o.getFieldNumber() == relevant.getFieldNumber())
		{
			if (o.getCarrotsAvailable()>relevant.getCarrotsAvailable())
				ret = Position.SECOND;
			else if (o.getCarrotsAvailable() == relevant.getCarrotsAvailable())
				ret = Position.TIE;
		}
		return ret;
	}
	
	private static boolean canPlayAnyCard(Board b, Player p)
	{
		boolean valid = false;

		for (final Action a : p.getActions())
		{
			switch (a)
			{
				case EAT_SALAD:
					valid = valid || isValidToPlayEatSalad(b, p);
					break;
				case FALL_BACK:
					valid = valid || isValidToPlayFallBack(b, p);
					break;
				case HURRY_AHEAD:
					valid = valid || isValidToPlayHurryAhead(b, p);
					break;
				case TAKE_OR_DROP_CARROTS:
					valid = valid || isValidToPlayTakeOrDropCarrots(b, p);
					break;
				default:
					throw new IllegalArgumentException("Unknown CardType " + a);
			}
		}

		return valid;
	}

	public static boolean isValidToPlayCard(Board b, Player p, Action c, int n)
	{
		boolean valid = false;
		switch (c)
		{
			case EAT_SALAD:
				valid = isValidToPlayEatSalad(b, p);
				break;
			case FALL_BACK:
				valid = isValidToPlayFallBack(b, p);
				break;
			case HURRY_AHEAD:
				valid = isValidToPlayHurryAhead(b, p);
				break;
			case TAKE_OR_DROP_CARROTS:
				valid = isValidToPlayTakeOrDropCarrots(b, p)
						&& (n == 20 || n == -20 || n == 0);
				break;
			default:
				throw new IllegalArgumentException("Unknown CardType " + c);
		}
		return valid;
	}

	public static String displayMoveAction(Move mov)
	{
		if (mov != null)
		{
			switch (mov.getTyp())
			{
				case EAT:
					return "frisst einen Salat";
				case MOVE:
					String str = String.valueOf(mov.getN())
							+ " Felder vorwärts";

					if (mov.getN() == 1)
					{
						str = String.valueOf(mov.getN()) + " Feld vorwärts";
					}

					return "setzt " + str;
				case TAKE_OR_DROP_CARROTS:
					String res = "";
					if (mov.getN() == 10)
					{
						res = "nimmt 10 Karotten";
					}
					else if (mov.getN() == -10)
					{
						res = "gibt 10 Karotten ab";
					}
					return res;
				case FALL_BACK:
					return "lässt sich auf Igel zurückfallen";
				case PLAY_CARD:
					switch (mov.getCard())
					{
						case TAKE_OR_DROP_CARROTS:
							return "spielt 'Nimm oder gib 20 Karotten'";
						case EAT_SALAD:
							return "spielt 'Friss sofort einen Salat'";
						case FALL_BACK:
							return "spielt 'Falle eine Position zurück'";
						case HURRY_AHEAD:
							return "spielt 'Rücke eine Position vor'";
						default:
							break;
					}
					break;
				default:
					break;
			}
		}
		return "";
	}
}
