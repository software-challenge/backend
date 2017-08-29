package sc.server.plugins;

import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.framework.plugins.ActionTimeout;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.framework.plugins.SimplePlayer;
import sc.shared.*;

public class TestGame extends RoundBasedGameInstance<TestPlayer>
{
	private TestGameState	state	= new TestGameState();

	public TestGame()
  {
	}

	@Override
	protected void onRoundBasedAction(SimplePlayer fromPlayer, Object data)
			throws GameLogicException
	{
		if (data instanceof TestMove)
		{

    /*
     * NOTE: Checking if right player sent move was already done by
     * {@link sc.framework.plugins.RoundBasedGameInstance#onAction(SimplePlayer, Object)}.
     * There is no need to do it here again.
     */


        final TestMove move = (TestMove) data;
        move.perform(this.state);
        next(this.state.currentPlayer == PlayerColor.RED ? state.red : state.blue);
		}
	}

  @Override
  protected WinCondition checkWinCondition() {
	  if (this.getRound() > 1) {
	    System.out.println("Someone won");
	    return new WinCondition(
	            ((TestGameState)this.getCurrentState()).state % 2 == 0 ? PlayerColor.RED : PlayerColor.BLUE, "Round limit reached");
    }
    return null;
  }

  @Override
	public SimplePlayer onPlayerJoined() throws TooManyPlayersException
	{
		if (this.players.size() < 2)
		{
			if (players.size() == 0){
			  state.red = new TestPlayer(PlayerColor.RED);
			  players.add(state.red);
			  return state.red;
      } else if (players.size() == 1){
			  state.blue = new TestPlayer(PlayerColor.BLUE);
			  players.add(state.blue);
			  return state.blue;
      }
		}

		throw new TooManyPlayersException();

	}

	public List<TestPlayer> getPlayers()
	{
		return this.players;
	}

	@Override
	protected Object getCurrentState()
	{
		return this.state;
	}
	
	@Override
	public void onPlayerLeft(SimplePlayer player, ScoreCause cause) {
		// this.players.remove(player);
		LoggerFactory.getLogger(this.getClass())
				.debug("Player left {}", player);
		Map<SimplePlayer, PlayerScore> result = generateScoreMap();
		result.put(player, new PlayerScore(false, "Spieler hat das Spiel verlassen."));
		result.get(player).setCause(cause);
		notifyOnGameOver(result);
	}

	@Override
	public void onPlayerLeft(SimplePlayer player)
	{
		onPlayerLeft(player, ScoreCause.LEFT);
	}

	@Override
	protected PlayerScore getScoreFor(TestPlayer p)
	{
		return new PlayerScore(true, "Spieler hat gewonnen.");
	}

	@Override
	public void loadFromFile(String file)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadGameInfo(Object gameInfo)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SimplePlayer> getWinners()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sends welcomeMessage to all listeners and notify player on new gameStates or MoveRequests
	 */
	@Override
	public void start() {
		for (final TestPlayer p : this.players) {
			p.notifyListeners(new WelcomeMessage(p.color));
		}

		super.start();
	}

	// XXX set to right value
	@Override
	protected ActionTimeout getTimeoutFor(TestPlayer player)
	{
		return new ActionTimeout(false, 100000000L, 20000000L);
	}
}
