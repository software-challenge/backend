package sc.plugin_minimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="minimal:welcome")
public class WelcomeMessage
{
	private FigureColor myColor;
	
	public WelcomeMessage(FigureColor c)
	{
		myColor = c;
	}
	
	public FigureColor getYourColor()
	{
		return myColor;
	}
}
