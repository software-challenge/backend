package sc.plugin2010;

import java.io.IOException;

public class GuiClient extends AbstractClient
{
	public GuiClient(String host, int port, EPlayerId id) throws IOException
	{
		super(host, port, id);
	}

	private IGUIObservation	obs;

	public void setObservation(IGUIObservation obs)
	{
		this.obs = obs;
		// this.client.start();
	}

	public IGUIObservation getObservation()
	{
		return obs;
	}

	private void sendLastTurn(Player oldPlayer, int playerId)
	{
		Move move = oldPlayer.getLastMove();

		if (move.getTyp() == MoveTyp.PLAY_CARD)
		{
			Move move2 = oldPlayer.getLastMove(-2);
			if (move2.getTyp() == MoveTyp.PLAY_CARD)
			{
				Move move3 = oldPlayer.getLastMove(-3);
				obs.newTurn(playerId, GameUtil.displayMoveAction(move3));
			}

			obs.newTurn(playerId, GameUtil.displayMoveAction(move2));
		}
		obs.newTurn(playerId, GameUtil.displayMoveAction(move));
	}

	@Override
	public void onNewState(String roomId, Object state)
	{
		super.onNewState(roomId, state);

		GameState gameState = (GameState) state;
		Game game = gameState.getGame();

		if (obs != null)
		{
			Player oldPlayer = game.getBoard().getOtherPlayer(
					game.getActivePlayer());

			int playerid = 0;
			if (oldPlayer.getColor() == FigureColor.RED)
			{
				playerid = 0;
			}
			else
			{
				playerid = 1;
			}
			;

			if (oldPlayer.getLastMove() != null)
			{
				sendLastTurn(oldPlayer, playerid);
			}

			if (!alreadyReady)
			{
				alreadyReady = true;
				obs.ready();
			}
		}
	}

	@Override
	public void onGameJoined(String roomId)
	{
		if (obs != null)
		{
			obs.ready();
		}
	}

	@Override
	public void onGameLeft(String roomId)
	{
		// nothing to do
	}
}
