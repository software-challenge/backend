package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GuiButton extends PrimitiveBase {
  
  String message;

  public GuiButton(FrameRenderer parent, String message) {
    super(parent);
    this.message = message;
  }

  @Override
  public void draw() {
    parent.pushMatrix();
    parent.pushStyle();
    int x; 
    
    int y; 
    if(message == "Links") {
      x = (int) (parent.getWidth() / 2f - 150);
      y = (int)(parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
    } else if(message == "Rechts"){
      x = (int) (parent.getWidth() / 2f + 100);
      y = (int)(parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
    } else {
      x = 0;
      y = 0;
    }
    parent.translate(parent.getWidth() / 2f - 50, parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
    // draw stroke
    if(parent != null && parent.currentGameState != null && parent.currentGameState.getCurrentPlayerColor() == PlayerColor.BLUE)
      parent.stroke(GuiConstants.colorBlue);
    else if(parent != null && parent.currentGameState != null && parent.currentGameState.getCurrentPlayerColor() == PlayerColor.RED)
      parent.stroke(GuiConstants.colorRed);
    else
      parent.stroke(GuiConstants.colorBlack);
    // fill rect
    parent.fill(GuiConstants.colorLightGrey);
    if(parent.getMousePosition() != null)
      if(parent.mouseX >= x
          && parent.mouseX < x + 100 
          && parent.mouseY >= y 
          && parent.mouseY <y + 25)
        parent.fill(GuiConstants.colorLightLightGrey);
    parent.rect(0, 0, 100, 25, 3);
    // draw text
    parent.translate(3, 20);
    parent.fill(GuiConstants.colorBlack);
    
    parent.textFont(GuiConstants.fonts[0]);
    parent.textSize(GuiConstants.fontSizes[0]);   
    parent.text(message, 6, 0);
    parent.popStyle();
    parent.popMatrix();
    
    
    
    

  }

}
