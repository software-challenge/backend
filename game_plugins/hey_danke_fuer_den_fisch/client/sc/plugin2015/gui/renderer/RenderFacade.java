package sc.plugin2015.gui.renderer;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.plugin2015.EPlayerId;
import sc.plugin2015.GameState;
import sc.plugin2015.IGameHandler;
import sc.plugin2015.Move;
import sc.plugin2015.Player;
import sc.plugin2015.PlayerColor;
import sc.shared.GameResult;
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
	private boolean receiverThreadRunning = false;
	private final List<GameState> gameStateQueue;
	private final Runnable gameStateReciever = new Runnable() {

		@Override
		public void run() {
			while (receiverThreadRunning) {

				try {

					synchronized (gameStateQueue) {
						// Make sure we don't end up in a deadlock
						if (gameStateQueue.size() == 0) {
							gameStateQueue.wait();
						}

						while (gameStateQueue.size() > 0) {
							GameState gameState = gameStateQueue.remove(0);
							frameRenderer.updateGameState(gameState);
							gameState = null;
						}
					}

				} catch (Exception e) {

					try {
						File file = new File("sync.error");
						if (!file.exists()) {
							file.createNewFile();
						}
						FileWriter fWriter = new FileWriter(file, true);
						PrintWriter pWriter = new PrintWriter(fWriter);
						e.printStackTrace(pWriter);
						pWriter.flush();
						pWriter.close();

					} catch (IOException ex) {

					}
					e.printStackTrace();
				}
			}
		}
	};
	private GameState lastGameState;

	private boolean first;

	private boolean disabled;

	private int maxTurn;

	private RenderFacade() {
		//frameRenderer = new FrameRenderer();
		
		
		gameStateQueue = new LinkedList<GameState>();
		//startReceiverThread();

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

	public void setRenderContext(final JPanel panel, final boolean threeDimensional) {

		synchronized (gameStateQueue) {
			gameStateQueue.clear();
		}
		
		maxTurn = 0;
		first = true;
		disabled = false;
		activePlayer = null;
		alreadyCreatedPlayerOne = false;
		frameRenderer = new FrameRenderer(); // neuer FrameRenderer

		if (panel != null) {
			//panel.setDoubleBuffered(true);
			panel.setLayout(new BorderLayout());
			panel.add(frameRenderer, BorderLayout.CENTER);
			frameRenderer.setBounds(0, 0, 1022, 595);
			frameRenderer.init();
			
			
			//frameRenderer.resize(panel.getSize());
			//frameRenderer.redraw();
		}
		startReceiverThread();
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
				startReceiverThread();
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

		activePlayer = id;
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
			switchToPlayer(id);
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

	}

	/**
	 * @param data
	 * @param id
	 */
	public void gameEnded(GameResult data, EPlayerId target, PlayerColor color, String errorMessage) {

		stopReceiverThread();

		System.out.println();
		if (disabled) {
			return;
		}
		if (data != null) {
			ScoreCause cause = data.getScores().get(color == PlayerColor.RED ? 0 : 1).getCause();

			if (errorMessage == null && cause != ScoreCause.REGULAR) {

				String err = "'" + lastGameState.getPlayerNames()[color == PlayerColor.RED ? 0 : 1]
						+ "' hat keinen Zug gesendet.\\n";

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

				lastGameState.endGame(color.opponent(), err);
				updateGameState(lastGameState, true);
			}
		}

		if (errorMessage != null) {
			lastGameState.endGame(color.opponent(), errorMessage);
			updateGameState(lastGameState, true);
		}

	}

	public EPlayerId getActivePlayer() {
		return activePlayer;
	}

	public Image getImage() {
		return frameRenderer.getImage();
	}
}
