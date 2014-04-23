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

import processing.core.PApplet;
import sc.plugin2015.GameState;

/**
 * @author fdu
 */

public class FrameRenderer extends PApplet {
	
	public void setup() {
		size(200, 200);
		background(0);
	}

	public void draw() {
		stroke(255);
		if (mousePressed) {
			line(mouseX, mouseY, pmouseX, pmouseY);
		}
	}

	public void updateGameState(GameState gameState) {
		draw();
		
	}

	public void requestMove(int maxTurn) {
		// TODO Auto-generated method stub
		
	}

	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}
