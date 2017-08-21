package sc.server.plugins;

import sc.shared.PlayerColor;

public class TestMove
{
	public int value;
	
	public TestMove(int i)
	{
		this.value = i;
	}

	public void perform(TestGameState state){
		int newSecret = this.value;

		if (state.currentPlayer == PlayerColor.BLUE)
		{
			state.secret0 = newSecret;
		}
		else
		{
			state.secret1 = newSecret;
		}
		state.turn ++;
		state.currentPlayer = state.currentPlayer==PlayerColor.RED?PlayerColor.BLUE:PlayerColor.RED;
	}
}
