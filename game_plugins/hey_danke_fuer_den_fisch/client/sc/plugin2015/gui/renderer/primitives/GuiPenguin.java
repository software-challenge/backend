package sc.plugin2015.gui.renderer.primitives;

import processing.core.*;
import sc.plugin2015.PlayerColor;

public class GuiPenguin extends PrimitiveBase {

	private float x, y;
	private float width;
	private float height;
	private PlayerColor owner;
	public PImage penguinImg;
	
	public GuiPenguin(PApplet parent, float posX, float posY, float width, PlayerColor owner) {
		super(parent);
		penguinImg = parent.loadImage(GuiConstants.PENGUIN_IMAGE);
		setX(posX);
		setY(posY);
		setWidth(width);
		setHeight(width / 200 * 232);
		this.owner = owner;
	}
	

	@Override
	public void draw() {
		parent.pushStyle();
		parent.pushMatrix();
		
		parent.image(penguinImg, getX(), getY(), getWidth(), getHeight());
		
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
		setHeight(width / 200 * 232);
	}

	/**
	 * @return the height
	 */
	private float getHeight() {
		return height;
	}


	/**
	 * @param height the height to set
	 */
	private void setHeight(float height) {
		this.height = height;
	}


	public PlayerColor getOwner() {
		return owner;
	}

}
