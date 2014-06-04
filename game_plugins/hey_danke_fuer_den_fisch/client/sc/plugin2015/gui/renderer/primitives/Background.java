package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PImage;

public class Background {
	
	PApplet parent;
	PImage image;

	public Background(PApplet parent) {
		this.parent = parent;
		image = parent.loadImage(GuiConstants.BACKGROUND_IMAGE);
	}
	
	public void draw(){
		parent.background(GuiConstants.colorBackGround);
		parent.image(image, 0, 0);
	}

}
