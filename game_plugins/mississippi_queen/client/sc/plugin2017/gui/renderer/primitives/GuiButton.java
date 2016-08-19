package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.FieldType;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GuiButton extends PrimitiveBase {
  
  String message;
  int x;
  int y;
  int width;
  int height;

  public GuiButton(FrameRenderer parent, String message) {
    super(parent);
    this.message = message;
  }

  @Override
  public void draw() {
    if(message == "Links") {
      x = (int) (parent.getWidth() / 2f - 150f);
      y = (int)(parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
      width = 100;
      height = 25;
    } else if(message == "Rechts"){
      x = (int) (parent.getWidth() / 2f - 50f);
      y = (int)(parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
      width = 100;
      height = 25;
    } else if(message == "-") {
      x = (int) (parent.getWidth() / 2f - 180f);
      y = (int)(parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
      width = 30;
      height = 25;
    } else if(message == "+") {
      x = (int) (parent.getWidth() / 2f + 50f);
      y = (int)(parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
      width = 30;
      height = 25;
    } else if(message == "Fertig") {
      x = (int) (parent.getWidth() / 2f - 260f);
      y = (int)(parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
      width = 80;
      height = 25;
    } else if(message == "Neu") {
      x = (int) (parent.getWidth() / 2f + 80f);
      y = (int)(parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
      width = 50;
      height = 25;
    } else {
      x = 0;
      y = 0;
      width = 50;
      height = 25;
    }
    parent.pushMatrix();
    parent.pushStyle();
    
    parent.translate(x, y);
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
    
    if(parent.getMousePosition() != null) {
      if(parent.mouseX >= x
          && parent.mouseX < x + width 
          && parent.mouseY >= y 
          && parent.mouseY <y + height) {
        parent.fill(GuiConstants.colorLightLightGrey);
      }
    }
    parent.rect(0, 0, width, height, 3);
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
    // TODO Auto-generated method stub
    if(parent.getMousePosition() != null) {
      if(parent.mouseX >= x
          && parent.mouseX < x + width 
          && parent.mouseY >= y 
          && parent.mouseY <y + height) {
        draw();
        return true;
      }
    }
    return false;
  }

  public String toString() {
    return message + " X: " + x + " Y: " + y + " width: " + width + " height: " + height;
  }
}
