package sc.server.plugins;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.framework.plugins.IPerspectiveAware;
import sc.shared.PlayerColor;

public class TestGameState implements IPerspectiveAware
{
	@XStreamOmitField
	private TestGame	controller;

	public Integer		round	= 0;
	public int			secret0	= 0;
	public int			secret1	= 0;
	public int lastPlayerIndex = 0;
	public int turn;
	public PlayerColor currentPlayer;

	public PlayerColor startPlayer;


	public TestPlayer red;
	public TestPlayer blue;

	public void setController(TestGame controller)
	{
		this.controller = controller;
	}


	public TestGameState(){
		this.turn = 0;
		this.currentPlayer = PlayerColor.RED;
		this.startPlayer = PlayerColor.RED;
		this.red = new TestPlayer(PlayerColor.RED);
		this.blue = new TestPlayer(PlayerColor.BLUE);
	}

	@Override
	public boolean isVisibleFor(Object viewer, String field)
	{
		if (field.equals("secret0"))
		{
			return viewer == this.controller.getPlayers().get(0);
		}
		else if (field.equals("secret1"))
		{
			return viewer == this.controller.getPlayers().get(1);
		}

		return true;
	}
}
