package sc.plugin_minimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="minimal:welcome")
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
