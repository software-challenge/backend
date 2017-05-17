package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.shared.PlayerColor;

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
