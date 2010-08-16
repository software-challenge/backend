package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="sit:welcome")
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
