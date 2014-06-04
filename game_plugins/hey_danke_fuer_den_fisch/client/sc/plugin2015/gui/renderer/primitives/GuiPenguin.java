package sc.plugin2015.gui.renderer.primitives;

import java.awt.Dimension;

import processing.core.*;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.util.Constants;

public class GuiPenguin extends PrimitiveBase {

	private float x, y;
	private float width;
	private float height;
	private PlayerColor owner;
	private int fieldX;
	private int fieldY;
	private PImage penguinImg;
	
	public GuiPenguin(PApplet parent, int fieldPosX, int fieldPosY, PlayerColor owner) {
		super(parent);
		if(owner == PlayerColor.RED) {
			penguinImg = parent.loadImage(GuiConstants.RED_PENGUIN_IMAGE);
		} else {
			penguinImg = parent.loadImage(GuiConstants.BLUE_PENGUIN_IMAGE);
		}
		setFieldX(fieldPosX);
		setFieldY(fieldPosY);
		
		setX((float) (parent.getWidth() * (GuiConstants.SIDE_BAR_START_X - (0.05 * (getFieldX() + 1)))));
		setY(owner == PlayerColor.RED ? 50 : 150);
		setWidth((float) (parent.getWidth() * 0.05));
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
	
	public void resize() {
		if(getFieldX() < 0) {
			setX((float) (parent.getWidth() * (GuiConstants.SIDE_BAR_START_X - (0.05 * (getFieldX() + 1)))));
			setWidth((float) (parent.getWidth() * 0.05));
			setHeight(width / 200 * 232);
		} else {
			
			
			float xDimension = parent.width * GuiConstants.GUI_BOARD_WIDHT;
			
			float yDimension = parent.height + GuiConstants.GUI_BOARD_HEIGHT;
			
			Dimension dim = new Dimension((int) xDimension, (int) yDimension);
			
			int penguinSize = calcPenguinSize(dim);

			
			float startX = (xDimension- (8 * penguinSize) ) / 2;
			
			float startY = (yDimension - 8* penguinSize) / 2;
			

			int i = getFieldX();
			int j = getFieldY();
			float x;
			if (j % 2 == 0) {
				// even rows
				x = startX + penguinSize / 2 + penguinSize * j;
			} else {
				// odd rows
				x = startX + penguinSize * j;
			}
			float y = startY + penguinSize * i;
			
			setX(x + penguinSize * 0.23f);
			setY(y - penguinSize * 0.1f);
			setWidth(penguinSize * 0.7f);
			setHeight(getWidth() / 200 * 232);
		}
	}
	
	private int calcPenguinSize(Dimension dim) {
		int PenguinWidth = dim.width / 8;
		int PenguinHeight = dim.height / 8;
		return Math.min(PenguinWidth, PenguinHeight);
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


	/**
	 * @return the fieldX
	 */
	private int getFieldX() {
		return fieldX;
	}


	/**
	 * @param fieldX the fieldX to set
	 */
	private void setFieldX(int fieldX) {
		this.fieldX = fieldX;
	}


	/**
	 * @return the fieldY
	 */
	private int getFieldY() {
		return fieldY;
	}


	/**
	 * @param fieldY the fieldY to set
	 */
	private void setFieldY(int fieldY) {
		this.fieldY = fieldY;
	}

}
