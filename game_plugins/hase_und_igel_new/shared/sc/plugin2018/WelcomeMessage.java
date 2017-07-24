package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.shared.PlayerColor;

@XStreamAlias(value="welcomeMessage")
public class WelcomeMessage
{
	private PlayerColor color;
	
	public WelcomeMessage(PlayerColor color)
	{
		this.color = color;
	}
	
	public PlayerColor getPlayerColor()
	{
		return color;
	}
}
