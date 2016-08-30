package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;

/**
 * Zeichnet den "Rahmen" um das Spielfeld. Rot/Blau wenn der betreffende Spieler
 * an der Reihe ist, sonst Grau.
 */
public class BoardFrame extends PrimitiveBase {

  public BoardFrame(FrameRenderer parent) {
    super(parent);
  }

  @Override
  public void draw() {
    parent.pushStyle();
    parent.noStroke();
    if (parent.currentGameState != null && parent.currentGameState.getCurrentPlayer() != null) {
      if (parent.currentGameState.getCurrentPlayer().getPlayerColor() == PlayerColor.RED) {
        parent.fill(GuiConstants.colorRed);
      } else if (parent.currentGameState.getCurrentPlayer().getPlayerColor() == PlayerColor.BLUE) {
        parent.fill(GuiConstants.colorBlue);
      } else {
        parent.fill(GuiConstants.colorGrey);
      }
    }

    parent.rect(0, 0, parent.getWidth(), GuiConstants.frameBorderSize);
    parent.rect(0, 0, GuiConstants.frameBorderSize, parent.getHeight());
    parent.rect(parent.getWidth() - GuiConstants.frameBorderSize, 0, GuiConstants.frameBorderSize, parent.getHeight());
    parent.rect(0, parent.getHeight() - GuiConstants.frameBorderSize, parent.getWidth(), GuiConstants.frameBorderSize);

    parent.popStyle();
  }

}
