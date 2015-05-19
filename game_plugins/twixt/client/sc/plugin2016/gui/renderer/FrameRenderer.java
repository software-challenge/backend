/**
 * 
 */
package sc.plugin2016.gui.renderer;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import sc.plugin2016.EPlayerId;
import sc.plugin2016.GameState;
import sc.plugin2016.PlayerColor;
import sc.plugin2016.gui.renderer.RenderConfigurationDialog;

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


	public FrameRenderer() {
		
	}

	public void setup() {
		
	}

	public void draw() {
		
	}

	public void updateGameState(GameState gameState) {
		
	}

	public void requestMove(int maxTurn, EPlayerId id) {
		
	}

	public Image getImage() {
		// TODO return an Image of the current board
		return null;
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	
	public void resize(int width, int height) {
		
	}
	public void keyPressed() {
		if (key == 'c' || key == 'C') {
			new RenderConfigurationDialog(FrameRenderer.this);
		}

	}

	public EPlayerId getId() {
		return null;
	}
}
