/**
 * 
 */
package sc.plugin2015.gui.renderer;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import sc.plugin2015.gui.renderer.RenderConfigurationDialog;
import sc.plugin2015.EPlayerId;
import sc.plugin2015.GameState;
import sc.plugin2015.Move;
import sc.plugin2015.NullMove;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.RunMove;
import sc.plugin2015.SetMove;
import sc.plugin2015.gui.renderer.primitives.Background;
import sc.plugin2015.gui.renderer.primitives.BoardFrame;
import sc.plugin2015.gui.renderer.primitives.GameEndedDialog;
import sc.plugin2015.gui.renderer.primitives.GuiBoard;
import sc.plugin2015.gui.renderer.primitives.GuiConstants;
import sc.plugin2015.gui.renderer.primitives.GuiPenguin;
import sc.plugin2015.gui.renderer.primitives.ProgressBar;
import sc.plugin2015.gui.renderer.primitives.SideBar;
import sc.plugin2015.util.Constants;

/**
 * @author fdu
 */

public class FrameRenderer extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory
			.getLogger(FrameRenderer.class);

	public GameState currentGameState;
	private boolean isUpdated;
	private boolean humanPlayer;
	private boolean humanPlayerMaxTurn;
	private int maxTurn;

	private EPlayerId id;

	private GuiBoard guiBoard;
	private Background background;
	private ProgressBar progressBar;
	private SideBar sidebar;
	private BoardFrame boardFrame;

	// penguin as [OWNER][NUMBER]
	private GuiPenguin[][] penguin;

	public FrameRenderer() {
		super();

		// logger.debug("calling frameRenderer.size()");
		this.humanPlayer = false;
		this.humanPlayerMaxTurn = false;
		isUpdated = false;
		this.id = EPlayerId.OBSERVER;

		RenderConfiguration.loadSettings();

		background = new Background(this);
		logger.debug("Dimension when creating board: (" + this.width + ","
				+ this.height + ")");
		guiBoard = new GuiBoard(this);
		progressBar = new ProgressBar(this);
		sidebar = new SideBar(this);

		penguin = new GuiPenguin[2][4];

		penguin[0][0] = new GuiPenguin(this, -1, -1, PlayerColor.RED);
		penguin[0][1] = new GuiPenguin(this, -2, -1, PlayerColor.RED);
		penguin[0][2] = new GuiPenguin(this, -3, -1, PlayerColor.RED);
		penguin[0][3] = new GuiPenguin(this, -4, -1, PlayerColor.RED);
		penguin[1][0] = new GuiPenguin(this, -1, -1, PlayerColor.BLUE);
		penguin[1][1] = new GuiPenguin(this, -2, -1, PlayerColor.BLUE);
		penguin[1][2] = new GuiPenguin(this, -3, -1, PlayerColor.BLUE);
		penguin[1][3] = new GuiPenguin(this, -4, -1, PlayerColor.BLUE);

		boardFrame = new BoardFrame(this);
		//logger.debug("Constructor finished");
	}

	public void setup() {
		maxTurn = -1;
		// this.frameRate(30);
		// choosing renderer from options - using P2D as default
		if (RenderConfiguration.optionRenderer.equals("JAVA2D")) {
			logger.debug("Using Java2D as Renderer");
			size(this.width, this.height, JAVA2D);
		} else if (RenderConfiguration.optionRenderer.equals("P3D")) {
			logger.debug("Using P3D as Renderer");
			size(this.width, this.height, P3D);
		} else {
			logger.debug("Using P2D as Renderer");
			size(this.width, this.height, P2D);
		}

		// noLoop(); // prevent thread from starving everything else
		smooth(RenderConfiguration.optionAntiAliasing); // Anti Aliasing

		// initial draw
		GuiConstants.generateFonts(this);
		resize(this.width, this.height);
		
	}

	public void draw() {
		// resize();
		background.draw();
		guiBoard.draw();
		progressBar.draw();
		sidebar.draw();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				penguin[i][j].draw();
			}
		}
		boardFrame.draw();
		if (currentGameState != null && currentGameState.gameEnded()) {
			GameEndedDialog.draw(this);
		}
	}

	public void updateGameState(GameState gameState) {
		int lastTurn = -1;
		if (currentGameState != null) {
			lastTurn = currentGameState.getTurn();
		}
		currentGameState = gameState;
		if (gameState != null && gameState.getBoard() != null)
			guiBoard.update(gameState.getBoard());
		if (currentGameState == null
				|| lastTurn == currentGameState.getTurn() - 1) {

			if (maxTurn == currentGameState.getTurn() - 1) {

				maxTurn++;
				humanPlayerMaxTurn = false;
			}
			PlayerColor lastPlayerColor;
			int i;
			if (gameState.getTurn() == 8) {
				lastPlayerColor = gameState.getCurrentPlayerColor();
				i = gameState.getCurrentPlayerColor() == PlayerColor.RED ? 0
						: 1;
			} else {
				lastPlayerColor = gameState.getOtherPlayerColor();
				i = gameState.getCurrentPlayerColor() == PlayerColor.RED ? 1
						: 0;
			}
			for (int j = 0; j < 4; j++) {
				// System.out.println(" test "+ penguin[i][j].getFieldX());
				penguin[i][j].update(gameState.getLastMove(), lastPlayerColor,
						gameState.getTurn(), humanPlayer);
			}
		} else {
			int blue = 0;
			int red = 0;
			for (int i = 0; i < Constants.ROWS; i++) {
				for (int j = 0; j < Constants.COLUMNS; j++) {
					if (gameState.getBoard().getPenguin(i, j) != null) {
						if (gameState.getBoard().getPenguin(i, j).getOwner() == PlayerColor.BLUE) {
							penguin[1][blue].setFieldX(i);
							penguin[1][blue].setFieldY(j);
							blue++;
						} else {
							penguin[0][red].setFieldX(i);
							penguin[0][red].setFieldY(j);
							red++;
						}
					}
				}
			}
			for (int i = blue + 1; i < 5; i++) {
				penguin[1][i - 1].setFieldX(-i);
				penguin[1][i - 1].setFieldY(-1);
			}
			for (int i = red + 1; i < 5; i++) {
				penguin[0][i - 1].setFieldX(-i);
				penguin[0][i - 1].setFieldY(-1);
			}
		}
		// System.out.println("maxTurn = " + maxTurn);
		humanPlayer = false;
		System.out.println("set humanplayer to false");
		if (currentGameState != null && maxTurn == currentGameState.getTurn() && humanPlayerMaxTurn) {
			humanPlayer = true;
		}
		System.out.println(humanPlayer? "+++++++++++++++++++++++humanPlayer = true" : "+++++++++++++++++++++++humanPlayer = false");
		System.out.println(humanPlayerMaxTurn? "+++++++++++++++++++++++humanPlayerMaxTurn = true" : "+++++++++++++++++++++++humanPlayerMaxTurn = false");
		System.out.println(maxTurn);
		if (currentGameState != null)
			System.out.println(currentGameState.getTurn());
		isUpdated = true;
	}

	public void requestMove(int maxTurn, EPlayerId id) {
		while (!isUpdated) {
			try {
				Thread.sleep(20);
				System.out.println("should not appear too often");
			} catch (InterruptedException e) {
			}
		}
		System.out.println("isUptaded was true at this point");
		isUpdated = false;
		int turn = currentGameState.getTurn();
		this.id = id;
		//System.out.println("turn = " + turn);
		if ((turn < 8 && turn % 2 == 1) || (turn >= 8 && turn % 2 == 0)) {
			// System.out.println("Blauer Spieler ist dran");
			if (id == EPlayerId.PLAYER_ONE) {
				// System.out.println("Spielerupdate");
				this.id = EPlayerId.PLAYER_TWO;
			}
		}
		// this.maxTurn = maxTurn;
		this.humanPlayer = true;
		humanPlayerMaxTurn = true;
		System.out.println("set humanPlayer to true!");
	}

	public Image getImage() {
		// TODO return an Image of the current board
		return null;
	}

	public void mouseClicked(MouseEvent e) {
		if (isHumanPlayer() && maxTurn == currentGameState.getTurn()) {
			// System.out.println("Mouse clicked");
			int x = e.getX();
			int y = e.getY();
			int player;
			if (id == EPlayerId.PLAYER_ONE) {
				player = 0;
			} else {
				player = 1;
			}
			float buttonX = getWidth() / 2f - 50;
			float buttonY = getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5;
			if (this.currentGameState.getTurn() > 7 && x > buttonX
					&& y > buttonY && x < buttonX + 100 && y < buttonY + 25) {
				// System.out.println("Aussetzknopf gedrückt");
				RenderFacade.getInstance().sendMove(new NullMove());
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (isHumanPlayer() && maxTurn == currentGameState.getTurn()) {
			int x = e.getX();
			int y = e.getY();
			int player;
			if (id == EPlayerId.PLAYER_ONE) {
				player = 0;
			} else {
				player = 1;
			}
			for (int i = 0; i < 4; i++) {
				if (isPenguinClicked(penguin[player][i], x, y)
						&& (this.currentGameState.getTurn() > 7 || penguin[player][i]
								.getFieldX() < 0)) {
					// loop();
					penguin[player][i].attachToMouse();
					List<Move> moves = currentGameState
							.getPossibleMovesForPenguin(
									penguin[player][i].getFieldX(),
									penguin[player][i].getFieldY());
					for (Move m : moves) {
						if (m instanceof SetMove) {
							this.guiBoard.highlightHexField(
									((SetMove) m).getSetY(),
									((SetMove) m).getSetX());
						} else if (m instanceof RunMove) {
							this.guiBoard.highlightHexField(
									((RunMove) m).getToY(),
									((RunMove) m).getToX());
						}
					}
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (isHumanPlayer() && maxTurn == currentGameState.getTurn()) {
			int x = e.getX();
			int y = e.getY();
			int player;
			if (id == EPlayerId.PLAYER_ONE) {
				player = 0;
			} else {
				player = 1;
			}
			for (int i = 0; i < 4; i++) {
				if (penguin[player][i].isAttached()) {
					int[] fieldCoordinates = getFieldCoordinates(x, y);
					if (fieldCoordinates != null) {
						if (penguin[player][i].getFieldX() < 0) {
							SetMove move = new SetMove(fieldCoordinates[0],
									fieldCoordinates[1]);
							if (this.currentGameState.getPossibleSetMoves()
									.contains(move)) {
								RenderFacade.getInstance().sendMove(move);
							}
						} else {
							RunMove move = new RunMove(
									penguin[player][i].getFieldX(),
									penguin[player][i].getFieldY(),
									fieldCoordinates[0], fieldCoordinates[1]);
							if (this.currentGameState.getPossibleMoves()
									.contains(move)) {
								RenderFacade.getInstance().sendMove(move);
							}
						}
					}
					penguin[player][i].releaseFromMouse();
					this.guiBoard.unHighlightAll();
				}
			}
		}
	}

	private int[] getFieldCoordinates(int x, int y) {
		// TODO implement this function
		int fieldX;
		int fieldY;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8 && (!(i % 2 == 0 && j == 7)); j++) {
				if (x >= guiBoard.getHexFields()[i][j].getX()
						&& x <= guiBoard.getHexFields()[i][j].getX()
								+ guiBoard.getHexFields()[i][j].getB() * 2
						&& y >= guiBoard.getHexFields()[i][j].getY()
								+ guiBoard.getHexFields()[i][j].getA()
						&& y <= guiBoard.getHexFields()[i][j].getY()
								+ guiBoard.getHexFields()[i][j].getA()
								+ guiBoard.getHexFields()[i][j].getC()) {
					/*
					 * System.out.println("x = " +
					 * guiBoard.getHexFields()[i][j].getFieldX() + ", y = " +
					 * guiBoard.getHexFields()[i][j].getFieldY());
					 */
					return new int[] {
							guiBoard.getHexFields()[i][j].getFieldX(),
							guiBoard.getHexFields()[i][j].getFieldY() };
				}
			}
		}
		return null;
	}

	private boolean isPenguinClicked(GuiPenguin penguin, int x, int y) {
		if (x >= penguin.getX() && y >= penguin.getY()
				&& x <= penguin.getX() + penguin.getWidth()
				&& y <= penguin.getY() + penguin.getHeight()) {
			return true;
		}
		return false;
	}

	public void resize(int width, int height) {
		background.resize(width, height);
		guiBoard.resize(width, height);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				penguin[i][j].resize(width, height);
			}
		}
	}

	/*
	 * Hack! wenn das Fenster resized wird, wird setBounds aufgerufen. hier
	 * rufen wir resize auf um die Komponenten auf die richtige größe zu
	 * bringen.
	 */
	public void setBounds(int x, int y, int width, int height) {
		//System.out.println("got an setBounds- x:" + x + ",y: " + y + ",width: "
		//		+ width + ",height: " + height);
		super.setBounds(x, y, width, height);
		this.resize(width, height);
	}

	public void keyPressed() {
		if (key == 'c' || key == 'C') {
			new RenderConfigurationDialog(FrameRenderer.this);
		}

	}

	public boolean isHumanPlayer() {
		return humanPlayer;
	}

	public EPlayerId getId() {
		return id;
	}

}
