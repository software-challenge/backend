package sc.server.plugins;

import sc.protocol.responses.ProtocolMove;
import sc.shared.PlayerColor;

public class TestMove extends ProtocolMove
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
