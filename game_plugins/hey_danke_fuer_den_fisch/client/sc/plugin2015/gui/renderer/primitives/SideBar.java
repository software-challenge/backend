package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;

/**
 * Zeichnet Spielerinformationen (Punkte, Schollen) sowie die Pinguine am Anfang
 * des Spieles.
 * 
 * @author felix
 * 
 */
public class SideBar extends PrimitiveBase{

	public SideBar(PApplet parent) {
		super(parent);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		parent.pushStyle();
		
		parent.stroke(1.0f);		// Umrandung
		parent.fill(0, 100, 0);
		
		parent.pushMatrix();
		parent.translate(parent.getWidth()- parent.getWidth()/5, 0);
		parent.rect(0, 0, parent.getWidth()/5, parent.getHeight() - parent.getHeight()/8);
		parent.popMatrix();
		
		
		parent.popStyle();
	}

}
