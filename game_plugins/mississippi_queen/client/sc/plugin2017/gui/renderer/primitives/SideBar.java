package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.Action;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;
import sc.plugin2017.gui.renderer.RenderConfiguration;

/**
 * Zeichnet Spielerinformationen (Punkte) aktueller Zug
 * des Spieles.
 */
// TODO find out why dispalyName == null
public class SideBar extends PrimitiveBase {

  public SideBar(FrameRenderer parent) {
    super(parent);
    this.parent = parent;
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

    if (parent.currentGameState != null
        && parent.currentGameState.getCurrentPlayerColor() == PlayerColor.RED)
      parent.fill(GuiConstants.colorRed);
    else
      parent.fill(GuiConstants.colorBlack);

    String redName = "Spieler1";
    int redPoints = 0;
//    int redSpeed = 1;
//    int redCoal = 6;
    int redMovement = 1;
    if (parent.currentGameState != null) {
      redName = parent.currentGameState.getRedPlayer().getDisplayName();
      redPoints = parent.currentGameState.getRedPlayer().getPoints();
//      redSpeed = parent.currentGameState.getRedPlayer().getSpeed();
      redMovement = parent.currentGameState.getRedPlayer().getMovement();
//      redCoal = parent.currentGameState.getRedPlayer().getCoal();
    }
    parent.translate(20, parent.textAscent() + 20);
    // passe Textgröße an
    int  preferredTextSize = 25;
    if(parent != null && parent.currentGameState != null && redName != null) {
      preferredTextSize = (int) (30f / parent.textWidth(redName) * (parent.getWidth() * GuiConstants.SIDE_BAR_WIDTH - 25));
    }
    if (!(preferredTextSize > 30)) {
      parent.textSize(preferredTextSize);
    }
    
    if(redName != null) {
      parent.text(redName, 0, 0);
    }
    
    // Punkte + Kohle + Geschwindigkeit
    parent.textFont(GuiConstants.fonts[1]);
    parent.textSize(GuiConstants.fontSizes[1]);


    parent.translate(0, parent.textAscent() + parent.textDescent());
    if (parent.currentGameState != null && !parent.currentGameState.gameEnded())
      parent.text("Punkte: " + redPoints, 0, 0);
    else
      parent.text("Punkte: " + redPoints, 0, 0);
    // Geschwindigkeit und Bewegungspunkte
    parent.translate(0, parent.textAscent() + parent.textDescent());
    parent.text("Züge: " + redMovement, 0, 0);
//    parent.translate(0, parent.textAscent() + parent.textDescent());
//    parent.text("Kohle: " + redCoal, 0, 0);
    
    // Blauer Spieler.
    parent.textFont(GuiConstants.fonts[2]);
    parent.textSize(GuiConstants.fontSizes[2]);

    parent.translate(0, parent.textAscent() + parent.textDescent());
    if (parent.currentGameState != null
        && parent.currentGameState.getCurrentPlayerColor() == PlayerColor.BLUE)
      parent.fill(GuiConstants.colorBlue);
    else
      parent.fill(GuiConstants.colorBlack);
    String blueName = "Spieler2";
    int bluePoints = 0;
//    int blueSpeed = 1;
    int blueMovement = 1;
//    int blueCoal = 6;
    if (parent.currentGameState != null) {
      blueName = parent.currentGameState.getBluePlayer().getDisplayName();
      bluePoints = parent.currentGameState.getBluePlayer().getPoints();
//      blueSpeed = parent.currentGameState.getBluePlayer().getSpeed();
      blueMovement = parent.currentGameState.getBluePlayer().getMovement();
//      blueCoal = parent.currentGameState.getBluePlayer().getCoal();
    }

    // passe Textgröße an
    if(blueName != null) {
      preferredTextSize = (int) (30f / parent.textWidth(blueName) * (parent
        .getWidth() * GuiConstants.SIDE_BAR_WIDTH - 25));
    }
    if (!(preferredTextSize > 30))
      parent.textSize(preferredTextSize);

    if(blueName != null) {
      parent.text(blueName, 0, 0);
    }
    // Punkte
    parent.textSize(GuiConstants.fontSizes[1]);
    parent.textFont(GuiConstants.fonts[1]);
    parent.translate(0, parent.textAscent() + parent.textDescent());
    if (parent.currentGameState != null && !parent.currentGameState.gameEnded())
      parent.text("Punkte: " + bluePoints, 0, 0);
    else
      parent.text("Punkte: " + bluePoints, 0, 0);
 // Geschwindigkeit und Bewegungspunkte
    parent.translate(0, parent.textAscent() + parent.textDescent());
    parent.text("Züge: "  + blueMovement, 0, 0);
//    parent.translate(0, parent.textAscent() + parent.textDescent());
//    parent.text("Kohle: " + blueCoal, 0, 0);
    // Aktueller Zug
    parent.translate(0, parent.textAscent() + parent.textDescent());
    if (parent.currentGameState != null
        && parent.currentGameState.getCurrentPlayerColor() == PlayerColor.RED)
      parent.fill(GuiConstants.colorRed);
    else
      parent.fill(GuiConstants.colorBlue);
    
    parent.textSize(GuiConstants.fontSizes[3]);
    parent.textFont(GuiConstants.fonts[3]);
    if(parent.currentMove != null && parent.currentMove.actions != null)
    for (Action action : parent.currentMove.actions) {
      if(action != null) {
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
      if (parent.currentGameState != null
          && parent.currentGameState.getLastMove() != null)
        for (int i = 0; i < parent.currentGameState.getLastMove().getHints()
            .size(); i++) {
          parent.translate(0, parent.textAscent() + parent.textDescent());
          parent.text(parent.currentGameState.getLastMove().getHints().get(i)
              .getContent(), 0, 0);
        }
    }

    parent.popMatrix();

    parent.popStyle();
  }

}
