package sc.plugin2015.gui.renderer.primitives;

import java.awt.Dimension;

import processing.core.*;
import sc.plugin2015.MoveType;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.Move;
import sc.plugin2015.SetMove;
import sc.plugin2015.RunMove;
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
			

			float i = getFieldX();
			float j = getFieldY();
			float x;
			if (j % 2 == 0) {
				// even rows
				x = startX + penguinSize / 2 + penguinSize * i + 2 * i;
			} else {
				// odd rows
				x = startX + penguinSize * i + 2 * i;
			}
			float a = penguinSize / 2 * PApplet.sin(PApplet.radians(30));
			float y = startY + (penguinSize - a) * j + GuiConstants.HEX_FIELD_GAP_SIZE * j;
			
			setX(x + penguinSize * 0.175f);
			setY(y + a * 0.1f);
			setWidth(penguinSize * 0.7f);
			setHeight(getWidth() / 200 * 232);
		}
	}
	
	private int calcPenguinSize(Dimension dim) {
		int PenguinWidth = dim.width / 8;
		int PenguinHeight = dim.height / 8;
		return Math.min(PenguinWidth, PenguinHeight);
	}
	
	public void update(Move lastMove, PlayerColor lastPlayer, int turn) {
		if(lastMove != null) {
			if(lastMove.getMoveType() == MoveType.SET) {
				System.out.println("SetMove update " + getFieldX());
				SetMove move = (SetMove) lastMove;
				if(lastPlayer == PlayerColor.RED) {
					System.out.println("Roter Spieler war dran im Zug " + turn + ", Berechnung war " + (- (turn/2) - 1));
					if(getFieldX() < 0 && (- (turn / 2)) - 1 == getFieldX()) {
						System.out.println("dieser Pinguin soll setzen " + getFieldX());
						setFieldX(move.getSetCoordinates()[0]);
						setFieldY(move.getSetCoordinates()[1]);
					}
				} else {
					System.out.println("Blauer Spieler war dran im Zug " + turn + ", Berechnung war " + (- turn/2));
					if(getFieldX() < 0 && (- turn / 2) == getFieldX()) {
						setFieldX(move.getSetCoordinates()[0]);
						setFieldY(move.getSetCoordinates()[1]);
					}
				}
			} else {
				RunMove move = (RunMove) lastMove;
				if(getFieldX() == move.fromX && getFieldY() == move.fromY) {
					setFieldX(move.toX);
					setFieldY(move.toY);
				}
			}
		}
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
