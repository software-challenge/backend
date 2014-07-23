package sc.plugin2015.gui.renderer.primitives;

import sc.plugin2015.Player;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.gui.renderer.FrameRenderer;

public class GuiButton extends PrimitiveBase {
	
	

	public GuiButton(FrameRenderer parent) {
		super(parent);
	}

	@Override
	public void draw() {
		if(parent != null && parent.currentGameState != null && parent.currentGameState.getTurn() >= 8){
			parent.pushMatrix();
			int x = (int) (parent.getWidth() / 2f - 50);
			int y = (int)(parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
			parent.translate(parent.getWidth() / 2f - 50, parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
			// draw stroke
			if(parent.currentGameState.getCurrentPlayerColor() == PlayerColor.BLUE)
				parent.stroke(GuiConstants.colorBlue);
			else if(parent.currentGameState.getCurrentPlayerColor() == PlayerColor.RED)
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
			parent.text("Aussetzen", 6, 0);
			parent.popMatrix();
		}
		
		
		

	}

}
