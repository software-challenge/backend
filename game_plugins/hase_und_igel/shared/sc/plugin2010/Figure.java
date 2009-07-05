package sc.plugin2010;

/**
 * @author rra
 * @since Jul 1, 2009
 * 
 */
public class Figure
{
	/**
	 * Alle Spielfiguren aus dem Hase und Igel Original
	 */
	public enum FigureColor
	{
		RED, BLUE, YELLOW, WHITE, GREEN, ORANGE,
	}

	private int				position;
	private final FigureColor	color;

	protected Figure(FigureColor color)
	{
		this.position = 0;
		this.color = color;
	}

	/**
	 * Die aktuelle Position der Figure auf dem Spielfeld. Vor dem ersten Zug
	 * steht eine Figure immer auf Spielfeld 0
	 * 
	 * @return
	 */
	public final int getPosition()
	{
		return this.position;
	}
	
	public final FigureColor getColor()
	{
		return this.color;
	}
}
