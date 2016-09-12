package sc.plugin2017.gui.renderer.primitives;

import java.util.Iterator;

import sc.plugin2017.Action;
import sc.plugin2017.DebugHint;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;
import sc.plugin2017.gui.renderer.RenderConfiguration;

/**
 * Zeichnet Spielerinformationen (Punkte) aktueller Zug
 * des Spieles.
 */
public class SideBar extends PrimitiveBase {

  private PlayerColor currentColor;
  private String redName = GuiConstants.DEFAULT_RED_NAME;
  private int redPoints = GuiConstants.DEFAULT_RED_POINTS;
  private String blueName = GuiConstants.DEFAULT_BLUE_NAME;
  private int bluePoints = GuiConstants.DEFAULT_BLUE_POINTS;

  private float width;
  private float height;
  private float baseFontSize;
  private String referenceString = "Dieser Text sollte passen.";

  public SideBar(FrameRenderer parent) {
    super(parent);
    this.parent = parent;
  }

  public void update(PlayerColor currentColor, String redName, int redPoints, String blueName, int bluePoints) {
    this.currentColor = currentColor;
    this.redName = redName;
    this.redPoints = redPoints;
    this.blueName = blueName;
    this.bluePoints = bluePoints;

    // calculate maximum font size to fit longest player name, or, if that is
    // shorter than the reference string, fit the reference string into sidebar
    float maxTextWidth = width * 0.9f;
    String longestPlayerName;
    if (redName.length() > blueName.length()) {
      longestPlayerName = redName;
    } else {
      longestPlayerName = blueName;
    }
    if (longestPlayerName.length() > referenceString.length()) {
      referenceString = longestPlayerName;
    }
    parent.textSize(10);
    baseFontSize = maxTextWidth / parent.textWidth(referenceString) * 10;
  }

  public void update(PlayerColor currentColor) {
    update(currentColor, GuiConstants.DEFAULT_RED_NAME, GuiConstants.DEFAULT_RED_POINTS, GuiConstants.DEFAULT_BLUE_NAME, GuiConstants.DEFAULT_BLUE_POINTS);

  }

  @Override
  public void draw() {
    parent.pushStyle();

    parent.stroke(1.0f); // Umrandung
    parent.fill(GuiConstants.colorSideBarBG);

    parent.pushMatrix();
    width = parent.getWidth() * GuiConstants.SIDE_BAR_WIDTH;
    height = parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT;
    parent.translate(parent.getWidth() * GuiConstants.SIDE_BAR_START_X,
        GuiConstants.SIDE_BAR_START_Y);
    parent.rect(0, 0, width, height);

    parent.textSize(baseFontSize);

    // Text
    // erster Spieler

    if (currentColor == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
    } else {
      parent.fill(GuiConstants.colorBlack);
    }

    parent.translate(20, parent.textAscent() + 20);
    parent.text(redName, 0, 0);

    // Punkte
    parent.textSize(baseFontSize * 0.8f);
    parent.translate(0, parent.textAscent() + parent.textDescent());
    parent.text("Punkte: " + redPoints, 0, 0);

    // Blauer Spieler.
    parent.textSize(baseFontSize);
    parent.translate(0, parent.textAscent() + parent.textDescent());
    if (currentColor == PlayerColor.BLUE) {
      parent.fill(GuiConstants.colorBlue);
    } else {
      parent.fill(GuiConstants.colorBlack);
    }
    parent.text(blueName, 0, 0);
    // Punkte
    parent.textSize(baseFontSize * 0.8f);
    parent.translate(0, parent.textAscent() + parent.textDescent());
    parent.text("Punkte: " + bluePoints, 0, 0);

    // Aktueller Zug
    if (currentColor == PlayerColor.BLUE) {
      parent.fill(GuiConstants.colorBlue);
    } else if (currentColor == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
    } else {
      parent.fill(GuiConstants.colorBlack);
    }
    parent.textSize(baseFontSize * 0.7f);
    parent.translate(0, 2 * (parent.textAscent() + parent.textDescent()));
    parent.text("Aktionen des aktuellen Zuges:", 0, 0);

    // using an iterator to avoid ConcurrentModificationException
    for (final Iterator<Action> iterator = parent.getCurrentActions().iterator(); iterator.hasNext(); ) {
      parent.translate(0, parent.textAscent() + parent.textDescent());
      parent.text(iterator.next().toString(), 0, 0);
    }

    // Debug Ausgabe
    parent.textSize(baseFontSize * 0.5f);
    parent.fill(GuiConstants.colorBlack);
    if (RenderConfiguration.optionDebug) {
      parent.translate(0, parent.textAscent() + parent.textDescent());
      // using an iterator to avoid ConcurrentModificationException
      for (final Iterator<DebugHint> iterator = parent.getCurrentHints().iterator(); iterator.hasNext(); ) {
          parent.translate(0, parent.textAscent() + parent.textDescent());
          parent.text(iterator.next().getContent(), 0, 0);
      }
    }

    parent.popMatrix();
    parent.popStyle();
  }

}
