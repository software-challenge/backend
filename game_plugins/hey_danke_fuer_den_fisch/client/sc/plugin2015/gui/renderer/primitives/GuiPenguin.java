package sc.plugin2015.gui.renderer.primitives;

import java.awt.Dimension;

import processing.core.*;
import sc.plugin2015.GameState;
import sc.plugin2015.MoveType;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.Move;
import sc.plugin2015.SetMove;
import sc.plugin2015.RunMove;
import sc.plugin2015.gui.renderer.FrameRenderer;
import sc.plugin2015.util.Constants;

public class GuiPenguin extends PrimitiveBase {

	private float x, y;
	private float width;
	private float height;
	private PlayerColor owner;
	private int fieldX;
	private int fieldY;
	private int oldFieldX;
	private int oldFieldY;
	private boolean isAnimated;
	private int animatedSteps;
	private boolean isAttached;
	private PImage penguinImg;

	public GuiPenguin(FrameRenderer parent, int fieldPosX, int fieldPosY,
			PlayerColor owner) {
		super(parent);
		isAttached = false;
		isAnimated = false;
		setAnimatedSteps(0);
		if (owner == PlayerColor.RED) {
			penguinImg = parent.loadImage(GuiConstants.RED_PENGUIN_IMAGE);
		} else {
			penguinImg = parent.loadImage(GuiConstants.BLUE_PENGUIN_IMAGE);
		}
		setFieldX(fieldPosX);
		setFieldY(fieldPosY);

		setWidth((float) (parent.getWidth() * 0.05));
		setHeight(width / 200 * 232);
		setX((float) (parent.getWidth() * (GuiConstants.SIDE_BAR_START_X - (0.05 * (getFieldX() + 1)))));
		setY(owner == PlayerColor.RED ? GuiConstants.SIDE_BAR_HEIGHT
				* parent.getHeight() - 2 * getHeight()
				: GuiConstants.SIDE_BAR_HEIGHT * parent.getHeight()
						- getHeight());
		this.owner = owner;
		//System.out.println("Size of penguin is: " + getX() + " - " + getY() );
	}

