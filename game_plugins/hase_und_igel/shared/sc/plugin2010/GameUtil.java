package sc.plugin2010;

import java.util.LinkedList;
import java.util.List;

import sc.plugin2010.Board.FieldTyp;
import sc.plugin2010.Move.MoveTyp;
import sc.plugin2010.Player.Action;

public class GameUtil
{
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

	public static boolean isValidToMove(Board b, Player p, int l)
	{
		return isValidToMove(b, p, l, true);
	}

	/**
	 * Überprüft <code>MoveTyp.MOVE</code> Züge auf ihre Korrektheit. Folgende
	 * Spielregeln werden beachtet:
	 * 
	 * - Der Spieler muss genügend Karotten für den Zug besitzen
	 * - Wenn das Ziel erreicht wird, darf der Spieler nach dem Zug maximal 10
	 * Karotten übrig haben
	 * - Man darf nicht auf Igelfelder ziehen
	 * - Salatfelder dürfen nur betreten werden, wenn man noch Salate essen muss
	 * - Hasenfelder dürfen nur betreten werden, wenn man noch Hasenkarten
	 * ausspielen kann
	 * 
	 * @param b
	 * @param l
	 * @param p
	 * @return
	 */
	public static boolean isValidToMove(Board b, Player p, int l,
			boolean checkCarrots)
	{
		boolean valid = true;
		int requiredCarrots = GameUtil.calculateCarrots(l);
		valid = valid && l > 0;

		if (checkCarrots)
			valid = valid && (requiredCarrots <= p.getCarrotsAvailable());

		int newPosition = p.getFieldNumber() + l;
		valid = valid && !b.isOccupied(newPosition);
		switch (b.getTypeAt(newPosition))
		{
			case INVALID:
				valid = false;
				break;
			case SALAD:
				valid = valid && p.getSaladsToEat() > 0;
				break;
			case RABBIT:
				Player p2 = p.clone();
				p2.setFieldNumber(newPosition);
				valid = valid && isValidToPlayCard(b, p2);
				break;
			case GOAL:
				int carrotsLeft = p.getCarrotsAvailable() - requiredCarrots;
				valid = valid && carrotsLeft <= 10;
				valid = valid && p.getSaladsToEat() == 0;
				break;
			case HEDGEHOG:
				valid = false;
		}
		return valid;
	}

	/**
	 * Überprüft <code>MoveTyp.EAT</code> Züge auf Korrektheit. Um einen Salat
	 * zu verzehren muss der Spieler sich:
	 * 
	 * - auf einem Salatfeld befinden
	 * - noch mindestens einen Salat besitzen
	 * - vorher kein Salat auf diesem Feld verzehrt wurde
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
		valid = valid && !playerMustMove(p, MoveTyp.EAT);

		return valid;
	}

	public static boolean playerMustMove(Player p, MoveTyp t)
	{
		int lastSaladAt = -1;
		for (final Move m : p.getHistory())
		{
			if (m.getTyp().equals(t))
				lastSaladAt = m.getTurn();
		}
		return !((lastSaladAt == -1) || (p.getHistory().size() - lastSaladAt > 1));
	}

	public static boolean isValidToTakeOrDrop10Carrots(Board b, Player p, int n)
	{
		return b.getTypeAt(p.getFieldNumber()).equals(FieldTyp.CARROT)
				&& (n == 10 || n == -10);
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

	public static List<Action> validCards(Board b, Player p)
	{
		List<Action> ret = new LinkedList<Action>();

		for (final Action a : p.getActions())
		{
			switch (a)
			{
				case HURRY_AHEAD:
					break;
				case TAKE_OR_DROP_CARROTS:
					break;
				case EAT_SALAD:
					break;
				case FALL_BACK:
					break;
			}
		}

		return ret;
	}

	public static boolean isValidToPlayFallBack(Board b, Player p)
	{
		boolean valid = b.getTypeAt(p.getFieldNumber()).equals(FieldTyp.RABBIT)
				&& b.isFirst(p);
		valid = valid && p.ownsCardOfTyp(Action.FALL_BACK);

		final Player o = b.getOtherPlayer(p);
		int nextPos = o.getFieldNumber() - 1;

		switch (b.getTypeAt(nextPos))
		{
			case INVALID:
			case HEDGEHOG:
				valid = false;
				break;
			case START:
				valid = true;
				break;
			case SALAD:
				valid = valid && p.getSaladsToEat() > 0;
				break;
			case RABBIT:
				Player p2 = (Player) p.clone();
				p2.setActions(p.getActionsWithout(Action.FALL_BACK));
				valid = valid && isValidToPlayCard(b, p2);
				break;
		}

		return valid;
	}

	public static boolean isValidToPlayHurryAhead(final Board b, final Player p)
	{
		boolean valid = b.getTypeAt(p.getFieldNumber()).equals(FieldTyp.RABBIT)
				&& !b.isFirst(p);
		valid = valid && p.ownsCardOfTyp(Action.HURRY_AHEAD);

		final Player o = b.getOtherPlayer(p);
		int nextPos = o.getFieldNumber() + 1;

		switch (b.getTypeAt(nextPos))
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
				p2.setActions(p.getActionsWithout(Action.HURRY_AHEAD));
				valid = valid && isValidToPlayCard(b, p2);
				break;
			case GOAL:
				valid = valid && b.canEnterGoal(p);
				break;
		}

		return valid;
	}

	public static boolean isValidToPlayTakeOrDropCarrots(Board b, Player p)
	{
		return b.getTypeAt(p.getFieldNumber()).equals(FieldTyp.RABBIT)
				&& p.ownsCardOfTyp(Action.TAKE_OR_DROP_CARROTS);
	}

	public static boolean isValidToPlayEatSalad(Board b, Player p)
	{
		return b.getTypeAt(p.getFieldNumber()).equals(FieldTyp.RABBIT)
				&& p.ownsCardOfTyp(Action.EAT_SALAD) && p.getSaladsToEat() > 0;
	}

	public static boolean isValidToPlayCard(Board b, Player p)
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
			}
		}

		return valid && b.getTypeAt(p.getFieldNumber()).equals(FieldTyp.RABBIT)
				&& p.getActions().size() > 0;
	}

	public static boolean canMove(Player player, Board board)
	{
		boolean canMove = true;
		if (player.getCarrotsAvailable() == 0)
		{
			canMove = player.getActions().size() > 0;
			// TODO
		}
		else
		{
			// TODO
		}
		return canMove;
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
		}
		return valid;
	}
}
