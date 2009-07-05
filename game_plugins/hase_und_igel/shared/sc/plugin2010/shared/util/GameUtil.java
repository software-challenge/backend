package sc.plugin2010.shared.util;

public class GameUtil
{
	/**
	 * returns the count of carrots needed to do the move by
	 * <code>moveCount</code> fields
	 * 
	 * @param moveCount
	 *            how many fields to move (must be positive)
	 * @return count of carrots needed
	 */
	public static int calculateCarrots(int moveCount)
	{
		int res = 0;

		for (int i = 1; i <= moveCount; i++)
		{
			res += i;
		}

		return res;
	}

	/**
	 * returns how many moves can be made with <code>carrots</code>
	 * 
	 * @param carrots
	 *            the carrots you want to spend
	 * @return moves that you can do maximal
	 */

	public static int calculateMoveableFields(int carrots)
	{
		int moves = 0;

		while (calculateCarrots(++moves) <= carrots);

		return moves-1;
	}
}
