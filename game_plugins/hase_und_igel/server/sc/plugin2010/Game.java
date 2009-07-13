package sc.plugin2010;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
import sc.api.plugins.exceptions.RescueableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.api.plugins.host.PlayerScore;
import sc.api.plugins.host.ScoreCause;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.plugin2010.Board.FieldTyp;
import sc.plugin2010.Player.Action;
import sc.plugin2010.Player.FigureColor;

/**
 * Die Spiellogik von Hase- und Igel.
 * 
 * Die Spieler spielen in genau der Reihenfolge in der sie das Spiel betreten
 * haben.
 * 
 * @author rra
 * @since Jul 4, 2009
 * 
 */
public class Game extends RoundBasedGameInstance<Player>
{
	private static Logger	logger				= LoggerFactory
	.getLogger(Game.class);
	
	private List<FigureColor> availableColors = new LinkedList<FigureColor>();
	private Board board = Board.create();
	
	protected Board getBoard()
	{
		return board;
	}
	
	public Player getActivePlayer() 
	{
		return activePlayer;
	}
	
	public Game()
	{
		availableColors.addAll(Arrays.asList(FigureColor.values()));
	}
	
	@Override
	protected boolean checkGameOverCondition()
	{
		boolean gameOver = turn >= GamePlugin.MAX_TURN_COUNT;
		
		for(final Player p : this.players)
			gameOver = gameOver || p.getPosition() == 64; 

		return gameOver;
	}

	@Override
	protected Object getCurrentState()
	{
		return new GameState(this);
	}

	@Override
	protected void onRoundBasedAction(IPlayer fromPlayer, Object data)
			throws RescueableClientException
	{
		final Player author = (Player) fromPlayer;
		if (data instanceof Move)
		{
			final Move move = (Move) data;
			move.setTurn(getTurn());

			if (board.isValid(move, author))
			{
				update(move, author);
				author.addToHistory(move);
			}
			else
			{
				HashMap<IPlayer, PlayerScore> res = new HashMap<IPlayer, PlayerScore>();
				for (final Player p : players)
					res.put(p, p.getScore(ScoreCause.RULE_VIOLATION));
				
				notifyOnGameOver(res);
			}
			
			next();
		} else {
			logger.warn("Ungültiger Zug von '{}'", author.getColor());
		}
	}

	private void update(Move move, Player player)
	{
		switch (move.getTyp())
		{
			case TAKE_OR_DROP_CARROTS:
				player.changeCarrotsAvailableBy(move.getN());
				break;
			case EAT:
				player.eatSalad();
				if (board.isFirst(player))
				{
					player.changeCarrotsAvailableBy(10);
				}
				else
				{
					player.changeCarrotsAvailableBy(30);
				}
				break;
			case MOVE:
				player.setFieldNumber(player.getFieldNumber() + move.getN());
				player.changeCarrotsAvailableBy(-GameUtil.calculateCarrots(move
						.getN()));
				break;
			case FALL_BACK:
			{
				int nextField = board.getPreviousFieldByTyp(FieldTyp.HEDGEHOG,
						player.getFieldNumber());
				int diff = player.getFieldNumber() - nextField;
				player.changeCarrotsAvailableBy(diff * 10);
				player.setFieldNumber(nextField);
				break;
			}
			case PLAY_CARD:
			{
				final Action action = move.getCard();
				List<Action> remaining = player.getActions();
				remaining.remove(action);
				player.setActions(remaining);

				switch (action)
				{
					case EAT_SALAD:
						player.eatSalad();
						if (board.isFirst(player))
						{
							player.changeCarrotsAvailableBy(10);
						}
						else
						{
							player.changeCarrotsAvailableBy(30);
						}
						break;
					case FALL_BACK:
						if (board.isFirst(player))
							player.setFieldNumber(board.getOtherPlayer(player)
									.getFieldNumber() - 1);
						break;
					case HURRY_AHEAD:
						if (!board.isFirst(player))
							player.setFieldNumber(board.getOtherPlayer(player)
									.getFieldNumber() + 1);
						break;
					case TAKE_OR_DROP_CARROTS:
						player.changeCarrotsAvailableBy(move.getN());
						break;
				}
			}
			default:
				break;
		}
	}

	@Override
	public IPlayer onPlayerJoined() throws TooManyPlayersException
	{
		if (this.players.size() >= GamePlugin.MAX_PLAYER_COUNT)
			throw new TooManyPlayersException();
		
		final Player player = new Player(this.availableColors.remove(0));
		this.players.add(player);
		this.board.addPlayer(player);

		for (final IGameListener listener : this.listeners)
			listener.onPlayerJoined(player);

		return player;
	}

	@Override
	public void onPlayerLeft(IPlayer player)
	{
		HashMap<IPlayer, PlayerScore> res = new HashMap<IPlayer, PlayerScore>();
		for (final Player p : players)
			res.put(p, p.getScore(ScoreCause.LEFT));
		
		players.remove(player);
		notifyOnGameOver(res);
	}

	@Override
	public boolean ready()
	{
		return players.size() == GamePlugin.MAX_PLAYER_COUNT;
	}
	
	@Override
	public void start()
	{
		for (final Player p : players)
		{
			p.notifyListeners(new WelcomeMessage(p.getColor()));
		}

		super.start();
	}
}
