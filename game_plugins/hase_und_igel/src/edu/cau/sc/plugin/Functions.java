package edu.cau.sc.plugin;

/**
 * some useful functions
 * 
 * @author ffi
 * 
 */
public class Functions
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
		if (moveCount > 0)
		{
			return moveCount + calculateCarrots(moveCount - 1);
		}
		else
		{
			return 0;
		}
	}
}
