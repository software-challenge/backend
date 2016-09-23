package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.PlayerColor;
import sc.plugin2017.WinCondition;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GameEndedDialog {

  // TODO don't give draw access to while gamestate, only pass what is needed and put it into an update function
  public static void draw(FrameRenderer parent, WinCondition condition, String winningPlayerName) {
    parent.pushStyle();
    parent.pushMatrix();
    parent.textSize(32);
    // Grey out Game Area
    parent.fill(GuiConstants.colorGreyOut);
    parent.rect(0, 0, parent.getWidth(), parent.getHeight());
    // message für winningreason
    String winningReason = "";
    if (condition.reason != null) {
      winningReason = condition.reason;
    }
    // message für endefenster
    String msg = "Das Spiel ist zu Ende!";
    String message = "Das Spiel ging unendschieden aus!";
    message = winningPlayerName + " hat gewonnen!";
    // Box Groß
    float x = Math.max(parent.textWidth(winningReason), parent.textWidth(message)) + 10;
    float y;
    if (winningReason.contains("\n")) {
      y = parent.getHeight() * GuiConstants.GAME_ENDED_SIZE * 1.25f;
    } else {
      y = parent.getHeight() * GuiConstants.GAME_ENDED_SIZE;
    }

    parent.fill(GuiConstants.colorSideBarBG);
    parent.translate((parent.getWidth() - x) / 2, y);
    parent.rect(0, 0, x, y, 7);

    // Box klein
    parent.fill(GuiConstants.colorHexFields);
    parent.rect(0, 0, x, parent.textAscent() + 2 * parent.textDescent(), 7, 7, 0, 0);

    // ##Text
    // #Game Ended
    parent.pushMatrix();
    parent.fill(GuiConstants.colorText);

    parent.translate((x - parent.textWidth(msg)) / 2, parent.textAscent() + parent.textDescent()); // mittig
                                                                                                   // positionieren
    parent.text(msg, 0, 0);
    parent.popMatrix();

    // # Winner

    parent.pushMatrix();
    parent.pushStyle();
    if (condition.winner == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
    } else if (condition.winner == PlayerColor.BLUE) {
      parent.fill(GuiConstants.colorBlue);
    }
    parent.translate((x - parent.textWidth(message)) / 2, 3 * parent.textAscent() + parent.textDescent()); // mittig
                                                                                                           // positionieren
    parent.text(message, 0, 0);
    parent.popStyle();
    parent.popMatrix();
    // # Winning Reason
    parent.pushMatrix();

    parent.textSize(32);
    parent.translate((x - parent.textWidth(winningReason)) / 2, 5 * parent.textAscent() + parent.textDescent());

    parent.text(winningReason, 0, 0);

    parent.popMatrix();

    parent.popMatrix();
    parent.popStyle();
  }

}
