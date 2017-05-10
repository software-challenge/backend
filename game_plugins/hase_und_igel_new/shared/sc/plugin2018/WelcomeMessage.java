package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="welcomeMessage")
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
