package sc.plugin2017.gui.renderer.primitives;

import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import sc.plugin2017.Action;
import sc.plugin2017.Board;
import sc.plugin2017.Field;
import sc.plugin2017.FieldType;
import sc.plugin2017.GameState;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.Push;
import sc.plugin2017.Step;
import sc.plugin2017.Tile;
import sc.plugin2017.gui.renderer.FrameRenderer;
import sc.plugin2017.util.Constants;

public class GuiBoard extends PrimitiveBase{

  FrameRenderer parent;

  Board currentBoard;

  public GuiButton left;
  public GuiButton right;
  public GuiButton speedUp;
  public GuiButton speedDown;
  public GuiButton send;
  public GuiButton cancel;

  public GuiPlayer red;
  public GuiPlayer blue;

  public LinkedList<GuiTile> tiles;
  /**
   * holds the position of 0,0 relative to parent
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
    if(parent.currentGameState != null) {
      currentBoard = parent.currentGameState.getVisibleBoard();
    }
    calcHexFieldSize();

    left = new CircularGuiButton(parent, "Links");
    right = new CircularGuiButton(parent, "Rechts");
    speedUp = new CircularGuiButton(parent, "+");
    speedDown = new CircularGuiButton(parent, "-");
    send = new CircularGuiButton(parent, "Fertig");
    cancel = new CircularGuiButton(parent, "Neu");
    resizeButtons();
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
      float xLength = (dim.width / (maxFieldsInX + 1f)) /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      float yLength = (dim.height / (maxFieldsInY + 1f)) /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
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
   * @param board Board
   * @param red red player
   * @param blue blue player
   * @param current PlayerColor of currentPlayer in gameState
   */
  public void update(Board board, Player red, Player blue, PlayerColor current) {
    currentBoard = board;
    this.red.update(red, current == PlayerColor.RED);
    this.blue.update(blue, current == PlayerColor.BLUE);
    updateButtonPositions(board, red, blue, current);
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
      if(parent.humanPlayer) {
        LinkedList<HexField> toHighlight = new LinkedList<HexField>();
        LinkedHashMap<HexField, Action> add = new LinkedHashMap<HexField, Action>();
        Player currentPlayer = (current == PlayerColor.RED) ? red : blue;
        Field redField = red.getField(currentBoard);
        Field blueField = blue.getField(currentBoard);
        if(redField == null || blueField == null) {
          throw new NullPointerException("Felder sind null");
        }
        if(redField.equals(blueField)) {
          // case push
          if(currentPlayer.getMovement() != 0) {
            for(int j = 0; j < 6; j++) {
              if(j != GameState.getOppositeDirection(currentPlayer.getDirection())) {
                HexField toAdd = getPassableGuiFieldInDirection(
                    currentPlayer.getX(), currentPlayer.getY(), j);
                if(toAdd != null) { // add push to list of actions
                  toHighlight.add(toAdd);
                  add.put(toAdd,new Push(j));
                }
              }
            }
          }
        } else if(currentPlayer.getField(currentBoard).getType() != FieldType.SANDBANK) {
          if(currentPlayer.getMovement() != 0) {
            toHighlight =
              getPassableGuiFieldsInDirection(currentPlayer.getX(), currentPlayer.getY(),
                  currentPlayer.getDirection(), currentPlayer.getMovement());
            // the actions are in order (smallest Step first, so this should work:
            int stepCounter = 1;
            for (HexField hexField : toHighlight) {
              add.put(hexField, new Step(stepCounter));
              ++stepCounter;
            }
          }

        } else {
          // case sandbank
          if(parent.currentGameState.getCurrentPlayer().getMovement() != 0) {
            HexField step = getPassableGuiFieldInDirection(currentPlayer.getX(), currentPlayer.getY(),
                currentPlayer.getDirection());
            if(step != null) {
              toHighlight.add(step);
              add.put(step, new Step(1));
            }
            step = getPassableGuiFieldInDirection(currentPlayer.getX(), currentPlayer.getY(),
                GameState.getOppositeDirection(currentPlayer.getDirection()));
            if(step != null) {
              toHighlight.add(step);
              add.put(step, new Step(-1));
            }
          }
        }
        for (HexField hexField : toHighlight) {
          hexField.setHighlighted(true);
        }
        parent.stepPossible = add;
      }
    }
  }

  private void updateButtonPositions(Board board, Player redPlayer, Player bluePlayer, PlayerColor currentPlayerColor) {
    GuiPlayer currentGuiPlayer;
    Player currentPlayer;
    if (currentPlayerColor == PlayerColor.RED) {
      currentGuiPlayer = red;
      currentPlayer = redPlayer;
    } else {
      currentGuiPlayer = blue;
      currentPlayer = bluePlayer;
    }

    int curX = Math.round(currentGuiPlayer.getX());
    int curY = Math.round(currentGuiPlayer.getY());
    int curAngle = (currentPlayer.getDirection() * -60) + 90;

    left.x = curX;
    left.y = curY;
    left.angle = curAngle;

    curY += 40;

    right.x = curX;
    right.y = curY;
    right.angle = curAngle;

    curY += 40;

    speedUp.x = curX;
    speedUp.y = curY;
    speedUp.angle = curAngle;

    curY += 40;

    speedDown.x = curX;
    speedDown.y = curY;
    speedDown.angle = curAngle;

    curY += 40;

    send.x = curX;
    send.y = curY;
    send.angle = curAngle;

    curY += 40;

    cancel.x = curX;
    cancel.y = curY;
    cancel.angle = curAngle;
  }

  private HexField getPassableGuiFieldInDirection(int x, int y, int j) {
    LinkedList<HexField> passable = getPassableGuiFieldsInDirection(x, y, j, 1);
    if(passable.isEmpty()) {
      return null;
    } else if(passable.getFirst() == null) {
      return null;
    }
    return passable.getFirst();
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
    }

    calcHexFieldSize();
  }


  @Override
  public void draw() {
    if (parent != null) {
      for (GuiTile tile : tiles) {
        tile.draw();
      }
      // draw players
      red.draw();
      blue.draw();

      // draw Buttons
      if (parent.isHumanPlayer() && parent.maxTurn == parent.currentGameState.getTurn()) {
        if (parent.currentGameState.getCurrentPlayer().getField(parent.currentGameState.getBoard())
            .getType() != FieldType.SANDBANK) {
          if (parent.currentGameState.getCurrentPlayer().getCoal()
              + parent.currentGameState.getCurrentPlayer().getFreeTurns() != 0) {
            right.draw();
            left.draw();
          }
          if (parent.currentGameState.getCurrentPlayer().getSpeed() != 1
              && parent.currentGameState.getCurrentPlayer().getMovement() != 0
              && parent.currentGameState.getCurrentPlayer().getCoal()
                  + parent.currentGameState.getCurrentPlayer().getFreeAcc() != 0) {
            speedDown.draw();
          }
          if (parent.currentGameState.getCurrentPlayer().getSpeed() != 6
              && parent.currentGameState.getCurrentPlayer().getCoal()
                  + parent.currentGameState.getCurrentPlayer().getFreeAcc() != 0) {
            speedUp.draw();
          }
        }
        send.draw();
        cancel.draw();
      }
    }
  }

  public void resize(int width, int height) {
    calculateSize(width, height);
    for (GuiTile tile : tiles) {
      tile.resize(startX, startY, offsetX, offsetY, this.width);
    }
    red.resize(startX, startY, offsetX, offsetY, this.width);
    blue.resize(startX, startY, offsetX, offsetY, this.width);
    resizeButtons();
  }

  private void resizeButtons() {
    int newButtonWidth = Math.round(width / 2);
    left.resize(newButtonWidth);
    right.resize(newButtonWidth);
    speedUp.resize(newButtonWidth);
    speedDown.resize(newButtonWidth);
    send.resize(newButtonWidth);
    cancel.resize(newButtonWidth);
  }

  @Override
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
    if(this.left != null && this.left.parent != null) {
      this.left.kill();
    }
    if(this.right != null && this.right.parent != null) {
      this.right.kill();
    }
    if(this.speedDown != null && this.speedDown.parent != null) {
      this.speedDown.kill();
    }
    if(this.speedUp != null && this.speedUp.parent != null) {
      this.speedUp.kill();
    }
    if(this.send != null && this.send.parent != null) {
      this.send.kill();
    }
  }

  /**
   *
   * @param startX anfangs x
   * @param startY anfangs y
   * @param direction Richtung
   * @param step Bewegunsgpunkte
   * @return Begehbare Felder
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
          if(i + 1 > step) {
            fields.remove(fields.getLast());
            return fields;
          } else {
            ++i; // moving over log costs 1 extra
          }
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
