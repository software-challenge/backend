package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;

public class Background {
	
	PApplet parent;

	public Background(PApplet parent) {
		this.parent = parent;
	}
	
	public void draw(){
		parent.background(0);
	}

}
