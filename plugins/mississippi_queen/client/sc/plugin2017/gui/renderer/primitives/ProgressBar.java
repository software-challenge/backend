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
    int height = (int) Math.round(parent.getHeight() * GuiConstants.PROGRESS_BAR_HEIGHT);

    // Umrandung
    parent.translate(0, (float) (parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT));
    parent.fill(GuiConstants.colorSideBarBG);
    parent.rect(0, 0, width, height);

    // Text
    if(parent.gameActive()) {
      parent.pushMatrix();
      parent.textSize(Math.round(height * 0.3));
      parent.translate(10, parent.textAscent());
      parent.fill(GuiConstants.colorBlack);
      parent.text(String.format("Runde: %d / %d", round + 1, GamePlugin.MAX_TURN_COUNT), 0, 0);
      parent.popMatrix();
    }

    // Statusbalken
    parent.stroke(1.0f);
    parent.fill(GuiConstants.colorDarkGrey);
    double barWidth = width * 0.9;
    double barHeight = height * 0.4;
    parent.translate((Math.round(width * 0.05)), (float) (height - barHeight - (height * 0.1)));
    parent.rect(0, 0, (float) barWidth, (float) barHeight, (float) (barWidth * 0.2));
    parent.fill(GuiConstants.colorProgressBar);
    if (round != 0) {
      parent.rect(0, 0, (float) (round * (barWidth / GamePlugin.MAX_TURN_COUNT)), (float) barHeight, (float) (barWidth * 0.2));
    }

    parent.popMatrix();
    parent.popStyle();
  }

  @Override
  public void kill() {
    super.kill();
  }
}
