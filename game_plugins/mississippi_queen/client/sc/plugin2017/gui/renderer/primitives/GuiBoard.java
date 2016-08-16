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
  public float startX;
  public float startY;
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
    if(parent == null) {
      System.out.println(" \n\n\n THis should never happen!!!!!!!\n\n\n");
    }
    this.parent = parent;
    tiles = new LinkedList<GuiTile>();
    for(int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
      tiles.add(new GuiTile(parent, i));
    }
    
    float xDimension = parent.getWidth() * GuiConstants.GUI_BOARD_WIDTH;

    float yDimension = parent.getHeight() * GuiConstants.GUI_BOARD_HEIGHT;

    dim = new Dimension((int) xDimension, (int) yDimension);
    System.out.println("Parent: (" + parent.getWidth() + ", " + parent.getHeight() + ") dim ("
        + xDimension + ", " + yDimension + ")");
    if(parent.currentGameState != null) {
      currentBoard = parent.currentGameState.getVisibleBoard();
    }
    calcHexFieldSize();
  }

  /**
   * sets width, maxFieldsInX, maxFieldsInY, startX, startY, offset according to dim and currentBoard
   */
  private void calcHexFieldSize() {
    // TODO calculate start
    if(currentBoard != null) {
      int lowX = 500;
      int highX = -500;
      int lowY = 500;
      int highY = -500;
      for (Tile tile : currentBoard.getTiles()) {
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
          if(highY < field.getY()) {
            highY = field.getY();
          }
        }
      }
      maxFieldsInX = highX - lowX;
      maxFieldsInY = highY - lowY;
      float xLength = (dim.width / ((float) maxFieldsInX + 1f)) /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      float yLength = (dim.height / ((float) maxFieldsInY + 1f)) /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      System.out.println("xLength: " + xLength + " yLength " + yLength);
      width = Math.min(xLength, yLength);
      offsetX = -lowX;
      offsetY = -lowY;
      float sizeX = (width + GuiConstants.BORDERSIZE);
      float sizeY = (HexField.calcA(width) + HexField.calcC(width) + GuiConstants.BORDERSIZE);
      startX = (dim.width - (sizeX * maxFieldsInX)) / 2f;
      startY = (dim.height - (sizeY * maxFieldsInY)) / 2f;
    }
  }

  /**
   * updates only the fieldTypes and visibility of tiles and fields
   * @param board
   */
  public void update(Board board) {
    System.out.println("\n\n\n Update board was called\n\n\n");
    currentBoard = board;
    // TODO check
    if(!currentBoard.getTiles().isEmpty()) {
      int toUpdate = 0;
      int index = currentBoard.getTiles().get(0).getIndex();
      for(int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
        if(index != i) {
          tiles.get(i).visible = false;
        } else {
          tiles.get(index).visible = true;
          tiles.get(index).update(currentBoard.getTiles().get(toUpdate));
          ++toUpdate;
          if(toUpdate < currentBoard.getTiles().size()) {
            index = currentBoard.getTiles().get(toUpdate).getIndex();
          }
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
    if(parent != null) {
      float xDimension = parent.getWidth() * GuiConstants.GUI_BOARD_WIDTH;
    

      float yDimension = parent.getHeight() * GuiConstants.GUI_BOARD_HEIGHT;
      dim = new Dimension((int) xDimension, (int) yDimension);
      System.out.println("Parent: (" + parent.getWidth() + ", " + parent.getHeight() + ") dim ("
          + xDimension + ", " + yDimension + ")");
    }

    calcHexFieldSize();
  }


  @Override
  public void draw() {
    if(parent != null) {
      System.out.println("\n\nBegin drawing tile\n\n");
      resize(parent.getWidth(), parent.getHeight()); // TODO nullpointer
      System.out.println("Width: " + width + " maxFieldsIn: (" + maxFieldsInX + ", " + maxFieldsInY + 
          ") offset: (" + offsetX + ", " + offsetY + ") Dim " + dim + "start: (" + startX + ", " + startY + ")");
      for (GuiTile tile : tiles) {
        tile.draw();
      }
    }
  }

  public void resize(int width, int height) {
    calculateSize(width, height); // TODO
    for (GuiTile tile : tiles) {
      tile.resize(startX, startY, offsetX, offsetY, this.width);
    }
  }

}
