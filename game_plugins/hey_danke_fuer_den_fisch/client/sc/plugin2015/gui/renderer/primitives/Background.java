package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PImage;

public class Background {
	
	PApplet parent;
	PImage rawImage;
	PImage img;

	public Background(PApplet parent) {
		this.parent = parent;
		rawImage = parent.loadImage(GuiConstants.BACKGROUND_IMAGE);
	}
	
	public void draw(){
		parent.background(GuiConstants.colorBackGround);
		parent.image(img, 0, 0);
	}
	
	public void resize(){
		try {
			img = (PImage) rawImage.clone();
		} catch (CloneNotSupportedException e) {
			img = new PImage();
		}
		img.resize(parent.getWidth(), parent.getHeight());
	}

}
