/**
 * 
 */
package sc.plugin2015.gui.renderer;

import static sc.plugin2015.gui.renderer.RenderConfiguration.ANTIALIASING;
import static sc.plugin2015.gui.renderer.RenderConfiguration.BACKGROUND;
import static sc.plugin2015.gui.renderer.RenderConfiguration.OPTIONS;
import static sc.plugin2015.gui.renderer.RenderConfiguration.TRANSPARANCY;
import static sc.plugin2015.gui.renderer.RenderConfiguration.MOVEMENT;
import static sc.plugin2015.gui.renderer.RenderConfiguration.DEBUG_VIEW;

import java.awt.Dimension;
import java.awt.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import sc.plugin2015.GameState;
import sc.plugin2015.gui.renderer.primitives.Background;
import sc.plugin2015.gui.renderer.primitives.GuiBoard;
import sc.plugin2015.gui.renderer.primitives.HexField;

/**
 * @author fdu
 */

public class FrameRenderer extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean startAnimation = false;
	private int x;
	private int y;
	private static final Logger logger = LoggerFactory
			.getLogger(FrameRenderer.class);
	
	
	private GuiBoard guiBoard;
	private HexField testField;
	private Background background;

	public void setup() {
		size(1024, 800 , P3D);	// Size and Renderer: either P2D, P3D or nothing(Java2D)
		noLoop();				// prevent thread from starving everything else
		//smooth();				// Anti Aliasing to 4
		
		background = new Background(this);
		guiBoard = new GuiBoard();
		
		x = getWidth() / 2;
		y = getHeight() / 2;
		testField = new HexField(this, x, y, 50);
		fill(255, 200, 0);
		
		
		//initial draw
		background.draw();
		testField.draw();
	}

	public void draw() {
		
		background.draw();		
		testField.draw();
		
		
		if (startAnimation) {
			x++;
			startAnimation = false;
		}
		if (x == getWidth() / 2) {
			noLoop();
		} else {
			if (x > getWidth()) {
				x = 0;
			} else {
				x++;
			}
		}
	}

	public void updateGameState(GameState gameState) {
		// TODO Auto-generated method stub
		
		startAnimation = true;
		loop();
	}

	public void requestMove(int maxTurn) {
		// TODO The User has to do a Move

	}

	public Image getImage() {
		// TODO return an Image of the current board
		return null;
	}

}
