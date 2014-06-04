package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;

/**
 * Zeichnet den Spielverlauf. Rundenanzahl + Bar
 * 
 * @author felix
 * 
 */
public class ProgressBar extends PrimitiveBase {

	public ProgressBar(PApplet par) {
		super(par);
	}

	@Override
	public void draw() {
		parent.pushStyle();


		// Umrandung
		parent.pushMatrix();
		parent.translate(0, parent.getHeight() * GuiConstants.SIDE_BAR_HEIGHT);
		parent.fill(GuiConstants.colorSideBarBG);
		parent.rect(0, 0, parent.getWidth(),
				parent.getHeight() - parent.getHeight()
						* GuiConstants.SIDE_BAR_HEIGHT);

		parent.popMatrix();

		// Statusbalken
		parent.pushMatrix();
		
		parent.stroke(1.0f); // Umrandung
		parent.fill(GuiConstants.colorDarkGrey); // Filling
		
		parent.translate(50, parent.getHeight() - 30);
		parent.rect(0, 0, parent.getWidth() - 60, 20, 7);
		parent.popMatrix();

		parent.popStyle();
	}

}
