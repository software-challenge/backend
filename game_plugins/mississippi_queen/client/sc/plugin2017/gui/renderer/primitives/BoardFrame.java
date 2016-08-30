package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;

/**
 * Zeichnet den "Rahmen" um das Spielfeld. Rot/Blau wenn der betreffende Spieler
 * an der Reihe ist, sonst Grau.
 */
public class BoardFrame extends PrimitiveBase {

  // Red, Blue or null
  private PlayerColor currentColor;

  public BoardFrame(FrameRenderer parent) {
    super(parent);
    currentColor = null;
  }

  public void update(PlayerColor currentColor) {
    this.currentColor = currentColor;
  }

  @Override
  public void draw() {
    parent.pushStyle();
    parent.noStroke();

    if (currentColor == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
    } else if (currentColor == PlayerColor.BLUE) {
      parent.fill(GuiConstants.colorBlue);
    } else {
      parent.fill(GuiConstants.colorGrey);
    }

    parent.rect(0, 0, parent.getWidth(), GuiConstants.frameBorderSize);
    parent.rect(0, 0, GuiConstants.frameBorderSize, parent.getHeight());
    parent.rect(parent.getWidth() - GuiConstants.frameBorderSize, 0, GuiConstants.frameBorderSize, parent.getHeight());
    parent.rect(0, parent.getHeight() - GuiConstants.frameBorderSize, parent.getWidth(), GuiConstants.frameBorderSize);

    parent.popStyle();
  }

}
