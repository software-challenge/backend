package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.Action;
import sc.plugin2017.DebugHint;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;
import sc.plugin2017.gui.renderer.RenderConfiguration;

/**
 * Zeichnet Spielerinformationen (Punkte) aktueller Zug
 * des Spieles.
 */
// TODO find out why dispalyName == null
public class SideBar extends PrimitiveBase {

  private PlayerColor currentColor;
  private String redName = GuiConstants.DEFAULT_RED_NAME;
  private int redPoints = GuiConstants.DEFAULT_RED_POINTS;
  private String blueName = GuiConstants.DEFAULT_BLUE_NAME;
  private int bluePoints = GuiConstants.DEFAULT_BLUE_POINTS;
  
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
    parent.translate(parent.getWidth() * GuiConstants.SIDE_BAR_START_X,
        GuiConstants.SIDE_BAR_START_Y);
    parent.rect(0, 0, parent.getWidth() * GuiConstants.SIDE_BAR_WIDTH,
        parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT);
    // Text
    // erster Spieler
    parent.textFont(GuiConstants.fonts[2]);
    parent.textSize(GuiConstants.fontSizes[2]);

    if (currentColor == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
    } else {
      parent.fill(GuiConstants.colorBlack);
    }

    parent.translate(20, parent.textAscent() + 20);
    // passe Textgröße an
    int  preferredTextSize = 25;
    preferredTextSize = (int) (30f / parent.textWidth(redName) * (parent.getWidth() * GuiConstants.SIDE_BAR_WIDTH - 25));

    if (!(preferredTextSize > 30)) {
      parent.textSize(preferredTextSize);
    }

    parent.text(redName, 0, 0);

    // Punkte + Kohle + Geschwindigkeit
    parent.textFont(GuiConstants.fonts[1]);
    parent.textSize(GuiConstants.fontSizes[1]);


    parent.translate(0, parent.textAscent() + parent.textDescent());
    parent.text("Punkte: " + redPoints, 0, 0);

    // Blauer Spieler.
    parent.textFont(GuiConstants.fonts[2]);
    parent.textSize(GuiConstants.fontSizes[2]);

    parent.translate(0, parent.textAscent() + parent.textDescent());
    if (currentColor == PlayerColor.BLUE) {
      parent.fill(GuiConstants.colorBlue);
    } else {
      parent.fill(GuiConstants.colorBlack);
    }

    // passe Textgröße an
    preferredTextSize = (int) (30f / parent.textWidth(blueName) * (parent.getWidth() * GuiConstants.SIDE_BAR_WIDTH - 25));
    if (!(preferredTextSize > 30)) {
      parent.textSize(preferredTextSize);
    }

    parent.text(blueName, 0, 0);
    // Punkte
    parent.textSize(GuiConstants.fontSizes[1]);
    parent.textFont(GuiConstants.fonts[1]);
    parent.translate(0, parent.textAscent() + parent.textDescent());
    parent.text("Punkte: " + bluePoints, 0, 0);

    // Aktueller Zug
    parent.translate(0, parent.textAscent() + parent.textDescent());
    if (currentColor == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
    } else if (currentColor == PlayerColor.BLUE) {
      parent.fill(GuiConstants.colorBlue);
    } else {
      parent.fill(GuiConstants.colorBlack);
    }

    parent.textSize(GuiConstants.fontSizes[3]);
    parent.textFont(GuiConstants.fonts[3]);
    for (Action action : parent.getCurrentActions()) {
      if (action != null) {
        parent.translate(0, parent.textAscent() + parent.textDescent());
        parent.text(action.toString(), 0, 0);
      }
    }

    // Debug Ausgabe
    if (RenderConfiguration.optionDebug) {
      parent.translate(0, parent.textAscent() + parent.textDescent());
      parent.textFont(GuiConstants.fonts[1]);
      parent.textSize(GuiConstants.fontSizes[1]);
      parent.fill(GuiConstants.colorBlack);
      parent.text(parent.frameRate, 0, 0);
      for (DebugHint hint : parent.getCurrentHints()) {
          parent.translate(0, parent.textAscent() + parent.textDescent());
          parent.text(hint.getContent(), 0, 0);
      }
    }

    parent.popMatrix();

    parent.popStyle();
  }

}
