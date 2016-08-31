package sc.plugin2017.gui.renderer.primitives;

import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class GuiBoard extends PrimitiveBase {

  private static final Logger logger = LoggerFactory.getLogger(GuiBoard.class);

  private Board currentBoard;

  private GuiButton left;
  private GuiButton right;
  private GuiButton speedUp;
  private GuiButton speedDown;
  private GuiButton send;
  private GuiButton cancel;

  private GuiPlayer red;
  private GuiPlayer blue;

  private LinkedList<GuiTile> tiles;
  /**
   * holds the position of 0,0 relative to parent
   */
  private float startX;
  private float startY;
  private int offsetX;
  private int offsetY;
  private Dimension dim;
  /**
   * Width of one field
   */
  private float width;

  /**
   * maximum fields in x direction
   */
  private int maxFieldsInX;
  /**
   * maximum fields in y direction
   */
  private int maxFieldsInY;

  public GuiBoard(FrameRenderer parent) {
    super(parent);
    this.parent = parent;

    calculateSize();

    red = new GuiPlayer(parent, width, startX, startY, offsetX, offsetY);
    blue = new GuiPlayer(parent, width, startX, startY, offsetX, offsetY);
    tiles = new LinkedList<GuiTile>();
    for (int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
      tiles.add(new GuiTile(parent, i, width, startX, startY, offsetX, offsetY));
    }

  }

  public void setup() {
    calculateSize();
    createButtons(calculateButtonSize());
  }

  private void createButtons(int size) {
    left = new GuiButton(parent, GuiConstants.ROTATE_LEFT_IMAGE_PATH, 0, 0, size);
    right = new GuiButton(parent, GuiConstants.ROTATE_RIGHT_IMAGE_PATH, 0, 0, size);
    speedUp = new GuiButton(parent, GuiConstants.INCREASE_IMAGE_PATH, 0, 0, size);
    speedDown = new GuiButton(parent, GuiConstants.DECREASE_IMAGE_PATH, 0, 0, size);
    send = new GuiButton(parent, GuiConstants.OKAY_IMAGE_PATH, 0, 0, size);
    cancel = new GuiButton(parent, GuiConstants.CANCEL_IMAGE_PATH, 0, 0, size);
  }

  /**
   * sets width, maxFieldsInX, maxFieldsInY, startX, startY, offset according to
   * dim and currentBoard
   */
  private void calcHexFieldSize() {
    if (currentBoard != null) {
      int lowX = 500;
      int highX = -500;
      int lowY = 500;
      int highY = -500;
      for (Tile tile : currentBoard.getTiles()) {
        for (Field field : tile.fields) {
          if (lowX > field.getX()) {
            lowX = field.getX();
          }
          if (highX < field.getX()) {
            highX = field.getX();
          }
          if (lowY > field.getY()) {
            lowY = field.getY();
          }
          if (highY < field.getY()) {
            highY = field.getY();
          }
        }
      }
      maxFieldsInX = highX - lowX + 1;
      maxFieldsInY = highY - lowY + 1;
      float xLength = (dim.width / (maxFieldsInX + 1f))
          /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      float yLength = (dim.height / (maxFieldsInY + 1f))
          /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
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
   *
   * @param board
   *          Board
   * @param red
   *          red player
   * @param blue
   *          blue player
   * @param current
   *          PlayerColor of currentPlayer in gameState
   */
  public void update(Board board, Player red, Player blue, PlayerColor current) {
    if (board == null) {
      logger.error("got no board in update");
    }
    currentBoard = board;
    calculateSize();
    Player currentPlayer = (current == PlayerColor.RED) ? red : blue;
    logger.debug(String.format("view player red is on %d,%d", red.getField(currentBoard).getX(),
        red.getField(currentBoard).getY()));
    logger.debug(String.format("view player blue is on %d,%d", blue.getField(currentBoard).getX(),
        blue.getField(currentBoard).getY()));
    this.red.update(red, current == PlayerColor.RED);
    this.blue.update(blue, current == PlayerColor.BLUE);
    updateButtonPositions(getCurrentGuiPlayer());
    updateButtonAvailability(currentPlayer, currentBoard);
    if (!currentBoard.getTiles().isEmpty()) {

      // I think this sets old tiles invisible (because they are no longer
      // included in the currentBoard and updates visible tiles.
      // TODO hard to understand, refactor
      int toUpdate = 0;
      int index = currentBoard.getTiles().get(0).getIndex();
      for (int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
        if (index != i) {
          tiles.get(i).setVisible(false);
        } else {
          tiles.get(index).setVisible(true);
          tiles.get(index).update(currentBoard.getTiles().get(toUpdate));
          ++toUpdate;
          if (toUpdate < currentBoard.getTiles().size()) {
            index = currentBoard.getTiles().get(toUpdate).getIndex();
          }
        }
      }

      if (parent.playerControlsEnabled()) {
        LinkedList<HexField> toHighlight = new LinkedList<>();
        LinkedHashMap<HexField, Action> add = new LinkedHashMap<>();
        Field redField = red.getField(currentBoard);
        Field blueField = blue.getField(currentBoard);
        if (redField == null || blueField == null) {
          throw new NullPointerException("Felder sind null");
        }
        if (redField.equals(blueField)) {
          // case push
          if (currentPlayer.getMovement() != 0) {
            for (int j = 0; j < 6; j++) {
              if (j != GameState.getOppositeDirection(currentPlayer.getDirection())) {
                HexField toAdd = getPassableGuiFieldInDirection(currentPlayer.getX(), currentPlayer.getY(), j);
                if (toAdd != null) { // add push to list of actions
                  toHighlight.add(toAdd);
                  add.put(toAdd, new Push(j));
                }
              }
            }
          }
        } else if (currentPlayer.getField(currentBoard).getType() != FieldType.SANDBANK) {
          if (currentPlayer.getMovement() != 0) {
            toHighlight = getPassableGuiFieldsInDirection(currentPlayer.getX(), currentPlayer.getY(),
                currentPlayer.getDirection(), currentPlayer.getMovement());
            // the actions are in order (smallest Step first, so this should
            // work:
            int stepCounter = 1;
            for (HexField hexField : toHighlight) {
              add.put(hexField, new Step(stepCounter));
              ++stepCounter;
            }
          }

        } else {
          // case sandbank
          if (parent.getCurrentPlayer().getMovement() != 0) {
            HexField step = getPassableGuiFieldInDirection(currentPlayer.getX(), currentPlayer.getY(),
                currentPlayer.getDirection());
            if (step != null) {
              toHighlight.add(step);
              add.put(step, new Step(1));
            }
            step = getPassableGuiFieldInDirection(currentPlayer.getX(), currentPlayer.getY(),
                GameState.getOppositeDirection(currentPlayer.getDirection()));
            if (step != null) {
              toHighlight.add(step);
              add.put(step, new Step(-1));
            }
          }
        }
        for (HexField hexField : toHighlight) {
          logger.debug(String.format("Player may move to %d,%d", hexField.getFieldX(), hexField.getFieldY()));
          hexField.setHighlighted(true);
        }
        parent.setPossibleSteps(add);
      } else {
        logger.debug("no human player");
      }
    } else {
      logger.debug("no tiles");
    }
  }

  private GuiPlayer getCurrentGuiPlayer() {
    Player currentPlayer = parent.getCurrentPlayer();
    if (currentPlayer != null) {
      if (currentPlayer.getPlayerColor() == PlayerColor.RED) {
        return red;
      } else {
        return blue;
      }
    } else {
      return null;
    }
  }

  private void updateButtonPositions(GuiPlayer currentGuiPlayer) {
    if (currentGuiPlayer != null) {
      int centerX = Math.round(currentGuiPlayer.getX() + (width / 2));
      int centerY = Math.round(currentGuiPlayer.getY() + (width / 2));
      int curAngle = (currentGuiPlayer.getDirection() * -60) + 90;

      left.moveTo(centerX - (width / 2), centerY - (width / 2));
      right.moveTo(centerX + (width / 2), centerY - (width / 2));

      speedUp.moveTo(centerX + (width / 2), centerY - (width / 6));
      speedDown.moveTo(centerX + (width / 2), centerY + (width / 6));

      send.moveTo(centerX + (width / 5), centerY + (width / 2));
      cancel.moveTo(centerX - (width / 5), centerY + (width / 2));
    }
  }

  private void updateButtonAvailability(Player currentPlayer, Board currentBoard) {

    boolean maxSpeed = parent.getCurrentPlayer().getSpeed() == 6;
    boolean minSpeed = parent.getCurrentPlayer().getSpeed() == 1;
    boolean onSandbank = parent.getCurrentPlayerField().getType() == FieldType.SANDBANK;
    boolean accelerationPossible = parent.getCurrentPlayer().getFreeAcc() + parent.getCurrentPlayer().getCoal() > 0;
    boolean rotationPossible = parent.getCurrentPlayer().getFreeTurns() + parent.getCurrentPlayer().getCoal() > 0;

    speedUp.setEnabled(!maxSpeed && !onSandbank && accelerationPossible);
    speedDown.setEnabled(!minSpeed && !onSandbank && accelerationPossible);
    left.setEnabled(rotationPossible && !onSandbank);
    right.setEnabled(rotationPossible && !onSandbank);
  }

  private HexField getPassableGuiFieldInDirection(int x, int y, int j) {
    LinkedList<HexField> passable = getPassableGuiFieldsInDirection(x, y, j, 1);
    if (passable.isEmpty() || passable.getFirst() == null) {
      return null;
    } else {
      return passable.getFirst();
    }
  }

  /**
   * Sets size of HexFields, calculates offset
   *
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

    // buttons
    if (parent.playerControlsEnabled()) {
      left.draw();
      right.draw();
      speedUp.draw();
      speedDown.draw();
      send.draw();
      cancel.draw();
    }
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
    updateButtonPositions(getCurrentGuiPlayer());
  }

  private void resizeButtons() {
    // buttons cannot be resized and need to be recreated
    createButtons(calculateButtonSize());
  }

  private int calculateButtonSize() {
    return Math.max(1, Math.round(width / 3));
  }

  @Override
  public void kill() {

    if (red != null) {
      red.kill();
    }
    if (blue != null) {
      blue.kill();
    }
    for (GuiTile tile : tiles) {
      tile.kill();
    }
  }

  /**
   *
   * @param startX
   *          anfangs x
   * @param startY
   *          anfangs y
   * @param direction
   *          Richtung
   * @param step
   *          Bewegunsgpunkte
   * @return Begehbare Felder
   */
  public LinkedList<HexField> getPassableGuiFieldsInDirection(int startX, int startY, int direction, int step) {
    Player opponent = parent.getCurrentOpponent();
    int opponentX = opponent.getX();
    int opponentY = opponent.getY();
    LinkedList<HexField> fields = new LinkedList<HexField>();
    // TODO document/refactor
    for (int i = 1; i <= step; i++) {
      switch (direction) {
      case 0:
        startX++;
        break;
      case 1:
        if (startY % 2 == 0) {
          --startY;
          ++startX;
        } else {
          --startY;
        }
        break;
      case 2:
        if (startY % 2 == 0) {
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
        if (startY % 2 == 0) {
          ++startY;
        } else {
          ++startY;
          --startX;
        }
        break;
      case 5:
        if (startY % 2 == 0) {
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
      if (highlight != null && Field.isPassable(highlight.getType())) {
        fields.add(highlight);
        if (highlight.getType() == FieldType.SANDBANK) {
          return fields;
        }
        if (startX == opponentX && startY == opponentY) {
          // moving onto opponent only assuming we want to push, and this costs
          // one more step
          if (i + 1 > step) {
            fields.remove(fields.getLast());
          }
          return fields;
        }
        if (highlight.getType() == FieldType.LOG) {
          if (i + 1 > step) {
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
      if (field != null) {
        return field;
      }
    }
    return null;
  }

  public List<GuiTile> getTiles() {
    return tiles;
  }

  public ClickedButton getClickedButton(int mouseX, int mouseY) {
    if (left.hover(mouseX, mouseY)) {
      return ClickedButton.LEFT;
    } else if (right.hover(mouseX, mouseY)) {
      return ClickedButton.RIGHT;
    } else if (speedUp.hover(mouseX, mouseY)) {
      return ClickedButton.SPEED_UP;
    } else if (speedDown.hover(mouseX, mouseY)) {
      return ClickedButton.SPEED_DOWN;
    } else if (send.hover(mouseX, mouseY)) {
      return ClickedButton.SEND;
    } else if (cancel.hover(mouseX, mouseY)) {
      return ClickedButton.CANCEL;
    } else {
      return ClickedButton.NONE;
    }
  }

  public boolean hoversButton(int mouseX, int mouseY) {
    return left.hover(mouseX, mouseY) || right.hover(mouseX, mouseY) || speedUp.hover(mouseX, mouseY)
        || speedDown.hover(mouseX, mouseY) || send.hover(mouseX, mouseY) || cancel.hover(mouseX, mouseY);
  }

}
