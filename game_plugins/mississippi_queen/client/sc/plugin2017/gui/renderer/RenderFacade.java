package sc.plugin2017.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Panel;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.plugin2017.EPlayerId;
import sc.plugin2017.GameState;
import sc.plugin2017.IGameHandler;
import sc.plugin2017.Move;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.WinCondition;
import sc.shared.GameResult;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

/**
 *
 * @author tkra, ffi, sca
 *
 */
public class RenderFacade {
	private static final Logger logger = LoggerFactory
			.getLogger(RenderFacade.class);
	private FrameRenderer frameRenderer;

	private EPlayerId activePlayer;

	/*
	 * Used here to determine what panel (IRenderer) should be used when a new
	 * panel is created. This solution only works with two players
	 */
	private boolean alreadyCreatedPlayerOne = false;

	private IGameHandler handler1;

	private IGameHandler handler2;

	private IGameHandler currentHandler;

	/**
	 * Singleton instance
	 */
	private static volatile RenderFacade instance;

	private Thread thread;
	private boolean receiverThreadRunning = false;
	private final List<GameState> gameStateQueue;
	private Panel panel;
	private final Runnable gameStateReciever = new Runnable() {

		@Override
		public void run() {
			while (receiverThreadRunning) {

				try {

					synchronized (gameStateQueue) {
						// Make sure we don't end up in a deadlock
						if (gameStateQueue.isEmpty()) {
							gameStateQueue.wait();
						}

						while (!gameStateQueue.isEmpty()) {
						  logger.debug("new gamestate");
							GameState gameState = gameStateQueue.remove(0);
							frameRenderer.updateGameState(gameState);
						}
					}

				} catch (InterruptedException e) {
				  logger.debug("Receiver thread was interrupted.");
				  receiverThreadRunning = false;
				} catch (Exception e) {
					logger.error("Exception in render thread", e);
				}
			}
		}
	};
	private GameState lastGameState;

	private boolean first;

	private boolean disabled;

	private int maxTurn;

	private RenderFacade() {
		gameStateQueue = new LinkedList<>();
		startReceiverThread();
	}

	public void startReceiverThread() {
		if (thread != null && !thread.isAlive()) {
			thread = null;
		}
		if (thread == null) {
			thread = new Thread(gameStateReciever);
			thread.setName("GameStateReciever");
			receiverThreadRunning = true;
			thread.start();
		}
	}

	public void stopReceiverThread() {
		if (thread != null) {
			thread.interrupt();
			receiverThreadRunning = false;
			thread = null;
		}
	}

	public static RenderFacade getInstance() {
		if (null == instance) {
			synchronized (RenderFacade.class) {
				if (null == instance) {
					instance = new RenderFacade();
				}
			}
		}
		return instance;
	}

	public enum GUIMode {
		CONNECT, OBSERVER, PLAYER_ONE, PLAYER_TWO;
	}

	public void setRenderContext(final Panel panel) {

		synchronized (gameStateQueue) {
			gameStateQueue.clear();
		}

		maxTurn = 0;
		first = true;
		disabled = false;
		activePlayer = null;
		alreadyCreatedPlayerOne = false;
		this.panel = panel;

		if (panel != null) {
		  // panel == null means that no display should be done (testrange)
      initRenderer();
		}
  }

