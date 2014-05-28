package sc.plugin2015.gui.renderer.primitives;

import java.awt.Dimension;

import processing.core.PApplet;
import sc.plugin2015.Board;
import sc.plugin2015.util.Constants;
import sc.plugin2015.gui.renderer.primitives.GuiConstants;

public class GuiBoard extends PrimitiveBase{

	private Board currentBoard;
	private HexField[][] hexFields;

	public GuiBoard(PApplet parent) {
		super(parent);
		// die größe eines Hexfield wird anhand der freien Fläche innerhalb des Spielfeldes berechnet
		System.out.println("parent breite = " + parent.width);
		System.out.println("parent hoehe = " + parent.height);
		System.out.println("x = "+ GuiConstants.SIDE_BAR_START_X);
		float xDimension =parent.width*GuiConstants.SIDE_BAR_START_X;
		System.out.println("xDimension ist =" + xDimension);
		float yDimension = parent.height + GuiConstants.PROGRESS_BAR_START_Y;
		System.out.println("yDimension ist =" + yDimension);
		Dimension test = new Dimension((int)xDimension, (int)yDimension);
		
		int hexFieldSize = calcHexFieldSize(test);

		hexFields = new HexField[Constants.ROWS][Constants.COLUMNS];
		float startX = (xDimension- (8 * hexFieldSize) ) / 2;
		System.out.println("startX ist =" + startX);
		float startY = (yDimension - 8* hexFieldSize) / 2;
		System.out.println("startY ist =" + startY);
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
					hexFields[i][j] = new HexField(this.parent, (int) x, (int) y,
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
