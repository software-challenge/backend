package sc.plugin2011;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

public class Game extends RoundBasedGameInstance<Player> {

	@Override
	protected boolean checkGameOverCondition() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Object getCurrentState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PlayerScore getScoreFor(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onNewTurn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws GameLogicException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IPlayer onPlayerJoined() throws TooManyPlayersException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPlayerLeft(IPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerLeft(IPlayer player, ScoreCause cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean ready() {
		// TODO Auto-generated method stub
		return false;
	}

}
