package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;
import sc.plugin2015.PlayerColor;

public class GuiPenguin extends PrimitiveBase {

	private float x, y;
	private float width;
	private PlayerColor owner;
	
	public GuiPenguin(PApplet parent, float posX, float posY, float width, PlayerColor owner) {
		super(parent);
		setX(posX);
		setY(posY);
		setWidth(width);
		this.owner = owner;
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub

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
