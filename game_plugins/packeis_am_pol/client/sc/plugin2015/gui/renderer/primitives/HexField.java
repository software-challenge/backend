package sc.plugin2015.gui.renderer.primitives;

import processing.core.PApplet;
import sc.plugin2015.Field;

/**
 * Hexagon Primitve for explanation see
 * http://grantmuller.com/drawing-a-hexagon-in-processing-java/
 * 
 * @author felix
 * 
 */
public class HexField extends PrimitiveBase{
	// Fields
	private PApplet parent;
	private float x, y;
	private float a, b, c;

	private int numFish = 0;

	public HexField(PApplet parent, float startX, float startY, float width) {
		super(parent);
		setX(startX);
		setY(startY);
		calcSize(width);
	}

	public void update(Field field) {
		numFish = field.getFish();
	}

	public void draw() {
		parent.noStroke();
		parent.fill(2, 6, 200);

		parent.pushMatrix();
		parent.translate(getX(), getY());

		parent.beginShape();
		parent.vertex(0, a);
		parent.vertex(b, 0);
		parent.vertex(2 * b, a);
		parent.vertex(2 * b, a + c);
		parent.vertex(b, 2 * a + c);
		parent.vertex(0, a + c);
		parent.vertex(0, a);
		parent.endShape();

		parent.popMatrix();
	}

	private void calcSize(float width) {
		b = width / 2;
		c = b / PApplet.cos(PApplet.radians(30));
		a = b * PApplet.sin(PApplet.radians(30));
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

}
