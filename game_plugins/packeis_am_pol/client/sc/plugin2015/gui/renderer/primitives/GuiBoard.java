package sc.plugin2015.gui.renderer.primitives;

import java.awt.Dimension;

import javax.swing.text.html.MinimalHTMLWriter;

import processing.core.PApplet;
import sc.plugin2015.Board;
import sc.plugin2015.util.Constants;

public class GuiBoard {

	private PApplet parent;
	private Board currentBoard;
	private HexField[][] hexFields;

	public GuiBoard(PApplet parent, Dimension dim) {
		this.parent = parent;

		int hexFieldSize = calcHexFieldSize(dim);

		hexFields = new HexField[Constants.ROWS][Constants.COLUMNS];
		float x = (dim.width-(8 * hexFieldSize)) / 2  +(hexFieldSize / 2);
		float y = (dim.height - 8* hexFieldSize) / 2;

		for (int i = 0; i < Constants.ROWS; i++) {
			if (i % 2 == 0) {
				// even rows
				x = hexFieldSize / 2;
			} else {
				// odd rows
				x = 0;
			}
			for (int j = 0; j < Constants.COLUMNS; j++) {
				if (!(i % 2 == 0 && j == 7)) {
					hexFields[i][j] = new HexField(parent, (int) x, (int) y,
							hexFieldSize);
					x = x + hexFieldSize + 10;
				}
			}

			y = y + (hexFieldSize - hexFields[0][0].getA()) + 10;

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
