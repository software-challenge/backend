package sc.player2015.logic;

import java.util.List;

import sc.player2015.Starter;
import sc.plugin2015.NullMove;
import sc.plugin2015.RunMove;
import sc.plugin2015.GameState;
import sc.plugin2015.IGameHandler;
import sc.plugin2015.Move;
import sc.plugin2015.MoveType;
import sc.plugin2015.Player;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.SetMove;
import sc.shared.GameResult;

public class NotSoSimpleLogic implements IGameHandler {

	private Starter client;
	private GameState gameState;
	private Player currentPlayer;

	public NotSoSimpleLogic(Starter client) {
		this.client = client;
	}

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
		
	}

	@Override
	public void onRequestAction() {
		if (gameState.getCurrentMoveType() == MoveType.SET) {
			List<SetMove> possibleMoves = gameState.getPossibleSetMoves();
			SetMove selection = setMoveWithMostPossibleMoves(possibleMoves);
			sendAction(selection);
		} else {
			List<Move> possibleMoves = gameState.getPossibleMoves();
			Move selection = runMoveWithMostPossibleMoves(possibleMoves);
			sendAction(selection);
		}
	}

	private Move runMoveWithMostPossibleMoves(List<Move> possibleMoves) {
		int maxPoints = -10000;
		Move bestMove = new NullMove();
		GameState nextGameState;
		for(int i = 0; i < possibleMoves.size() - 1; i++) {
			try {
				nextGameState = (GameState) gameState.clone();
				//nextGameState.prepareNextTurn(possibleMoves.get(i));
				if(possibleMoves.get(i) instanceof RunMove) {
					RunMove currentMove = (RunMove) possibleMoves.get(i);
					nextGameState.getBoard().movePenguin(currentMove.fromX, currentMove.fromY, currentMove.toX, currentMove.toY, currentPlayer.getPlayerColor());
				} else {
					
				}
				int possibleMovesNextTurn = nextGameState.getPossibleMoves(currentPlayer.getPlayerColor()).size();
				int possibleMovesNextTurnOtherPlayer = nextGameState.getPossibleMoves(currentPlayer.getPlayerColor().opponent()).size();
				if(possibleMoves.get(i) instanceof RunMove) {
					if(possibleMovesNextTurn - possibleMovesNextTurnOtherPlayer + 2 * gameState.getBoard().getFishNumber(((RunMove) possibleMoves.get(i)).toX, ((RunMove) possibleMoves.get(i)).toY) > maxPoints) {
						bestMove = possibleMoves.get(i);
						maxPoints = possibleMovesNextTurn - possibleMovesNextTurnOtherPlayer + 2 * gameState.getBoard().getFishNumber(((RunMove) possibleMoves.get(i)).toX, ((RunMove) possibleMoves.get(i)).toY);
					}
				} else {
					if(-10000 >= maxPoints) {
						bestMove = possibleMoves.get(i);
						maxPoints = 0;
					}
				}
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bestMove;
	}

	private SetMove setMoveWithMostPossibleMoves(List<SetMove> possibleMoves) {
		float maxPoints = -1;
		SetMove bestMove = gameState.getPossibleSetMoves().get(0);
		GameState nextGameState;
		for(int i = 0; i < possibleMoves.size(); i++) {
			try {
				nextGameState = (GameState) gameState.clone();
				try {
					possibleMoves.get(i).perform(nextGameState, currentPlayer);
				} catch (Exception e) {
					
				}
				//nextGameState.prepareNextTurn(possibleMoves.get(i));
				int possibleMovesNextTurn = nextGameState.getPossibleMoves(currentPlayer.getPlayerColor()).size();
				int possibleMovesNextTurnOtherPlayer = nextGameState.getPossibleMoves(currentPlayer.getPlayerColor().opponent()).size();
				if(possibleMovesNextTurn - possibleMovesNextTurnOtherPlayer > maxPoints) {
					bestMove = possibleMoves.get(i);
					maxPoints = possibleMovesNextTurn - possibleMovesNextTurnOtherPlayer;
				}
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bestMove;
	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;

	}

	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();
	}

	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}

}
