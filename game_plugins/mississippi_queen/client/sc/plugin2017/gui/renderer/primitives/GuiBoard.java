package sc.plugin2017.gui.renderer.primitives;

import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import g4p_controls.GButton;
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

  private static final Logger logger = LoggerFactory.getLogger(GuiBoard.class);

  FrameRenderer parent;

  Board currentBoard;

  public GButton left;
  public GButton right;
  public GButton speedUp;
  public GButton speedDown;
  public GButton send;
  public GButton cancel;

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

  }

  public void setup() {
    calculateSize();
    createButtons();
  }

  /**
   * creating the buttons in the class constructor leads to NullPointer
   * exceptions. It seems that G4P requires that the setup method was called
   * before the buttons are created.
   */
  private void createButtons() {
    left = new GButton(parent, 0, 0, 50, 20);
    right = new GButton(parent, 0, 0, 50, 20);
    speedUp = new GButton(parent, 0, 0, 50, 20);
    speedDown = new GButton(parent, 0, 0, 50, 20);
    send = new GButton(parent, 0, 0, 50, 20);
    cancel = new GButton(parent, 0, 0, 50, 20);
    /*
    left = new GImageButton(parent, 0, 0, new String[] { GuiConstants.ROTATE_LEFT_IMAGE_PATH });
    right = new GImageButton(parent, 0, 0, new String[] { GuiConstants.ROTATE_RIGHT_IMAGE_PATH });
    speedUp = new GImageButton(parent, 0, 0, new String[] { GuiConstants.INCREASE_IMAGE_PATH });
    speedDown = new GImageButton(parent, 0, 0, new String[] { GuiConstants.DECREASE_IMAGE_PATH });
    send = new GImageButton(parent, 0, 0, new String[] { GuiConstants.OKAY_IMAGE_PATH });
    cancel = new GImageButton(parent, 0, 0, new String[] { GuiConstants.CANCEL_IMAGE_PATH });
    */

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
    } else {
      logger.error("trying to calculate hex field size without currentBoard!");
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
    if (board == null) {
      logger.error("got no board in update");
    }
    currentBoard = board;
    calculateSize();
    Player currentPlayer = (current == PlayerColor.RED) ? red : blue;
    logger.debug(String.format("view player red is on %d,%d", red.getField(currentBoard).getX(), red.getField(currentBoard).getY() ));
    logger.debug(String.format("view player blue is on %d,%d", blue.getField(currentBoard).getX(), blue.getField(currentBoard).getY() ));
    this.red.update(red, current == PlayerColor.RED);
    this.blue.update(blue, current == PlayerColor.BLUE);
    updateButtonPositions(board, red, blue, current);
    updateButtonAvailability(currentPlayer, currentBoard);
    if(!currentBoard.getTiles().isEmpty()) {

      // I think this sets old tiles invisible (because they are no longer
      // included in the currentBoard and updates visible tiles.
      // TODO hard to understand, refactor
      int toUpdate = 0;
      int index = currentBoard.getTiles().get(0).getIndex();
      for(int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
        if(index != i) {
          tiles.get(i).visible = false;
        } else {
          tiles.get(index).visible = true;
          tiles.get(index).update(currentBoard.getTiles().get(toUpdate));
          ++toUpdate;
          if (toUpdate < currentBoard.getTiles().size()) {
            index = currentBoard.getTiles().get(toUpdate).getIndex();
          }
        }
      }

      if (parent.humanPlayer) {
        LinkedList<HexField> toHighlight = new LinkedList<>();
        LinkedHashMap<HexField, Action> add = new LinkedHashMap<>();
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
          logger.debug(String.format("Player may move to %d,%d", hexField.fieldX, hexField.fieldY));
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

    left.moveTo(curX, curY);
    right.setRotation(curAngle);

    curY += 40;

    right.moveTo(curX, curY);
    right.setRotation(curAngle);

    curY += 40;

    speedUp.moveTo(curX, curY);

    curY += 40;

    speedDown.moveTo(curX, curY);

    curY += 40;

    send.moveTo(curX, curY);

    curY += 40;

    cancel.moveTo(curX, curY);
  }

  private void updateButtonAvailability(Player currentPlayer, Board currentBoard) {

    boolean maxSpeed = parent.currentGameState.getCurrentPlayer().getSpeed() == 6;
    boolean minSpeed = parent.currentGameState.getCurrentPlayer().getSpeed() == 1;
    boolean onSandbank = parent.currentGameState.getCurrentPlayer().getField(parent.currentGameState.getBoard()).getType() == FieldType.SANDBANK;
    boolean accelerationPossible = parent.currentGameState.getCurrentPlayer().getFreeAcc() + parent.currentGameState.getCurrentPlayer().getCoal() > 0;
    boolean rotationPossible = parent.currentGameState.getCurrentPlayer().getFreeTurns() + parent.currentGameState.getCurrentPlayer().getCoal() > 0;

    speedUp.setEnabled( !maxSpeed && !onSandbank && accelerationPossible);
    speedDown.setEnabled(!minSpeed && !onSandbank && accelerationPossible);
    left.setEnabled(rotationPossible && !onSandbank);
    right.setEnabled(rotationPossible && !onSandbank);
  }

  private HexField getPassableGuiFieldInDirection(int x, int y, int j) {
    LinkedList<HexField> passable = getPassableGuiFieldsInDirection(x, y, j, 1);
    if(passable.isEmpty() || passable.getFirst() == null) {
      return null;
    } else {
      return passable.getFirst();
    }
  }

  /**
   * Sets size of HexFields, calculates offset
   * @param width
   * @param height
   */
  private void calculateSize() {
    logger.debug(String.format("calculating gui board sizes, size: %d,%d", parent.getWidth(), parent.getHeight()));
    float xDimension = parent.getWidth() * GuiConstants.GUI_BOARD_WIDTH;
    float yDimension = parent.getHeight() * GuiConstants.GUI_BOARD_HEIGHT;
    dim = new Dimension((int) xDimension, (int) yDimension);

    calcHexFieldSize();
  }


  @Override
  public void draw() {
    for (GuiTile tile : tiles) {
      tile.draw();
    }
    // draw players
    red.draw();
    blue.draw();

    // buttons are drawn automatically
  }

  /**
   * should be called when the parents dimensions change
   */
  public void resize() {
    calculateSize();
    for (GuiTile tile : tiles) {
      tile.resize(startX, startY, offsetX, offsetY, this.width);
    }
    red.resize(startX, startY, offsetX, offsetY, this.width);
    blue.resize(startX, startY, offsetX, offsetY, this.width);
    resizeButtons();
    if (parent.currentGameState != null) {
      updateButtonPositions(parent.currentGameState.getBoard(), parent.currentGameState.getRedPlayer(), parent.currentGameState.getBluePlayer(), parent.currentGameState.getCurrentPlayerColor());
    }
  }

  private void resizeButtons() {
    int newButtonWidth = Math.round(width / 2);
    /*
    left.resize(newButtonWidth, newButtonWidth);
    right.resize(newButtonWidth, newButtonWidth);
    speedUp.resize(newButtonWidth, newButtonWidth);
    speedDown.resize(newButtonWidth, newButtonWidth);
    send.resize(newButtonWidth, newButtonWidth);
    cancel.resize(newButtonWidth, newButtonWidth);
    */
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
