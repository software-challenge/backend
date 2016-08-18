package sc.plugin2017.gui.renderer.primitives;

import java.awt.Color;

import sc.plugin2017.FieldType;
import sc.plugin2017.GamePlugin;
import sc.plugin2017.gui.renderer.FrameRenderer;

/**
 * Zeichnet den Spielverlauf. Rundenanzahl + Bar
 * 
 * @author soeren
 * 
 */
public class ProgressBar extends PrimitiveBase {
  
  public GuiButton left;
  public GuiButton right;
  public GuiButton speedUp;
  public GuiButton speedDown;
  public GuiButton send;
  public GuiButton cancel; // TODO 

  public ProgressBar(FrameRenderer par) {
    super(par);
    left = new GuiButton(par, "Links");
    right = new GuiButton(par, "Rechts");
    speedUp = new GuiButton(par, "+");
    speedDown = new GuiButton(par, "-");
    send = new GuiButton(par, "Fertig");
  }

  @Override
  public void draw() {
    int round = 0;
    if (parent.currentGameState != null) {
      round = parent.currentGameState.getRound();
    }
    parent.pushStyle();

    // Umrandung
    parent.pushMatrix();
    parent.translate(0, parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT);
    parent.fill(GuiConstants.colorSideBarBG);
    parent.rect(0, 0, parent.getWidth(), parent.getHeight()
        * GuiConstants.PROGRESS_BAR_HEIGHT);
    // Text
    if(parent.currentGameState != null && !parent.currentGameState.gameEnded()) {
      parent.textFont(GuiConstants.fonts[0]);
      parent.textSize(GuiConstants.fontSizes[0]);   
  
      parent.translate(10, parent.textAscent());
      parent.fill(GuiConstants.colorBlack);
      parent.text("Runde:", 0, 0);
      // parent.translate(0, parent.textDescent() + parent.textAscent());
      parent.translate(parent.textWidth("Runde:"), 0);
      parent.text(round + 1, 0, 0);
      parent.translate(parent.textWidth(String.valueOf(round + 1)), 0);
      parent.text("/", 0, 0);
      parent.translate(parent.textWidth("/"), 0);
      parent.text(GamePlugin.MAX_TURN_COUNT, 0, 0);
    }
    parent.popMatrix();

    // Statusbalken
    parent.pushMatrix();

    parent.stroke(1.0f); // Umrandung
    parent.fill(GuiConstants.colorDarkGrey); // Filling

    float balkenWidth = parent.getWidth() - 120f;
    parent.translate(60, parent.getHeight() - 30);
    parent.rect(0, 0, balkenWidth, 20, 7);
    parent.fill(GuiConstants.colorHexFields);
    //parent.noStroke();
    if (round != 0)
      parent.rect(0, 0, (float) round * (balkenWidth / (float) GamePlugin.MAX_TURN_COUNT),
          20, 7);

    parent.popMatrix();

    parent.popStyle();
    
    // draw Buttons
    if(parent.isHumanPlayer() && parent.maxTurn == parent.currentGameState.getTurn()) {
      if(parent.currentGameState.getCurrentPlayer()
        .getField(parent.currentGameState.getBoard()).getType() != FieldType.SANDBANK) {
        right.draw();   
        left.draw();
        if(parent.currentGameState.getCurrentPlayer().getSpeed() != 1) {
          speedDown.draw();
        }
        if(parent.currentGameState.getCurrentPlayer().getSpeed()  != 6) {
          speedUp.draw();
        }
      }
      send.draw();
    }
  }
  
  @Override
  public void kill() {
    if(this.left != null && this.left.parent != null) {
      this.left.kill();
    }
    if(this.right != null && this.right.parent != null) {
      this.right.kill();
    }
    if(this.speedDown != null && this.speedDown.parent != null) {
      this.speedDown.kill();
    }
    if(this.speedUp != null && this.speedUp.parent != null) {
      this.speedUp.kill();
    }
    if(this.send != null && this.send.parent != null) {
      this.send.kill();
    }
    super.kill();
  }
}