	@Override
	public void draw() {
		if (!isAttached) {
			if (getFieldX() < 0) {
				setX((float) (parent.getWidth() * (GuiConstants.SIDE_BAR_START_X - (0.05 * (getFieldX() + 1)))));
				
				setY(owner == PlayerColor.RED ? GuiConstants.SIDE_BAR_HEIGHT
						* parent.getHeight() - 2 * getHeight()
						: GuiConstants.SIDE_BAR_HEIGHT *  parent.getHeight()
								- getHeight());
			} else {
				float x, oldX, oldY;
				

				float xDimension =  parent.getWidth() * GuiConstants.GUI_BOARD_WIDTH;

				float yDimension =  parent.getHeight()
						+ GuiConstants.GUI_BOARD_HEIGHT;

				Dimension dim = new Dimension((int) xDimension,
						(int) yDimension);

				int penguinSize = calcPenguinSize(dim);

				float startX = (xDimension - (8 * penguinSize)) / 2;

				float startY = (yDimension - 8 * penguinSize) / 2;

				float i = getFieldX();
				float j = getFieldY();
				if (j % 2 == 0) {
					// even rows
					x = startX + penguinSize / 2 + penguinSize * i + 2 * i;
				} else {
					// odd rows
					x = startX + penguinSize * i + 2 * i;
				}
				float a = penguinSize / 2 * PApplet.sin(PApplet.radians(30));

				float y = startY
						+ (penguinSize - a)
						* j
						+ (GuiConstants.HEX_FIELD_GAP_SIZE * parent.getHeight())
						* j;
				
				if(isAnimated) {
					if(getOldFieldX() < 0) {
						oldX = (float) (parent.getWidth() * (GuiConstants.SIDE_BAR_START_X - (0.05 * (getOldFieldX() + 1))));
						oldY = owner == PlayerColor.RED ? GuiConstants.SIDE_BAR_HEIGHT
								* parent.getHeight() - 2 * getHeight()
								: GuiConstants.SIDE_BAR_HEIGHT *  parent.getHeight()
										- getHeight();
					} else {
						i = getOldFieldX();
						j = getOldFieldY();
						if (j % 2 == 0) {
							// even rows
							oldX = startX + penguinSize / 2 + penguinSize * i + 2 * i;
						} else {
							// odd rows
							oldX = startX + penguinSize * i + 2 * i;
						}
						oldY = startY
								+ (penguinSize - a)
								* j
								+ (GuiConstants.HEX_FIELD_GAP_SIZE * parent.getHeight())
								* j;
						 
					}
					float newX = (x + penguinSize * 0.175f);
					float newY = y + a * 0.1f;
					float xDistance = Math.abs(newX - oldX);
					float yDistance = Math.abs(newY - oldY);
					
					float distance = (float) Math.sqrt(xDistance * xDistance + yDistance * yDistance);
					float parentDiagonal = (float) Math.sqrt(parent.getHeight() * parent.getHeight() + parent.getWidth() * parent.getWidth());
					int steps = (int) ((distance/parentDiagonal) * 60f);
					if(steps < 12) {
						steps = 12;
					}
					
					/*if(distance > parent.getWidth() * 0.8f) {
						steps = 80;
					} else if(distance > parent.getWidth() * 0.5f) {
						steps = 50;
					} else if(distance > parent.getWidth() * 0.4f) {
						steps = 40;
					} else if(distance > parent.getWidth() * 0.25f) {
						steps = 25;
					} else if(distance > parent.getWidth() * 0.2f) {
						steps = 20;
					} else {
						steps = 10;
					}*/
					if(getAnimatedSteps() >= steps) {
						setX(newX);
						setY(newY);
						parent.numberAnimatedPenguins--;
						setAnimated(false);
						setAnimatedSteps(0);
					} else {
						setX(getX() + (newX - oldX) * (1f/(float)steps));
						setY(getY() + (newY - oldY) * (1f/(float)steps));
						setAnimatedSteps(getAnimatedSteps() + 1);
					}
					
				} else {

					setX(x + penguinSize * 0.175f);
					setY(y + a * 0.1f);
				}
			}
		} else {
			setX(parent.mouseX - this.getWidth() / 2);
			setY(parent.mouseY - this.getHeight() / 2);
		}
		
		parent.pushStyle();
		parent.pushMatrix();
		//TODO put logic into here instead of resize
		resize(parent.width,parent.height);
		parent.image(penguinImg, getX(), getY(), getWidth(), getHeight());

		parent.popMatrix();
		parent.popStyle();

	}

	public void resize(int width, int height) {
		
		if (getFieldX() < 0) {
			setWidth((float) (width * 0.05));
		} else {

			float xDimension = width * GuiConstants.GUI_BOARD_WIDTH;

			float yDimension = height
					+ GuiConstants.GUI_BOARD_HEIGHT;

			Dimension dim = new Dimension((int) xDimension,
					(int) yDimension);

			int penguinSize = calcPenguinSize(dim);
			setWidth(penguinSize * 0.7f);
			setHeight(getWidth() / 200 * 232);

		}
	
	}

	private int calcPenguinSize(Dimension dim) {
		int PenguinWidth = dim.width / 8;
		int PenguinHeight = dim.height / 8;
		return Math.min(PenguinWidth, PenguinHeight);
	}
	
	
	public void update(Move lastMove, PlayerColor lastPlayer, int turn, boolean humanPlayer) { //new update function with animation
	if (lastMove != null) {
		if (lastMove instanceof SetMove) {
			SetMove move = (SetMove) lastMove;
			if (lastPlayer == PlayerColor.RED) {
				if (getFieldX() < 0 && (-(turn / 2)) - 1 == getFieldX()) {
					if(!humanPlayer) {
						if(!isAnimated()) {
							parent.numberAnimatedPenguins++;
						}
						setAnimated(true);
						setOldFieldX(getFieldX());
						setOldFieldY(getFieldY());
					}
					setFieldX(move.getSetCoordinates()[0]);
					setFieldY(move.getSetCoordinates()[1]);
				}
			} else {
				if (getFieldX() < 0 && (-turn / 2) == getFieldX()) {
					if(!humanPlayer) {
						if(!isAnimated()) {
							parent.numberAnimatedPenguins++;
						}
						setAnimated(true);
						setOldFieldX(getFieldX());
						setOldFieldY(getFieldY());
					}
					setFieldX(move.getSetCoordinates()[0]);
					setFieldY(move.getSetCoordinates()[1]);
				}
			}
		} else if (lastMove.getMoveType() == MoveType.RUN) {
			if (lastMove instanceof RunMove) {
				RunMove move = (RunMove) lastMove;
				if (getFieldX() == move.fromX && getFieldY() == move.fromY) {
					if(!humanPlayer) {
						if(!isAnimated()) {
							parent.numberAnimatedPenguins++;
						}
						setAnimated(true);
						setOldFieldX(getFieldX());
						setOldFieldY(getFieldY());
					}
					setFieldX(move.toX);
					setFieldY(move.toY);
				}
			}
		}
	}
	//System.out.println("ein Pinguin von Spieler " + this.getOwner()
	//		+ " steht auf " + this.getFieldX() + ", " + this.getFieldY());
}

