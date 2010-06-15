package sc.plugin2011;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="fdh:welcome")
public class WelcomeMessage
{
	private PlayerColor myColor;
	
	public WelcomeMessage(PlayerColor c)
	{
		myColor = c;
	}
	
	public PlayerColor getYourColor()
	{
		return myColor;
	}
}
