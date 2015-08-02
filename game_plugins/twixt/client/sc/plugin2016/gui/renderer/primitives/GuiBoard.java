package sc.plugin2016.gui.renderer.primitives;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import sc.plugin2016.Board;
import sc.plugin2016.Connection;
import sc.plugin2016.util.Constants;
import sc.plugin2016.gui.renderer.FrameRenderer;
import sc.plugin2016.gui.renderer.primitives.GuiConstants;

public class GuiBoard extends PrimitiveBase {

	private Board currentBoard;
	private GuiField[][] guiFields;
	private List<GuiConnection> guiConnections;

	public GuiBoard(FrameRenderer parent) {
		super(parent);
		setGuiFields(new GuiField[Constants.SIZE][Constants.SIZE]);
		guiConnections = new ArrayList<GuiConnection>();

		float xDimension = parent.width * GuiConstants.GUI_BOARD_WIDTH;

		float yDimension = parent.height + GuiConstants.GUI_BOARD_HEIGHT;

		Dimension test = new Dimension((int) xDimension, (int) yDimension);

		int guiFieldSize = calcGuiFieldSize(test);

		float startX = ((xDimension / (Constants.SIZE + 2)) - 5) / 2.0f + (Constants.SIZE + 2);

		float startY = ((yDimension / (Constants.SIZE + 2)) - 5) / 2.0f + (Constants.SIZE + 2);

		float y = startY;

		for (int i = 0; i < Constants.SIZE; i++) {
			float x;
				x = startX;
			for (int j = 0; j < Constants.SIZE; j++) {
				getGuiFields()[i][j] = new GuiField(this.parent, (int) x,
						(int) y, guiFieldSize, i, j);
				x = x + guiFieldSize + 5;
				
			}
			y = y + guiFieldSize + 5;
		}

		// draw();
	}

	public void calculateSize(int width, int height) {

		// die größe eines Guifield wird anhand der freien Fläche innerhalb des
		// Spielfeldes berechnet

    float xDimension = parent.width * GuiConstants.GUI_BOARD_WIDTH;

    float yDimension = parent.height + GuiConstants.GUI_BOARD_HEIGHT;

    Dimension test = new Dimension((int) xDimension, (int) yDimension);

    int guiFieldSize = calcGuiFieldSize(test);

    float startX = (xDimension - (22 * (guiFieldSize + 5))) / 2;

    float startY = (yDimension - 22 * (guiFieldSize + 5)) / 2;

    float y = startY;

    for (int i = 0; i < Constants.SIZE; i++) {
      float x;
        x = startX;
      for (int j = 0; j < Constants.SIZE; j++) {
        getGuiFields()[i][j] = new GuiField(this.parent, (int) x,
            (int) y, guiFieldSize, i, j);
        x = x + guiFieldSize + 5;
        
      }
      y = y + guiFieldSize + 5;
		}
	}

	public void resize(int width, int height) {
		calculateSize(width, height);
		for(GuiConnection c : guiConnections) {
			c.resize();
		}
	}

	public void update(Board board) {
		currentBoard = board;
		for (int i = 0; i < Constants.SIZE; i++) {
			for (int j = 0; j < Constants.SIZE; j++) {
				getGuiFields()[i][j].update(board.getField(i, j));
			}
		}
		guiConnections = new ArrayList<GuiConnection>();
		for(Connection c : board.connections) {
		  guiConnections.add(new GuiConnection(parent, c));
		}
		
		
	}

	public void draw() {

		for (int i = 0; i < Constants.SIZE; i++) {
			for (int j = 0; j < Constants.SIZE; j++) {
				if(getGuiFields() != null) {
					getGuiFields()[i][j].draw();
				}
			}
		}
		if(guiConnections != null) {
      for(GuiConnection gc : guiConnections) {
        gc.draw();
      }
		}
		
	}

	private int calcGuiFieldSize(Dimension dim) {
		int guiFieldWidth = (dim.width / (Constants.SIZE + 2)) - 5;
		int guiFieldHeight = (dim.height / (Constants.SIZE + 2)) - 5;
		return Math.min(guiFieldWidth, guiFieldHeight);
	}

	
	public GuiField[][] getGuiFields() {
		return guiFields;
	}

	
	public void setGuiFields(GuiField[][] guiFields) {
		this.guiFields = guiFields;
	}

	public void highlightGuiField(int x, int y) {
		this.guiFields[x][y].setHighlighted(true);
	}

	public void unHighlightAll() {
		for (int i = 0; i < Constants.SIZE; i++) {
			for (int j = 0; j < Constants.SIZE; j++) {
				getGuiFields()[i][j].setHighlighted(false);
			}
		}
	}

  public GuiField getField(int x, int y) {
    return this.guiFields[x][y];
  }

}