	/*public void update(Move lastMove, PlayerColor lastPlayer, int turn) { //old update function without animation
		if (lastMove != null) {
			if (lastMove.getMoveType() == MoveType.SET) {
				SetMove move = (SetMove) lastMove;
				if (lastPlayer == PlayerColor.RED) {
					if (getFieldX() < 0 && (-(turn / 2)) - 1 == getFieldX()) {
						setFieldX(move.getSetCoordinates()[0]);
						setFieldY(move.getSetCoordinates()[1]);
					}
				} else {
					if (getFieldX() < 0 && (-turn / 2) == getFieldX()) {
						setFieldX(move.getSetCoordinates()[0]);
						setFieldY(move.getSetCoordinates()[1]);
					}
				}
			} else if (lastMove.getMoveType() == MoveType.RUN) {
				if (lastMove instanceof RunMove) {
					RunMove move = (RunMove) lastMove;
					if (getFieldX() == move.fromX && getFieldY() == move.fromY) {
						setFieldX(move.toX);
						setFieldY(move.toY);
					}
				}
			}
		}
		//System.out.println("ein Pinguin von Spieler " + this.getOwner()
		//		+ " steht auf " + this.getFieldX() + ", " + this.getFieldY());
	}*/
	
	public void reposition(GameState gameState) {
		
	}

	public void attachToMouse() {
		isAttached = true;
	}

	public void releaseFromMouse() {
		try {
			Thread.sleep(20);
			//System.out.println("Slept for 20ms");
		} catch (InterruptedException e){}
		isAttached = false;
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
	public float getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
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
	public int getFieldX() {
		return fieldX;
	}

	/**
	 * @param fieldX
	 *            the fieldX to set
	 */
	public void setFieldX(int fieldX) {
		this.fieldX = fieldX;
	}

	/**
	 * @return the fieldY
	 */
	public int getFieldY() {
		return fieldY;
	}

	/**
	 * @param fieldY
	 *            the fieldY to set
	 */
	public void setFieldY(int fieldY) {
		this.fieldY = fieldY;
	}

	public boolean isAttached() {
		return this.isAttached;
	}

	/**
	 * @return the oldFieldX
	 */
	private int getOldFieldX() {
		return oldFieldX;
	}

	/**
	 * @param oldFieldX the oldFieldX to set
	 */
	private void setOldFieldX(int oldFieldX) {
		this.oldFieldX = oldFieldX;
	}

	/**
	 * @return the oldFieldY
	 */
	private int getOldFieldY() {
		return oldFieldY;
	}

	/**
	 * @param oldFieldY the oldFieldY to set
	 */
	private void setOldFieldY(int oldFieldY) {
		this.oldFieldY = oldFieldY;
	}

	/**
	 * @return the isAnimated
	 */
	public boolean isAnimated() {
		return isAnimated;
	}

	/**
	 * @param isAnimated the isAnimated to set
	 */
	private void setAnimated(boolean isAnimated) {
		this.isAnimated = isAnimated;
	}

	/**
	 * @return the animatedSteps
	 */
	private int getAnimatedSteps() {
		return animatedSteps;
	}

	/**
	 * @param animatedSteps the animatedSteps to set
	 */
	private void setAnimatedSteps(int animatedSteps) {
		this.animatedSteps = animatedSteps;
	}


}
