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
		hexFields = new HexField[Constants.ROWS][Constants.COLUMNS];
		
		float xDimension =parent.width*GuiConstants.GUI_BOARD_WIDHT;
		
		float yDimension = parent.height + GuiConstants.GUI_BOARD_HEIGHT;
		
		Dimension test = new Dimension((int)xDimension, (int)yDimension);
		
		int hexFieldSize = calcHexFieldSize(test);

		
		float startX = (xDimension- (8 * hexFieldSize) ) / 2;
		
		float startY = (yDimension - 8* hexFieldSize) / 2;
		
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
							hexFieldSize, j, i);
					x = x + hexFieldSize + 2;
				}
			}
			
			y = y + (hexFieldSize - hexFields[0][0].getA()) + GuiConstants.HEX_FIELD_GAP_SIZE;

		}
		
		draw();
	}
	
	public void calculateSize(){
		
		// die größe eines Hexfield wird anhand der freien Fläche innerhalb des Spielfeldes berechnet
		
		float xDimension =parent.width*GuiConstants.GUI_BOARD_WIDHT;
		
		float yDimension = parent.height + GuiConstants.GUI_BOARD_HEIGHT;
		
		Dimension test = new Dimension((int)xDimension, (int)yDimension);
		
		int hexFieldSize = calcHexFieldSize(test);

		
		float startX = (xDimension- (8 * hexFieldSize) ) / 2;
		
		float startY = (yDimension - 8* hexFieldSize) / 2;
		
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
					hexFields[i][j].resize((int) x, (int) y, hexFieldSize);
					
					x = x + hexFieldSize + 2;
				}
			}

			y = y + (hexFieldSize - hexFields[0][0].getA()) + GuiConstants.HEX_FIELD_GAP_SIZE;

		}
	}
	
	public void resize(){
		calculateSize();
		//draw();
	}

	public void update(Board board) {
		currentBoard = board;
		for(int i = 0; i < Constants.ROWS; i++) {
			for(int j = 0; j < Constants.COLUMNS; j++) {
				if (!(i % 2 == 0 && j == 7)) {
					hexFields[i][j].update(board.getField(j, i));
				}
			}	
		}
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
