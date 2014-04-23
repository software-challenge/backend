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

import java.awt.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import sc.plugin2015.GameState;

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

	public void setup() {
		// original setup code here ...
		size(400, 400);
		// prevent thread from starving everything else
		noLoop();
		x = getWidth() / 2;
		y = getHeight() / 2;
	}

	public void draw() {
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
		fill(255, 200, 0);
		rect(x, y, 50, 50);
	}

	public void updateGameState(GameState gameState) {
		// TODO Auto-generated method stub
		startAnimation = true;
		loop();
	}

	public void requestMove(int maxTurn) {
		// TODO Auto-generated method stub

	}

	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}
