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
import sc.plugin2016.GamePlugin;
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

	public static int calcColor(int alpha, int r, int g, int b) {
    int col = 0;
    col |= alpha << 24;
    col |= r << 16;
    col |= g << 8;
    col |= b;
    return col;
  }

  public static final int colorBackGround = calcColor(200, 200, 200, 200);
  public static final int colorRed = calcColor(255, 200, 0, 0);
  public static final int colorBlue = calcColor(255, 0, 0, 200);
  public static final int colorGreen = calcColor(255, 0, 255, 0);
  public static final int colorLightGrey = calcColor(255, 100, 100, 100);
  public static final int colorLightLightGrey = calcColor(255, 130, 130, 130);
  public static final int colorGrey = calcColor(255, 50, 68, 70);
  public static final int colorDarkGrey = calcColor(255, 30, 30, 30);
  public static final int colorBlack = calcColor(255, 0, 0, 0);
  public static final int colorSideBarBG = calcColor(200, 200, 200, 200);
  public static final int colorHexFields = calcColor(240, 21, 160, 177);
  public static final int colorHexFieldsHighlight = calcColor(240, 21, 195, 177);
  public static final int colorText = calcColor(255, 0, 0, 0);
  public static final int colorGreyOut = calcColor(100, 30, 30, 30);
	
	public void draw() {
    this.pushMatrix();
    this.translate(0, this.getHeight() * 50);
    this.fill(colorBlue);
    this.rect(0, 0, this.getWidth(), this.getHeight()
        * 100);
    
    this.popMatrix();
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
