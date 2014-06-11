package sc.plugin2015.gui.renderer.primitives;

import sc.plugin2015.gui.renderer.FrameRenderer;

/**
 * Zeichnet den "Rahmen" um das Spielfeld. Rot/Blau wenn der betreffende Spieler
 * an der Reihe ist, sonst Grau.
 * 
 * @author felix
 * 
 */
public class BoardFrame extends PrimitiveBase {

	public BoardFrame(FrameRenderer parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
		parent.pushStyle();
		//TODO Farbe in abhängigkeit des aktuellen Spielers setzen.
		parent.noStroke();
		parent.fill(GuiConstants.colorGrey);
		
		parent.rect(0, 0, parent.getWidth(), GuiConstants.frameBorderSize);
		parent.rect(0, 0, GuiConstants.frameBorderSize, parent.getHeight());
		parent.rect(parent.getWidth() - GuiConstants.frameBorderSize, 0, GuiConstants.frameBorderSize, parent.getHeight());
		parent.rect(0,parent.getHeight() - GuiConstants.frameBorderSize, parent.getWidth(), GuiConstants.frameBorderSize);
		
		parent.popStyle();
	}

}
