package sc.plugin2017.gui.renderer.primitives;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedList;

import sc.plugin2017.gui.renderer.primitives.GuiConstants;
import sc.plugin2017.gui.renderer.primitives.HexField;
import sc.plugin2017.util.Constants;
import sc.plugin2017.Board;
import sc.plugin2017.Field;
import sc.plugin2017.Tile;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GuiBoard extends PrimitiveBase{

  FrameRenderer parent;
  
  Board currentBoard;
  
  LinkedList<GuiTile> tiles;
  /**
   * holds the position of 0,0 relativ to parent 
   */
  public int offsetX; 
  public int offsetY;
  public Dimension dim;
  /**
   * Width of one field
   */
  public float width;
  
  /**
   * maximum fields in x direction
   */
  public int maxFieldsInX;
  /**
   * maximum fields in y direction
   */
  public int maxFieldsInY;
  
  public GuiBoard(FrameRenderer parent) {
    super(parent);
    
    tiles = new LinkedList<GuiTile>();
    for(int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
      tiles.add(new GuiTile(parent, i));
    }
    
    float xDimension = parent.width * GuiConstants.GUI_BOARD_WIDTH;

    float yDimension = parent.height * GuiConstants.PROGRESS_BAR_HEIGHT;

    dim = new Dimension((int) xDimension, (int) yDimension);
    if(parent.currentGameState != null) {
      currentBoard = parent.currentGameState.getVisibleBoard();
    }
    calcHexFieldSize();
  }

  /**
   * sets width, macFieldsInX, maxFieldsInY according to dim and currentBoard
   */
  private void calcHexFieldSize() {
    // TODO calculate offset 
    if(currentBoard != null) {
      int lowX = 500;
      int highX = -500;
      int lowY = 500;
      int highY = -500;
      for (Tile tile : currentBoard.getVisibleTiles()) {
        for (Field field : tile.fields) {
          if(lowX > field.getX()) {
            lowX = field.getX();
          }
          if(highX < field.getX()) {
            highX = field.getX();
          }
          if(lowY > field.getY()) {
            lowY = field.getY();
          }
          if(highY > field.getY()) {
            highY = field.getY();
          }
        }
      }
      maxFieldsInX = highX - lowX;
      maxFieldsInY = highY - lowY;
      float xLength = (dim.width / ((float)maxFieldsInX + 1f)) /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      float yLength = (dim.height / ((float)maxFieldsInY + 1f)) /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      width = Math.min(xLength, yLength);
    }
  }

  /**
   * updates only the fieldTypes and visibility of tiles and fields
   * @param board
   */
  public void update(Board board) {
    currentBoard = board;
    // TODO check
    if(!currentBoard.getVisibleTiles().isEmpty()) {
      int toUpdate = 0;
      int index = currentBoard.getVisibleTiles().get(0).getIndex();
      for(int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
        if(index != i) {
          tiles.get(i).visible = false;
        } else {
          tiles.get(index).update(currentBoard.getVisibleTiles().get(toUpdate));
          ++toUpdate;
          index = currentBoard.getVisibleTiles().get(toUpdate).getIndex();
        }
      }
    }
  }

  /**
   * Sets size of HexFields, calculates offset
   * @param width
   * @param height
   */
  private void calculateSize(int width, int height) {
    float xDimension = parent.width * GuiConstants.GUI_BOARD_WIDTH;

    float yDimension = parent.height * GuiConstants.PROGRESS_BAR_HEIGHT;
    dim = new Dimension((int) xDimension, (int) yDimension);

    calcHexFieldSize();
  }


  @Override
  public void draw() {
    // resize(parent.displayWidth, parent.displayHeight); // TODO nullpointer
    for (GuiTile tile : tiles) {
      tile.draw();
    }
  }

  public void resize(int width, int height) {
    // calculateSize(width, height); // TODO
    for (GuiTile tile : tiles) {
      tile.resize(offsetX, offsetY, width);
    }
  }

}
