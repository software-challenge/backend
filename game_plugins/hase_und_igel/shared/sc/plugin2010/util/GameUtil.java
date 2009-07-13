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
	public static boolean isValidToMove(Board b, Player p, int l, boolean checkCarrots)
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
				valid = valid && p.getActions().size() > 0;
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
			Action typ, int l)
	{
		Boolean valid = board.getTypeAt(player.getFieldNumber()).equals(FieldTyp.RABBIT);
		valid = valid && !playerMustMove(player, MoveTyp.PLAY_CARD) && !playerMustMove(player, MoveTyp.EAT);
		switch (typ)
		{
			case TAKE_OR_DROP_CARROTS:
				valid = valid
						&& player.ownsCardOfTyp(Action.TAKE_OR_DROP_CARROTS);
				valid = valid && (l == 0 || l == 20 || l == -20);
				break;
			case EAT_SALAD:
				valid = valid && player.ownsCardOfTyp(Action.EAT_SALAD);
				valid = valid && player.getSaladsToEat() > 0;
				break;
			case FALL_BACK:
			{
				valid = valid && player.ownsCardOfTyp(Action.FALL_BACK);
				valid = valid && board.isFirst(player);
				final Player o = board.getOtherPlayer(player);
				valid = valid && o.getFieldNumber() != 0;
				int nextPos = o.getFieldNumber() - 1;
				valid = valid && isValidToMove(board, player, player.getFieldNumber()-nextPos, false);
				int previousHedgehog = board.getPreviousFieldByTyp(
						FieldTyp.HEDGEHOG, o.getFieldNumber());
				valid = valid && ((o.getFieldNumber() - previousHedgehog) != 1);
				break;
			}
			case HURRY_AHEAD:
			{
				valid = valid && player.ownsCardOfTyp(Action.HURRY_AHEAD);
				valid = valid && !board.isFirst(player);
				final Player o = board.getOtherPlayer(player);
				valid = valid && o.getFieldNumber() != 64;
				int nextPos = o.getFieldNumber() - 1;
				valid = valid && isValidToMove(board, player, nextPos-player.getFieldNumber(), false);
				int nextHedgehog = board.getNextFieldByTyp(FieldTyp.HEDGEHOG, o
						.getFieldNumber());
				valid = valid && ((nextHedgehog - o.getFieldNumber()) != 1);

				if (o.getFieldNumber() == 63)
					valid = valid && board.canEnterGoal(player);
				break;
			}
			default:
				valid = false;
				break;
		}
		return valid;
	}

	public static boolean canMove(Player player, Board board)
	{
		boolean canMove = true;
		if (player.getCarrotsAvailable() == 0)
		{
			canMove = player.getActions().size() > 0;
			// TODO sauber implementieren!
		}
		else
		{
			// TODO true!
		}
		return canMove;
	}
}
