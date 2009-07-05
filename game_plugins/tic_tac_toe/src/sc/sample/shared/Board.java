package sc.sample.shared;


public class Board
{
	protected Field[][]	fields	= new Field[3][];
	
    public Board()
    {
    	for(int i=0; i<fields.length; i++)
    	{
    		fields[i] = new Field[fields.length];
    		for(int j=0; j<fields.length; j++) {
    			fields[i][j] = new Field();
    		}
    	}
    }
	
	public boolean isGameOver()
	{
		return getWinner() != null;
	}

	private Player getWinner()
	{
		int[][] dirs = new int[][] { new int[] { 0, 0, 1, 0 },
				new int[] { 0, 1, 1, 0 }, new int[] { 0, 2, 1, 0 },
				new int[] { 0, 0, 0, 1 }, new int[] { 1, 0, 0, 1 },
				new int[] { 2, 0, 0, 1 }, new int[] { 0, 0, 1, 1 },
				new int[] { 0, 2, 1, -1 } };

		Player winner = null;

		for (int i = 0; i < dirs.length; i++)
		{
			int x = dirs[i][0];
			int y = dirs[i][1];
			int stepX = dirs[i][2];
			int stepY = dirs[i][3];

			Player firstOwner = getOwner(x, y);
			if (firstOwner == null)
			{
				continue;
			}

			if (isSamePlayer(firstOwner, x, y, stepX, stepY))
			{
				winner = firstOwner;
			}
		}

		return winner;
	}

	private boolean isSamePlayer(Player p, int x, int y, int stepX, int stepY)
	{
		if (p == null)
		{
			return false;
		}

		int newX = x + stepX;
		int newY = y + stepY;

		if (!validCoordinate(newX, newY))
		{
			return true;
		}

		return p.equals(getOwner(x, y))
				&& isSamePlayer(p, newX, newY, stepX, stepY);
	}

	protected Player getOwner(int x, int y)
	{
		return fields[y][x].getOwner();
	}

	private boolean validCoordinate(int x, int y)
	{
		return (x >= 0 && x < 3 && y >= 0 && y < 3);
	}
}
