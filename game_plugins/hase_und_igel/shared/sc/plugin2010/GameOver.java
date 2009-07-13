package sc.plugin2010;

/**
 * Das Spiel ist vorbei. 
 * 
 * @author rra
 * @since Jul 5, 2009
 *
 */
public class GameOver
{
	// Platzierung des Spielers im Rennen
	private boolean isFirst;
	private int position;
	
	public GameOver(boolean isFirst, int position)
	{
		this.isFirst = isFirst;
		this.position = position;
	}
	
	public boolean isFirst()
	{
		return isFirst;
	}
	
	public int getPosition()
	{
		return position;
	}
}