	private void initRenderer() {
	  logger.debug("initializing rendere for game");
    panel.setLayout(new BorderLayout());
		panel.setVisible(true);
		panel.removeAll(); //
    frameRenderer = new FrameRenderer();
    frameRenderer.init();
    panel.add(frameRenderer, BorderLayout.CENTER);
    panel.revalidate();
    panel.repaint();
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Sets handler
	 * @param handler Game handler
	 * @param target EPlayerId
	 *
	 */
	public void setHandler(IGameHandler handler, EPlayerId target) {

		if (target == EPlayerId.OBSERVER) {

		} else if (target == EPlayerId.PLAYER_ONE) {
			handler1 = handler;
			setAlreadyCreatedPlayerOne(true);
		} else if (target == EPlayerId.PLAYER_TWO) {
			handler2 = handler;
		}
	}

	public void sendMove(Move move) {
    if (currentHandler != null) {
      currentHandler.sendAction(move);
    }
  }

	// TODO
	public void updatePlayer(final Player myplayer, final Player otherPlayer, final EPlayerId target) {

	}

	public void updateGameState(final GameState gameState) {
		updateGameState(gameState, false);
	}

	private void updateGameState(final GameState gameState, boolean force) {
		if (disabled) {
			return;
		}

		synchronized (gameStateQueue) {
			if (first || gameState.getTurn() != lastGameState.getTurn() || force) {
				gameStateQueue.add(gameState);
				startReceiverThread();
				first = false;
				maxTurn = Math.max(maxTurn, gameState.getTurn());
				lastGameState = gameState;
				gameStateQueue.notifyAll();
			}
		}
	}

	// TODO
	public void updateChat(final String chatMsg, final EPlayerId target) {

	}

	public void switchToPlayer(EPlayerId id) {

		if (id == null) {
			currentHandler = null;
			return;
		}

		switch (id) {
		case OBSERVER:
			currentHandler = null;
			break;
		case PLAYER_ONE:
			currentHandler = handler1;
			break;
		case PLAYER_TWO:
			currentHandler = handler2;
			break;
		}
		activePlayer = id;
	}

	public void setAlreadyCreatedPlayerOne(boolean alreadyCreatedPlayerOne) {
		this.alreadyCreatedPlayerOne = alreadyCreatedPlayerOne;
	}

	public boolean getAlreadyCreatedPlayerOne() {
		return alreadyCreatedPlayerOne;
	}

	/**
	 * @param id EPlayerId
	 */
	public synchronized void requestMove(EPlayerId id) {
    logger.debug("request move with {} for player {}", this.maxTurn, id);
		if (id == EPlayerId.PLAYER_ONE || id == EPlayerId.PLAYER_TWO) {
			switchToPlayer(id);
			// FIXME: remove this:
			while(gameStateQueue.size() > 0) {
				try {
					Thread.sleep(20);
				} catch (Exception e) {
					System.err.println(e);
				}
			}
			frameRenderer.requestMove(maxTurn, id);
		}
	}

	public void gameError(String errorMessage) {
	  // gamestate should be set to an game end state
	  // TODO better make this explicit
	  logger.debug("gameError called: {}", errorMessage);
	}

	/**
	 * @param data result of game
	 * @param target EPlayerId
	 * @param color Playercolor
	 * @param errorMessage error message
	 */
	public void gameEnded(GameResult data, EPlayerId target, PlayerColor color, String errorMessage) {

	  logger.debug("gameEnded called: {}", errorMessage);
		stopReceiverThread();

		if (disabled) {
			return;
		}
		if (data != null) {
		  PlayerScore score = data.getScores().get(color == PlayerColor.RED ? 0 : 1);
			ScoreCause cause = score.getCause();
			String err = score.getReason();

			if (errorMessage == null && cause != ScoreCause.REGULAR) {

				err = "'" + lastGameState.getPlayerNames()[color == PlayerColor.RED ? 0 : 1]
						+ "' hat keinen Zug gesendet.\n";

				switch (cause) {

				case SOFT_TIMEOUT:
				case HARD_TIMEOUT:
					err += "Die maximale Zugzeit von 2 Sekunden wurde überschritten.";
					break;

				case LEFT:
					err += "Der Spieler hat das Spiel verlassen.";
					break;

				case UNKNOWN:
					err += "Es ist ein unbekannter Fehler aufgetreten.";
					break;
				}

			}

			PlayerColor winner = null;
			if (!data.getWinners().isEmpty()) {
			  assert data.getWinners().size() == 1;
			  winner = ((Player)data.getWinners().get(0)).getPlayerColor();
			}
			logger.debug("gameEnded with result data. winner {}", winner);
			frameRenderer.endGame(new WinCondition(winner, err));
		} else if (errorMessage != null) {
			logger.debug("gameEnded no result. winner, message is {}", errorMessage);
			frameRenderer.endGame(new WinCondition(null, errorMessage));
		}

	}

	public EPlayerId getActivePlayer() {
		return activePlayer;
	}

	public Image getImage() {
		return frameRenderer.getImage();
	}
}
