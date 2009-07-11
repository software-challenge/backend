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
	
	public GameOver(boolean isFirst)
	{
		this.isFirst = isFirst;
	}
	
	public boolean isFirst()
	{
		return isFirst;
	}
}
