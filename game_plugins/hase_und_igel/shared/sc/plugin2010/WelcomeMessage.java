package sc.plugin2010;

import sc.plugin2010.Player.FigureColor;

public class WelcomeMessage
{
	private FigureColor myColor;
	
	public WelcomeMessage(FigureColor c)
	{
		myColor = c;
	}
	
	public FigureColor getMyColor()
	{
		return myColor;
	}
}
