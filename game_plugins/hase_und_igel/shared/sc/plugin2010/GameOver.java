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
	private int position;
	
	public GameOver( int position)
	{
		this.position = position;
	}
	
	public int getPosition()
	{
		return position;
	}
}
