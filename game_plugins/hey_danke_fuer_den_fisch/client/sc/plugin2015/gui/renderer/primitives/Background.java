package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PImage;

public class Background {
	
	PApplet parent;
	PImage rawImage;

	public Background(PApplet parent) {
		this.parent = parent;
		rawImage = parent.loadImage(GuiConstants.BACKGROUND_IMAGE);
	}
	
	public void draw(){
		parent.background(GuiConstants.colorBackGround);
		PImage img;
		try {
			img = (PImage) rawImage.clone();
		} catch (CloneNotSupportedException e) {
			img = new PImage();
		}
		img.resize(parent.getWidth(), parent.getHeight());
		parent.image(img, 0, 0);
	}

}
