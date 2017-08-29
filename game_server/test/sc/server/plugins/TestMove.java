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
		state.state = this.value;
		state.turn ++;
		state.currentPlayer = state.currentPlayer==PlayerColor.RED?PlayerColor.BLUE:PlayerColor.RED;
	}
}
