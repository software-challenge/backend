package sc.plugin2010.util;

import sc.plugin2010.Board;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
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

	/**
	 * Überprüft <code>MoveTyp.MOVE</code> Züge auf ihre Korrektheit
	 * 
	 * @param b
	 * @param l
	 * @param p
	 * @return
	 */
	public static boolean isValidToMove(Board b, Player p, int l)
	{
		boolean valid = true;
		int requiredCarrots = GameUtil.calculateCarrots(l);
		valid = valid && l > 0;
		valid = valid && (requiredCarrots <= p.getCarrotsAvailable());

		int newPosition = p.getPosition() + l;
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
				valid = valid && p.getActions().size() > 0;
				break;
			case GOAL:
				int carrotsLeft = p.getCarrotsAvailable() - requiredCarrots;
				valid = valid && carrotsLeft <= 10;
				valid = valid && p.getSaladsToEat() == 0;
				break;
		}
		return valid;
	}

	/**
	 * Überprüft <code>MoveTyp.EAT</code> Züge auf Korrektheit
	 * 
	 * @param b
	 * @param p
	 * @return
	 */
	public static boolean isValidToEat(Board b, Player p)
	{
		boolean valid = true;
		FieldTyp currentField = b.getTypeAt(p.getPosition());
		valid = valid && (currentField.equals(FieldTyp.SALAD));
		valid = valid && (p.getSaladsToEat() > 0);
		return valid;
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
				.getPosition());
		valid = valid && (newPosition != -1);
		valid = valid && !b.isOccupied(newPosition);
		return valid;
	}

	/**
	 * Überprüft <code>MoveTyp.PLAY_CARD</code>_X Züge auf Korrektheit
	 * 
	 * @param board
	 * @param player
	 * @param typ
	 * @param l
	 * @return
	 */
	public static boolean isValidToPlayCard(Board board, Player player,
			MoveTyp typ, int l)
	{
		Boolean valid = true;
		switch (typ)
		{
			case PLAY_CARD_CHANGE_CARROTS:
				valid = valid && player.ownsCardOfTyp(Action.TAKE_OR_DROP_CARROTS);
				valid = valid && (l == 0 || l == 20 || l == -20);
				break;
			case PLAY_CARD_EAT_SALAD:
				valid = valid && player.ownsCardOfTyp(Action.EAT_SALAD);
				valid = valid && player.getSaladsToEat() > 0;
				break;
			case PLAY_CARD_FALL_BACK:
			{
				valid = valid && player.ownsCardOfTyp(Action.FALL_BACK);
				valid = valid && board.isFirst(player);
				final Player o = board.getOtherPlayer(player);
				valid = valid && o.getPosition() != 0;
				int previousHedgehog = board.getPreviousFieldByTyp(
						FieldTyp.HEDGEHOG, o.getPosition());
				valid = valid && ((o.getPosition() - previousHedgehog) != 1);
				break;
			}
			case PLAY_CARD_HURRY_AHEAD:
			{
				valid = valid && player.ownsCardOfTyp(Action.HURRY_AHEAD);
				valid = valid && !board.isFirst(player);
				final Player o = board.getOtherPlayer(player);
				valid = valid && o.getPosition() != 64;
				int nextHedgehog = board.getNextFieldByTyp(FieldTyp.HEDGEHOG, o
						.getPosition());
				valid = valid && ((nextHedgehog - o.getPosition()) != 1);

				if (o.getPosition() == 63)
					valid = valid && board.canEnterGoal(player);
				break;
			}
			default:
				valid = false;
				break;
		}
		return valid;
	}
}
