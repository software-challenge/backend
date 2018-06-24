package root;

import sc.plugin2019.GameState;

public interface IStateUpdate {
  public void onStateChanged(GameState state);
}
