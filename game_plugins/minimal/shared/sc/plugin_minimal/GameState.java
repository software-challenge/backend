package sc.plugin_minimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="hui:gameState")
public class GameState
{
	// FIXME: shouldn't send "Game" over the network, since it is Part
	// of the SERVER-src folder.
	private Game	game;

	protected GameState(final Game game)
	{
		this.game = game;
	}

	public Game getGame()
	{
		return game;
	}
}
