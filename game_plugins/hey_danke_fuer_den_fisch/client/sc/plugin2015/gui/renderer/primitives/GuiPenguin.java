package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.*;
import sc.plugin2015.PlayerColor;

public class GuiPenguin extends PrimitiveBase {

	private float x, y;
	private float width;
	private PlayerColor owner;
	public PImage img;
	
	public GuiPenguin(PApplet parent, float posX, float posY, float width, PlayerColor owner) {
		super(parent);
		img = parent.loadImage("resource/game/Tux.png");
		setX(posX);
		setY(posY);
		setWidth(width);
		this.owner = owner;
	}
	

	@Override
	public void draw() {
		parent.pushStyle();
		parent.pushMatrix();
		
		parent.image(img, 0, 0);
		
		parent.popMatrix();
		parent.popStyle();

	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public PlayerColor getOwner() {
		return owner;
	}

}
