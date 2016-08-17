package sc.plugin2017.gui.renderer.primitives;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedList;

import sc.plugin2017.gui.renderer.primitives.GuiConstants;
import sc.plugin2017.gui.renderer.primitives.HexField;
import sc.plugin2017.util.Constants;
import sc.plugin2017.Board;
import sc.plugin2017.Field;
import sc.plugin2017.FieldType;
import sc.plugin2017.GameState;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.Tile;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GuiBoard extends PrimitiveBase{

  FrameRenderer parent;
  
  Board currentBoard;
  

  public GuiPlayer red;
  public GuiPlayer blue;
  
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

    red = new GuiPlayer(parent);
    blue = new GuiPlayer(parent);
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
      maxFieldsInX = highX - lowX + 1;
      maxFieldsInY = highY - lowY + 1;
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
  public void update(Board board, Player red, Player blue, PlayerColor current) {
    System.out.println("\n\n\n Update board was called\n\n\n");
    currentBoard = board;
    this.red.update(red, current == PlayerColor.RED);
    this.blue.update(blue, current == PlayerColor.BLUE);
    // TODO set highlighted fields
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
      LinkedList<HexField> toHighlight = new LinkedList<HexField>();
      Player currentPlayer = (current == PlayerColor.RED) ? red : blue;
      if(red.getField(currentBoard).equals(blue.getField(currentBoard))) {
      }
      if(currentPlayer.getField(currentBoard).getType() != FieldType.SANDBANK) {
        toHighlight = 
            getPassableGuiFieldsInDirection(currentPlayer.getX(), currentPlayer.getY(),
                currentPlayer.getDirection(), currentPlayer.getMovement());
        
      } else {
        toHighlight = getPassableGuiFieldsInDirection(currentPlayer.getX(), currentPlayer.getY(),
            currentPlayer.getDirection(), 1);
        toHighlight.addAll(getPassableGuiFieldsInDirection(currentPlayer.getX(), currentPlayer.getY(),
            GameState.getOppositeDirection(currentPlayer.getDirection()), 1));
      }
      for (HexField hexField : toHighlight) {
        System.out.println(hexField);
        hexField.setHighlighted(true);
      }
//      GuiPlayer currentGuiPlayer = (current == PlayerColor.RED) ? this.red : this.blue;
//      parent.currentGameState.getPossibleMovesInDirection(currentPlayer, currentGuiPlayer.movement, currentGuiPlayer.coal);
//      red.getField(board).getFieldInDirection(direction, board)
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
      resize(parent.getWidth(), parent.getHeight());
      System.out.println("Width: " + width + " maxFieldsIn: (" + maxFieldsInX + ", " + maxFieldsInY + 
          ") offset: (" + offsetX + ", " + offsetY + ") Dim " + dim + "start: (" + startX + ", " + startY + ")");
      for (GuiTile tile : tiles) {
        tile.draw();
      }
      // draw players
      red.draw();
      blue.draw();
    }
  }

  public void resize(int width, int height) {
    calculateSize(width, height);
    for (GuiTile tile : tiles) {
      tile.resize(startX, startY, offsetX, offsetY, this.width);
    }
    red.resize(startX, startY, offsetX, offsetY, this.width);
    blue.resize(startX, startY, offsetX, offsetY, this.width);
  }
  
  public void kill(){

    if(red != null) {
      red.kill();
    }
    if(blue != null) {
      blue.kill();
    }
    for (GuiTile tile : tiles) {
      tile.kill();
    }
  }
  
  /**
   * 
   * @param startX anfangs x
   * @param startY anfangs y
   * @param direction Richtung
   * @param step Bewegunsgpunkte
   * @return
   */
  public LinkedList<HexField> getPassableGuiFieldsInDirection(int startX, int startY, int direction, int step) {
    LinkedList<HexField> fields = new LinkedList<HexField>();
    for(int i = 1; i <= step; i++) {
      switch (direction) {
      case 0:
        startX++;
        break;
      case 1:
        if(startY % 2 == 0) {
          --startY;
          ++startX;
        } else {
          --startY;
        }
        break;
      case 2:
        if(startY % 2 == 0) {
          --startY;
        } else {
          --startY;
          --startX;
        }
        break;
      case 3:
        --startX;
        break;
      case 4:
        if(startY % 2 == 0) {
          ++startY;
        } else {
          ++startY;
          --startX;
        }
        break;
      case 5:
        if(startY % 2 == 0) {
          ++startY;
          ++startX;
        } else {
          ++startY;
        }
        break;

      default:
        break;
      }
      HexField highlight = getHexField(startX, startY);
      if(highlight != null && Field.isPassable(highlight.type)) {
        fields.add(highlight);
        if(highlight.type == FieldType.SANDBANK) {
          return fields;
        }
        if(highlight.type == FieldType.LOG) {
          ++step;
        }
      } else {
        return fields;
      }
    }
    return fields;
  }
  
  public HexField getHexField(int x, int y) {
    for (GuiTile tile : tiles) {
      HexField field = tile.getHexField(x, y);
      if(field != null) {
        return field;
      }
    }
    return null;
  }

}
