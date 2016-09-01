package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.GamePlugin;
import sc.plugin2017.gui.renderer.FrameRenderer;

/**
 * Zeichnet den Spielverlauf. Rundenanzahl + Bar
 */
public class ProgressBar extends PrimitiveBase {

  public ProgressBar(FrameRenderer parent) {
    super(parent);
  }

  @Override
  public void draw() {
    parent.pushStyle();
    parent.pushMatrix();

    int round = parent.getCurrentRound();

    int width = parent.getWidth();
    int height = Math.round(parent.getHeight() * GuiConstants.PROGRESS_BAR_HEIGHT);

    // Umrandung
    parent.translate(0, parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT);
    parent.fill(GuiConstants.colorSideBarBG);
    parent.rect(0, 0, width, height);

    // Text
    if(parent.gameActive()) {
      parent.pushMatrix();
      parent.textSize(Math.round(height * 0.4));
      parent.translate(10, parent.textAscent());
      parent.fill(GuiConstants.colorBlack);
      parent.text(String.format("Runde: %d / %d", round + 1, GamePlugin.MAX_TURN_COUNT), 0, 0);
      parent.popMatrix();
    }

    // Statusbalken
    parent.stroke(1.0f);
    parent.fill(GuiConstants.colorDarkGrey);
    float barWidth = width * 0.9f;
    float barHeight = height * 0.4f;
    parent.translate(Math.round(width * 0.05), height - barHeight - (height * 0.1f));
    parent.rect(0, 0, barWidth, barHeight, barWidth * 0.2f);
    parent.fill(GuiConstants.colorProgressBar);
    if (round != 0) {
      parent.rect(0, 0, round * (barWidth / GamePlugin.MAX_TURN_COUNT), barHeight, barWidth * 0.2f);
    }

    parent.popMatrix();
    parent.popStyle();
  }

  @Override
  public void kill() {
    super.kill();
  }
}
