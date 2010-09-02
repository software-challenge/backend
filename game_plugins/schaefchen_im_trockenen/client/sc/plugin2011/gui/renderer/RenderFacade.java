package sc.plugin2011.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Image;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import sc.plugin2011.EPlayerId;
import sc.plugin2011.GameState;
import sc.plugin2011.IGameHandler;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.shared.GameResult;

/**
 * 
 * @author tkra, ffi, sca
 * 
 */
public class RenderFacade {
	private FrameRenderer frameRenderer; // The observer gets an extra panel
	// (shown while
	// a AI client acts)

	// private IRenderer player1; // One panel for player1 (shown at player
	// one's
	// turn)
	// private IRenderer player2; // One panel for player2 (shown at player
	// two's
	// turn)

	private EPlayerId activePlayer;

	/*
	 * GUI modes are used to determine what the gui is showing at the moment Can
	 * be connection dialogue, player1, ...
	 */

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
	private final List<GameState> gameStateQueue;
	private final Runnable gameStateReciever = new Runnable() {

		@Override
		public void run() {

			while (true) {

				synchronized (gameStateQueue) {

					try {
						gameStateQueue.wait();

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

				while (gameStateQueue.size() > 0) {

					try {

						GameState gameState = gameStateQueue.remove(0);
						frameRenderer.updateGameState(gameState);
					} catch (Exception e) {
						System.err.println(e.getStackTrace());
					}
				}

			}

		}
	};
	private GameState lastGameState;

	private boolean first;

	private boolean disabled;

	private int maxTurn;

	private RenderFacade() {

		frameRenderer = new FrameRenderer();
		gameStateQueue = new LinkedList<GameState>();
		thread = new Thread(gameStateReciever);
		thread.setName("GameStateReciever");
		thread.start();

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

	public void setRenderContext(final JPanel panel,
			final boolean threeDimensional) {

		synchronized (gameStateQueue) {
			gameStateQueue.clear();
		}

		if (!thread.isAlive()) {
			thread = new Thread(gameStateReciever);
			thread.setName("GameStateReciever");
			thread.start();
		}

		maxTurn = 0;
		first = true;
		disabled = false;
		activePlayer = null;
		alreadyCreatedPlayerOne = false;

		if (panel != null) {
			panel.setDoubleBuffered(true);
			panel.setLayout(new BorderLayout());
			panel.add(frameRenderer, BorderLayout.CENTER);

		}
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void sendMove(Move move) {
		if (currentHandler != null) {
			currentHandler.sendAction(move);
		}
	}

	/**
	 * creates a new panel
	 * 
	 * @param asPlayer1
	 *            if asPlayer1 is true than panel will be created for player 1
	 *            else it is created for player2
	 */
	// TODO
	public void setHandler(IGameHandler handler, EPlayerId target) {

		if (target == EPlayerId.OBSERVER) {

		} else if (target == EPlayerId.PLAYER_ONE) {
			handler1 = handler;
			setAlreadyCreatedPlayerOne(true);
		} else if (target == EPlayerId.PLAYER_TWO) {
			handler2 = handler;
		}

		frameRenderer.repaint();

	}

	// TODO
	public void updatePlayer(final Player myplayer, final Player otherPlayer,
			final EPlayerId target) {

	}

	public void updateGameState(final GameState gameState,
			final EPlayerId target) {

		if (disabled) {
			return;
		}

		synchronized (gameStateQueue) {

			if (first || gameState.getTurn() != lastGameState.getTurn()) {

				first = false;
				maxTurn = Math.max(maxTurn, gameState.getTurn());
				gameStateQueue.add(gameState);
				lastGameState = gameState;
				gameStateQueue.notifyAll();
			}
		}
	}

	// TODO
	public void updateChat(final String chatMsg, final EPlayerId target) {

	}

	/**
	 * 
	 */
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
		frameRenderer.repaint();

	}

	public void setAlreadyCreatedPlayerOne(boolean alreadyCreatedPlayerOne) {
		this.alreadyCreatedPlayerOne = alreadyCreatedPlayerOne;
	}

	public boolean getAlreadyCreatedPlayerOne() {
		return alreadyCreatedPlayerOne;
	}

	/**
	 * @param id
	 */
	public synchronized void requestMove(EPlayerId id) {

		if (id == EPlayerId.PLAYER_ONE || id == EPlayerId.PLAYER_TWO) {
			setActivePlayer(id);
			frameRenderer.requestMove(maxTurn);
		}
	}

	public void gameError(String errorMessage) {

	}

	/**
	 * @param data
	 * @param id
	 */
	public void gameEnded(GameResult data, EPlayerId target, PlayerColor color,
			String errorMessage) {

	}

	private void setActivePlayer(EPlayerId activePlayer) {
		this.activePlayer = activePlayer;
	}

	public EPlayerId getActivePlayer() {
		return activePlayer;
	}

	public Image getImage() {
		return frameRenderer.getImage();
	}
}
