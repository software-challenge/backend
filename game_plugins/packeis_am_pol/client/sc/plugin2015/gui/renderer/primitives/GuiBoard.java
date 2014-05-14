package sc.plugin2015.gui.renderer.primitives;

import java.awt.Dimension;

import processing.core.PApplet;
import sc.plugin2015.Board;
import sc.plugin2015.util.Constants;

public class GuiBoard {

	private PApplet parent;
	private Board currentBoard;
	private HexField[][] hexFields;

	public GuiBoard(PApplet parent) {
		this.parent = parent;

		int hexFieldSize = calcHexFieldSize(parent.getSize());

		hexFields = new HexField[Constants.ROWS][Constants.COLUMNS];
		float startX = (parent.width- (8 * hexFieldSize)) / 2;
		float startY = (parent.height - 8* hexFieldSize) / 2;
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
					hexFields[i][j] = new HexField(parent, (int) x, (int) y,
							hexFieldSize);
					x = x + hexFieldSize + 2;
				}
			}

			y = y + (hexFieldSize - hexFields[0][0].getA()) + 8;

		}
	}

	public void update(Board board) {
		currentBoard = board;
	}

	public void draw() {

		for (int i = 0; i < Constants.ROWS; i++) {
			for (int j = 0; j < Constants.COLUMNS; j++) {
				if (!(i % 2 == 0 && j == 7)) {
					hexFields[i][j].draw();
				}
			}
		}
	}

	private int calcHexFieldSize(Dimension dim) {
		int hexFieldWidth = dim.width / 8;
		int hexFieldHeight = dim.height / 8;
		return Math.min(hexFieldWidth, hexFieldHeight);
	}

}
