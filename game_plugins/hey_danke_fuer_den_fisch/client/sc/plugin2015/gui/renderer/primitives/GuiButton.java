package sc.plugin2015.gui.renderer.primitives;

import sc.plugin2015.gui.renderer.FrameRenderer;

public class GuiButton extends PrimitiveBase {
	
	

	public GuiButton(FrameRenderer parent) {
		super(parent);
	}

	@Override
	public void draw() {
		parent.pushMatrix();
		parent.translate(parent.getWidth() / 2f - 50, parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5);
		parent.stroke(1.0f);
		parent.fill(GuiConstants.colorLightGrey);
		parent.rect(0, 0, 100, 25, 3);
		parent.translate(3, 20);
		parent.fill(GuiConstants.colorBlack);
		parent.textFont(GuiConstants.fonts[0]);
		parent.textSize(GuiConstants.fontSizes[0]);		
		parent.text("Aussetzen", 0, 0);
		
		
		
		
		parent.popMatrix();
	}

}
