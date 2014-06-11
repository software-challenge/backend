package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;
import sc.plugin2015.Field;
import sc.plugin2015.gui.renderer.FrameRenderer;

/**
 * Hexagon Primitve for explanation see
 * http://grantmuller.com/drawing-a-hexagon-in-processing-java/
 * 
 * @author felix
 * 
 */
public class HexField extends PrimitiveBase{
	// Fields
	private float x, y;
	private float a, b, c;
	/**
	 * x position des Feldes innerhalb des Spielefeld arrays
	 */
	private int fieldX;
	/**
	 * y position des Feldes innerhalb des Spielefeld arrays
	 */
	private int fieldY;

	private int numFish = 0;

	public HexField(FrameRenderer parent, float startX, float startY, float width, int fieldX, int fieldY) {
		super(parent);
		setX(startX);
		setY(startY);
		calcSize(width);
		setFieldX(fieldX);
		setFieldY(fieldY);
	}

	public void update(Field field) {
		numFish = field.getFish();
	}

	public void draw() {
		if(numFish != 0) {
			parent.pushStyle();
			parent.noStroke();
			parent.fill(GuiConstants.colorHexFields);
	
			parent.pushMatrix();
			parent.translate(getX(), getY());
	
			parent.beginShape();
			parent.vertex(0, a);
			parent.vertex(b, 0);
			parent.vertex(2 * b, a);
			parent.vertex(2 * b, a + getC());
			parent.vertex(b, 2 * a + getC());
			parent.vertex(0, a + getC());
			parent.vertex(0, a);
			parent.endShape();
			parent.fill(0);
			parent.text("" + this.fieldX + " " + this.fieldY, 25, 25);
			parent.text("" + numFish, 25, 50);
			parent.popMatrix();
			parent.popStyle();
		}
	}

	private void calcSize(float width) {
		b = width / 2;
		setC(b / PApplet.cos(PApplet.radians(30)));
		a = b * PApplet.sin(PApplet.radians(30));
	}
	
	public void resize(float startX, float startY, float width){
		setX(startX);
		setY(startY);
		calcSize(width);
		
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

	public float getA() {
		return this.a;
	}

	public float getB() {
		return this.b;
	}

	public int getFieldX() {
		return fieldX;
	}

	public void setFieldX(int fieldX) {
		this.fieldX = fieldX;
	}

	public int getFieldY() {
		return fieldY;
	}

	public void setFieldY(int fieldY) {
		this.fieldY = fieldY;
	}

	/**
	 * @return the c
	 */
	public float getC() {
		return c;
	}

	/**
	 * @param c the c to set
	 */
	private void setC(float c) {
		this.c = c;
	}

}
