package sc.plugin2015;

import sc.plugin2015.util.InvalideMoveException;

public class NullMove extends RunMove{
	
	public NullMove() {
		
	}

	@Override
	void perform(GameState state, Player player) throws InvalideMoveException {
		//Ein NullMove macht eben nichts.
	}

	@Override
	public MoveType getMoveType() {
		return MoveType.RUN;
	}

}
