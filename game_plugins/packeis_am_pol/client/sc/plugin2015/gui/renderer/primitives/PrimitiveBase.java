package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;

public abstract class PrimitiveBase {
	
	PApplet parent;

	public PrimitiveBase(PApplet parent) {
		this.parent = parent;
	}
	
	//public abstract void update();
	
	public abstract void draw();

}
