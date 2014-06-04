/**
 * 
 */
package sc.plugin2015.gui.renderer;

import java.awt.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import sc.plugin2015.gui.renderer.RenderConfigurationDialog;
import sc.plugin2015.GameState;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.gui.renderer.primitives.Background;
import sc.plugin2015.gui.renderer.primitives.BoardFrame;
import sc.plugin2015.gui.renderer.primitives.GuiBoard;
import sc.plugin2015.gui.renderer.primitives.GuiConstants;
import sc.plugin2015.gui.renderer.primitives.GuiPenguin;
import sc.plugin2015.gui.renderer.primitives.ProgressBar;
import sc.plugin2015.gui.renderer.primitives.SideBar;

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

	private GuiBoard guiBoard;
	private Background background;
	private ProgressBar progressBar;
	private SideBar sidebar;
	private BoardFrame boardFrame;

	// penguin as [OWNER][NUMBER]
	private GuiPenguin[][] penguin;

	private GuiPenguin testPenguin;
	private GuiPenguin testPenguin2;
	private GuiPenguin testPenguin3;
	private GuiPenguin testPenguin4;
	private GuiPenguin testPenguin5;
	private GuiPenguin testPenguin6;
	private GuiPenguin testPenguin7;
	private GuiPenguin testPenguin8;

	public void setup() {
		// logger.debug("calling frameRenderer.size()");

		RenderConfiguration.loadSettings();

		// choosing renderer from options - using P2D as default
		if (RenderConfiguration.optionRenderer.equals("JAVA2D")) {
			size(this.width, this.height, JAVA2D);
			logger.debug("Using P2D as Renderer");
		} else if (RenderConfiguration.optionRenderer.equals("P3D")) {
			size(this.width, this.height, P3D);
			logger.debug("Using P3D as Renderer");
		} else {
			size(this.width, this.height, P2D);
			logger.debug("Using Java2D as Renderer");
		}

		noLoop(); // prevent thread from starving everything else
		smooth(RenderConfiguration.optionAntiAliasing); // Anti Aliasing

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
		/*
		 * testPenguin = new GuiPenguin(this, 0, 0, PlayerColor.BLUE);
		 * testPenguin2 = new GuiPenguin(this, 1, 0, PlayerColor.BLUE);
		 * testPenguin3 = new GuiPenguin(this, 0, 1, PlayerColor.BLUE);
		 * testPenguin4 = new GuiPenguin(this, 1, 1, PlayerColor.BLUE);
		 * testPenguin5 = new GuiPenguin(this, 5, 0, PlayerColor.BLUE);
		 * testPenguin6 = new GuiPenguin(this, 6, 0, PlayerColor.BLUE);
		 * testPenguin7 = new GuiPenguin(this, 6, 1, PlayerColor.BLUE);
		 * testPenguin8 = new GuiPenguin(this, 7, 1, PlayerColor.BLUE);
		 */

		// initial draw

		boardFrame = new BoardFrame(this);

		// initial draw
		background.draw();
		guiBoard.draw();
		progressBar.draw();
		sidebar.draw();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				penguin[i][j].draw();
			}
		}/*
		 * testPenguin.resize(); testPenguin.draw(); testPenguin2.resize();
		 * testPenguin2.draw(); testPenguin3.resize(); testPenguin3.draw();
		 * testPenguin4.resize(); testPenguin4.draw(); testPenguin5.resize();
		 * testPenguin5.draw(); testPenguin6.resize(); testPenguin6.draw();
		 * testPenguin7.resize(); testPenguin7.draw(); testPenguin8.resize();
		 * testPenguin8.draw();
		 */
		boardFrame.draw();
	}

	public void draw() {
		this.resize();
		background.draw();
		guiBoard.draw();
		progressBar.draw();
		sidebar.draw();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				penguin[i][j].draw();
			}
		}/*
		 * testPenguin.draw(); testPenguin2.draw(); testPenguin3.draw();
		 * testPenguin4.draw(); testPenguin5.draw(); testPenguin6.draw();
		 * testPenguin7.draw(); testPenguin8.draw();
		 */
		boardFrame.draw();
	}

	public void updateGameState(GameState gameState) {
		PlayerColor lastPlayerColor;
		guiBoard.update(gameState.getBoard());
		int i = gameState.getCurrentPlayerColor() == PlayerColor.RED ? 1 : 0;
		for (int j = 0; j < 4; j++) {
			if (gameState.getTurn() == 9) {
				lastPlayerColor = gameState.getCurrentPlayerColor();
			} else {
				lastPlayerColor = gameState.getOtherPlayerColor();
			}
			penguin[i][j].update(gameState.getLastMove(), lastPlayerColor,
					gameState.getTurn());
		}
		redraw();
	}

	public void requestMove(int maxTurn) {
		// TODO The User has to do a Move

	}

	public Image getImage() {
		// TODO return an Image of the current board
		return null;
	}

	public void mouseClicked() {
		this.resize();
		// this.redraw();
	}

	public void resize() {
		guiBoard.resize();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 4; j++) {
				penguin[i][j].resize();
			}
		}
		// testPenguin.resize();
		this.redraw();

	}

	public void keyPressed() {
		if (key == 'c' || key == 'C') {
			new RenderConfigurationDialog(FrameRenderer.this);
		}

	}

}
