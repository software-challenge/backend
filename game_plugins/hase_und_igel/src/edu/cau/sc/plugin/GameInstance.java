package edu.cau.sc.plugin;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameListener;
import sc.api.plugins.IPlayer;


public class GameInstance implements IGameInstance
{
	// the actual gameboard
	private Board				gameBoard;

	// the server expects an answer from playerID (-1 if no answer expected)
	// TODO make more secure by only answer onto the question that was asked
	private int					wantAnswerFromID	= -1;

	// the players taking part in the game
	private LinkedList<Player>	players				= new LinkedList<Player>();

	private int					currentPlayerIndex	= 0;

	private boolean				gameHasStarted		= false;

	private Set<IGameListener>	gameListeners		= new HashSet<IGameListener>();

	private int					neededPlayers;

	public GameInstance(int neededPlayers)
	{
		this.neededPlayers = neededPlayers;
		initialize();
	}

	private void initialize()
	{
		gameBoard = new Board();
	}

	private void nextPlayer()
	{
		if (currentPlayerIndex < (players.size() - 1))
		{
			currentPlayerIndex++;
		}
		else
		{
			currentPlayerIndex = 0;
		}

		newRound();
	}

	private void newRound()
	{
		Player curPlayer = players.get(currentPlayerIndex);

		// TODO ask the player what he wants to do

		if (curPlayer.isPause())
		{
			curPlayer.setPause(false);
		}

		nextPlayer();
	}

	@Override
	public void actionReceived(IPlayer fromPlayer, Serializable data)
	{
		Player sender = (Player) fromPlayer;

		if (data.toString().equalsIgnoreCase("join"))
		{
			players.add(sender);
		}
		else if (data.toString().equalsIgnoreCase("start"))
		{
			if (neededPlayers == players.size())
			{
				gameHasStarted = true;
				newRound();
			}
			else
			{
				// TODO Error Message Not Enough Players
			}
		}
		else if (data.toString().equalsIgnoreCase("move"))
		{
			// TODO extract moveCount from String
			int moveCount = 6;
			int newFieldIndex = sender.getFieldPosition() + moveCount;
			if ((Functions.calculateCarrots(moveCount) <= sender.getCarrots())
					&& (!sender.isPause())
					&& (!gameBoard.getField(newFieldIndex).isOccupied()))
			{
				// enough carrots to do the move

				// dec carrots
				sender.setCarrots(sender.getCarrots()
						- Functions.calculateCarrots(moveCount));

				// free the old field
				gameBoard.setFieldTaken(sender.getFieldPosition(), false);

				// set player to new field
				sender.setFieldPosition(newFieldIndex);

				// take the new field
				gameBoard.setFieldTaken(sender.getFieldPosition(), true);
			}
		}
		else if ((data.toString().equalsIgnoreCase("eatSalad"))
				&& (sender.getPlayerno() == wantAnswerFromID))
		{
			if (sender.getSalads() > 0)
			{
				sender.setSalads(sender.getSalads() - 1);
				sender.setPause(true);
			}
			else
			{
				// TODO Error Message not enough salads to eat
			}

		}

	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public IPlayer playerJoined()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playerLeft(IPlayer player)
	{
		// TODO Auto-generated method stub

	}

	public boolean isGameHasStarted()
	{
		return gameHasStarted;
	}

	@Override
	public void addGameListener(IGameListener listener)
	{
		this.gameListeners.add(listener);
	}

	@Override
	public void removeGameListener(IGameListener listener)
	{
		this.gameListeners.remove(listener);
	}
}
