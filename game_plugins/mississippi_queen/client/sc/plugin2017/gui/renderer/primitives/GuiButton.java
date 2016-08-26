package sc.plugin2017.gui.renderer.primitives;

import processing.core.PApplet;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GuiButton extends PrimitiveBase {

  String message;
  int x;
  int y;
  int width;
  int height;
  int angle = 0;

  public GuiButton(FrameRenderer parent, String message) {
    super(parent);
    this.message = message;
  }

  @Override
  public void draw() {
    parent.pushMatrix();
    parent.pushStyle();

    parent.translate(x, y);
    //parent.rotate((float)Math.toRadians(angle));
    // TODO rotation makes buttons unclickable, solve by making buttons circular
    //continue here

    // draw stroke
    if(parent != null && parent.currentGameState != null && parent.currentGameState.getCurrentPlayerColor() == PlayerColor.BLUE)
      parent.stroke(GuiConstants.colorBlue);
    else if(parent != null && parent.currentGameState != null && parent.currentGameState.getCurrentPlayerColor() == PlayerColor.RED)
      parent.stroke(GuiConstants.colorRed);
    else
      parent.stroke(GuiConstants.colorBlack);
    // fill rect
    if(message == "Fertig" && parent.currentGameState.getCurrentPlayer().getMovement() != 0) {
      parent.fill(GuiConstants.colorOrange);
    } else {
      parent.fill(GuiConstants.colorLightLightGrey);
    }

    if (mouseHover()) {
      parent.fill(GuiConstants.colorLightLightGrey);
    }

    parent.rectMode(PApplet.CENTER);
    parent.ellipse(0, 0, width, height);
    // draw text
    parent.translate(3, 20);
    parent.fill(GuiConstants.colorBlack);

    parent.textFont(GuiConstants.fonts[0]);
    parent.textSize(GuiConstants.fontSizes[0]);
    parent.text(message, 6, 0);

    parent.popStyle();
    parent.popMatrix();
  }

  public boolean isClicked() {
    return mouseHover();
  }

  public boolean mouseHover() {
    if(parent.getMousePosition() != null) {
      return (parent.mouseX >= x
          && parent.mouseX < x + width
          && parent.mouseY >= y
          && parent.mouseY <y + height);
    } else {
      return false;
    }
  }

  /**
   * Tells the primitive that a mouse click happend. It may react accordingly.
   * @param x X coordinate of the click.
   * @param y Y coordinate of the click.
   */
  public void mouseClick(int x, int y) {

  }

  @Override
  public String toString() {
    return message + " X: " + x + " Y: " + y + " width: " + width + " height: " + height;
  }

  public void resize(int newWidth) {

  }
}
