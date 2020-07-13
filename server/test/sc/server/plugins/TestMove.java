package sc.server.plugins;

import sc.api.plugins.IMove;
import sc.server.helpers.TestTeam;

public class TestMove implements IMove {

  public int value;

  public TestMove(int i) {
    this.value = i;
  }

  public void perform(TestGameState state) {
    state.setState(this.value);
    state.setTurn(state.getTurn() + 1);
    state.setCurrentPlayer(state.getCurrentPlayer() == TestTeam.RED ? TestTeam.BLUE : TestTeam.RED);
  }

}
