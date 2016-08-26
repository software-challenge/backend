package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.FieldType;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class CircularGuiButton extends GuiButton {

  public CircularGuiButton(FrameRenderer parent, String message) {
    super(parent, message);
  }

  @Override
  public void resize(int newWidth) {
    width = newWidth;
    height = newWidth;
  }

  @Override
  public boolean isClicked() {
    if(message == "Fertig") {
      // would stop termoination if game
//      if(parent.currentGameState.getCurrentPlayer().getMovement() != 0) {
//        return false;
//      }
    } else if(message == "-") {
      if(parent.currentGameState.getCurrentPlayer().getSpeed() == 1 ||
          parent.currentGameState.getCurrentPlayer().getMovement() == 0 ||
          parent.currentGameState.getCurrentPlayer().getField(
              parent.currentGameState.getBoard()).getType() == FieldType.SANDBANK ||
          parent.currentGameState.getCurrentPlayer().getFreeAcc() +
          parent.currentGameState.getCurrentPlayer().getCoal() == 0) {
        return false;
      }
    } else if(message == "+") {
      if(parent.currentGameState.getCurrentPlayer().getSpeed() == 6 ||
          parent.currentGameState.getCurrentPlayer().getField(
              parent.currentGameState.getBoard()).getType() == FieldType.SANDBANK ||
          parent.currentGameState.getCurrentPlayer().getFreeAcc() +
          parent.currentGameState.getCurrentPlayer().getCoal() == 0) {
        return false;
      }
    } else if(message == "Links") {
      if(parent.currentGameState.getCurrentPlayer().getFreeTurns() +
          parent.currentGameState.getCurrentPlayer().getCoal() == 0 ||
          parent.currentGameState.getCurrentPlayer().getField(
              parent.currentGameState.getBoard()).getType() == FieldType.SANDBANK) {
        return false;
      }
    } else if(message == "Rechts") {
      if(parent.currentGameState.getCurrentPlayer().getFreeTurns() +
          parent.currentGameState.getCurrentPlayer().getCoal() == 0 ||
          parent.currentGameState.getCurrentPlayer().getField(
              parent.currentGameState.getBoard()).getType() == FieldType.SANDBANK) {
        return false;
      }
    }
    return mouseHover();
  }

  @Override
  public boolean mouseHover() {
    if(parent.getMousePosition() != null) {
      long distance = Math.round(Math.sqrt(Math.pow(parent.mouseX - x, 2) + Math.pow(parent.mouseY - y, 2)));
      System.out.println(String.format("(%d, %d): Distance to %s is %d", parent.mouseX, parent.mouseY, message, distance));
      return distance < width / 2; // width is the diameter of the circle
    } else {
      return false;
    }
  }
}
