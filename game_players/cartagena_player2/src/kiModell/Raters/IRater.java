package kiModell.Raters;

import sc.plugin2013.GameState;
import sc.plugin2013.MoveContainer;

public interface IRater {
	int getRating(GameState gameStateBefore,GameState gameStateAfter, MoveContainer move);
}
