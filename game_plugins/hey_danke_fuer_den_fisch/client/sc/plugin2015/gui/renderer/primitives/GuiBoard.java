package sc.plugin2015.gui.renderer.primitives;

import java.awt.Dimension;

import sc.plugin2015.Board;
import sc.plugin2015.util.Constants;
import sc.plugin2015.gui.renderer.FrameRenderer;
import sc.plugin2015.gui.renderer.primitives.GuiConstants;

public class GuiBoard extends PrimitiveBase {

	private Board currentBoard;
	private HexField[][] hexFields;

	public GuiBoard(FrameRenderer parent) {
		super(parent);
		setHexFields(new HexField[Constants.ROWS][Constants.COLUMNS]);

		float xDimension = parent.width * GuiConstants.GUI_BOARD_WIDTH;

		float yDimension = parent.height + GuiConstants.GUI_BOARD_HEIGHT;

		Dimension test = new Dimension((int) xDimension, (int) yDimension);

		int hexFieldSize = calcHexFieldSize(test);

		float startX = (xDimension - (8 * hexFieldSize)) / 2;

		float startY = (yDimension - 8 * hexFieldSize) / 2;

		float y = startY;

		for (int i = 0; i < Constants.ROWS; i++) {
			float x;
			if (i % 2 == 0) {
				// even rows
				x = startX + hexFieldSize / 2;
			} else {
				// odd rows
				x = startX;
			}
			for (int j = 0; j < Constants.COLUMNS; j++) {
				if (!(i % 2 == 0 && j == 7)) {

					getHexFields()[i][j] = new HexField(this.parent, (int) x,
							(int) y, hexFieldSize, j, i);
					x = x + hexFieldSize + 2;
				}
			}

			y = y + (hexFieldSize - getHexFields()[0][0].getA())
					+ parent.getHeight() * GuiConstants.HEX_FIELD_GAP_SIZE;

		}

		// draw();
	}

	public void calculateSize(int width, int height) {

		// die größe eines Hexfield wird anhand der freien Fläche innerhalb des
		// Spielfeldes berechnet

		float xDimension = width * GuiConstants.GUI_BOARD_WIDTH;

		float yDimension = height + GuiConstants.GUI_BOARD_HEIGHT;

		Dimension test = new Dimension((int) xDimension, (int) yDimension);

		int hexFieldSize = calcHexFieldSize(test);

		float startX = (xDimension - (8 * hexFieldSize)) / 2;

		float startY = (yDimension - 8 * hexFieldSize) / 2;

		float y = startY;

		for (int i = 0; i < Constants.ROWS; i++) {
			float x;
			if (i % 2 == 0) {
				// even rows
				x = startX + hexFieldSize / 2;
			} else {
				// odd rows
				x = startX;
			}
			for (int j = 0; j < Constants.COLUMNS; j++) {
				if (!(i % 2 == 0 && j == 7)) {
					getHexFields()[i][j].resize((int) x, (int) y, hexFieldSize);

					x = x + hexFieldSize + 2;
				}
			}

			y = y + (hexFieldSize - getHexFields()[0][0].getA()) + height
					* GuiConstants.HEX_FIELD_GAP_SIZE;

		}
	}

	public void resize(int width, int height) {
		calculateSize(width, height);
		// draw();
	}

	public void update(Board board) {
		currentBoard = board;
		for (int i = 0; i < Constants.ROWS; i++) {
			for (int j = 0; j < Constants.COLUMNS; j++) {
				if (!(i % 2 == 0 && j == 7)) {
					getHexFields()[i][j].update(board.getField(j, i));
				}
			}
		}
	}

	public void draw() {

		for (int i = 0; i < Constants.ROWS; i++) {
			for (int j = 0; j < Constants.COLUMNS; j++) {
				if (!(i % 2 == 0 && j == 7)) {
					getHexFields()[i][j].draw();
				}
			}
		}
	}

	private int calcHexFieldSize(Dimension dim) {
		int hexFieldWidth = dim.width / 8;
		int hexFieldHeight = dim.height / 8;
		return Math.min(hexFieldWidth, hexFieldHeight);
	}

	/**
	 * @return the hexFields
	 */
	public HexField[][] getHexFields() {
		return hexFields;
	}

	/**
	 * @param hexFields
	 *            the hexFields to set
	 */
	public void setHexFields(HexField[][] hexFields) {
		this.hexFields = hexFields;
	}

	public void highlightHexField(int x, int y) {
		this.hexFields[x][y].setHighlighted(true);
	}

	public void unHighlightAll() {
		for (int i = 0; i < Constants.ROWS; i++) {
			for (int j = 0; j < Constants.COLUMNS; j++) {
				if (!(i % 2 == 0 && j == 7)) {
					getHexFields()[i][j].setHighlighted(false);
				}
			}
		}
	}

}
