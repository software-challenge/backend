package sc.plugin2017.gui.renderer.primitives;

import java.awt.Dimension;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.plugin2017.Acceleration;
import sc.plugin2017.Action;
import sc.plugin2017.Advance;
import sc.plugin2017.Board;
import sc.plugin2017.Direction;
import sc.plugin2017.Field;
import sc.plugin2017.FieldType;
import sc.plugin2017.Move;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.Push;
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
  private EnumMap<ClickedButton, GuiButton> allButtons;

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
    createButtons(calculateButtonSize());
  }

  public void setup() {
    for (GuiButton button : allButtons.values()) {
      button.setup();
    }
  }

  private void createButtons(int size) {
    allButtons = new EnumMap<>(ClickedButton.class);
    left = new GuiButton(parent, GuiConstants.ROTATE_LEFT_IMAGE_PATH, "Drehung nach links", 0, 0, size);
    allButtons.put(ClickedButton.LEFT, left);
    right = new GuiButton(parent, GuiConstants.ROTATE_RIGHT_IMAGE_PATH, "Drehung nach rechts", 0, 0, size);
    allButtons.put(ClickedButton.RIGHT, right);
    speedUp = new GuiButton(parent, GuiConstants.INCREASE_IMAGE_PATH, "Beschleunigen", 0, 0, size);
    allButtons.put(ClickedButton.SPEED_UP, speedUp);
    speedDown = new GuiButton(parent, GuiConstants.DECREASE_IMAGE_PATH, "Abbremsen", 0, 0, size);
    allButtons.put(ClickedButton.SPEED_DOWN, speedDown);
    send = new GuiButton(parent, GuiConstants.OKAY_IMAGE_PATH, "Zug beenden", 0, 0, size);
    allButtons.put(ClickedButton.SEND, send);
    cancel = new GuiButton(parent, GuiConstants.CANCEL_IMAGE_PATH, "Zug erneut eingeben", 0, 0, size);
    allButtons.put(ClickedButton.CANCEL, cancel);
  }

  /**
   * sets width, maxFieldsInX, maxFieldsInY, startX, startY, offset according to
   * dim and currentBoard
   */
  private void calcHexFieldSize(Dimension viewPortSize) {
    // before getting the initial board we can't (and don't have to) calculate sizes
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
      float xLength = (viewPortSize.width / (maxFieldsInX + 1f))
          /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      float yLength = (viewPortSize.height / (maxFieldsInY + 1f))
          /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      width = Math.min(xLength, yLength);
      offsetX = -lowX;
      offsetY = -lowY;
      float sizeX = (width + GuiConstants.BORDERSIZE);
      float sizeY = (HexField.calcA(width) + HexField.calcC(width) + GuiConstants.BORDERSIZE);
      startX = (viewPortSize.width - (sizeX * maxFieldsInX)) / 2f;
      startY = (viewPortSize.height - (sizeY * maxFieldsInY)) / 2f;
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
  public void update(Board board, Player red, Player blue, PlayerColor current, Move currentMove) {
    if (board == null) {
      logger.error("got no board in update");
    }
    currentBoard = board;
    calculateSize();
    Player currentPlayer = (current == PlayerColor.RED) ? red : blue;
    this.red.update(red, current == PlayerColor.RED);
    this.blue.update(blue, current == PlayerColor.BLUE);
    updateButtonPositions(getCurrentGuiPlayer());
    updateButtonAvailability(currentPlayer, currentBoard, currentMove);
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
            for (Direction j : Direction.values()) {
              if (j != currentPlayer.getDirection().getOpposite()) {
                HexField toAdd = getPassableGuiFieldInDirection(currentPlayer.getX(), currentPlayer.getY(), j, currentPlayer.getMovement());
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
              add.put(hexField, new Advance(stepCounter));
              ++stepCounter;
            }
          }

        } else {
          // case sandbank
          if (parent.getCurrentPlayer().getMovement() != 0) {
            HexField step = getPassableGuiFieldInDirection(currentPlayer.getX(), currentPlayer.getY(),
                currentPlayer.getDirection(), parent.getCurrentPlayer().getMovement());
            if (step != null) {
              toHighlight.add(step);
              add.put(step, new Advance(1));
            }
            step = getPassableGuiFieldInDirection(currentPlayer.getX(), currentPlayer.getY(),
                currentPlayer.getDirection().getOpposite(), currentPlayer.getMovement());
            if (step != null) {
              toHighlight.add(step);
              add.put(step, new Advance(-1));
            }
          }
        }
        for (HexField hexField : toHighlight) {
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

      left.moveTo(centerX - (width / 2), centerY - (width / 2));
      right.moveTo(centerX + (width / 2), centerY - (width / 2));

      speedUp.moveTo(centerX + (width / 2), centerY - (width / 6));
      speedDown.moveTo(centerX + (width / 2), centerY + (width / 6));

      send.moveTo(centerX + (width / 5), centerY + (width / 2));
      cancel.moveTo(centerX - (width / 5), centerY + (width / 2));
    }
  }

  private void updateButtonAvailability(Player currentPlayer, Board currentBoard, Move currentMove) {

    boolean maxSpeed = currentPlayer.getSpeed() == 6;
    boolean minSpeed = currentPlayer.getSpeed() == 1;
    boolean onSandbank = currentPlayer.getField(currentBoard).getType() == FieldType.SANDBANK;
    boolean accelerationPossible = currentPlayer.getFreeAcc() + currentPlayer.getCoal() > 0;
    boolean rotationPossible = currentPlayer.getFreeTurns() + currentPlayer.getCoal() > 0;
    boolean firstAction = true;
    for (Action action : currentMove.actions) {
      if (action.getClass() != Acceleration.class) {
        firstAction = false;
      }
    }

    speedUp.setEnabled(!maxSpeed && !onSandbank && accelerationPossible && firstAction);
    speedDown.setEnabled(!minSpeed && !onSandbank && accelerationPossible && firstAction);
    left.setEnabled(rotationPossible && !onSandbank);
    right.setEnabled(rotationPossible && !onSandbank);
  }

  private HexField getPassableGuiFieldInDirection(int x, int y, Direction j, int movement) {
    LinkedList<HexField> passable = getPassableGuiFieldsInDirection(x, y, j, movement);
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
    float xDimension = parent.getWidth() * GuiConstants.GUI_BOARD_WIDTH;
    float yDimension = parent.getHeight() * GuiConstants.GUI_BOARD_HEIGHT;
    Dimension viewPortSize = new Dimension((int) xDimension, (int) yDimension);
    calcHexFieldSize(viewPortSize);
  }

  @Override
  public void draw() {
    resize();
    for (GuiTile tile : tiles) {
      tile.draw();
    }
    // highlights need to be drawn above fields
    if (parent.playerControlsEnabled()) {
      for (GuiTile tile : tiles) {
        tile.drawHighlights();
      }
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
    if (left != null && right != null && speedUp != null && speedDown != null && send != null && cancel != null) {
      int newSize = calculateButtonSize();
      left.resize(newSize);
      right.resize(newSize);
      speedUp.resize(newSize);
      speedDown.resize(newSize);
      send.resize(newSize);
      cancel.resize(newSize);
    }
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
  public LinkedList<HexField> getPassableGuiFieldsInDirection(int startX, int startY, Direction direction, int step) {
    Player opponent = parent.getCurrentOpponent();
    int opponentX = opponent.getX();
    int opponentY = opponent.getY();
    LinkedList<HexField> fields = new LinkedList<HexField>();
    // TODO document/refactor
    for (int i = 1; i <= step; i++) {
      switch (direction) {
      case RIGHT:
        startX++;
        break;
      case UP_RIGHT:
        if (startY % 2 == 0) {
          --startY;
          ++startX;
        } else {
          --startY;
        }
        break;
      case UP_LEFT:
        if (startY % 2 == 0) {
          --startY;
        } else {
          --startY;
          --startX;
        }
        break;
      case LEFT:
        --startX;
        break;
      case DOWN_LEFT:
        if (startY % 2 == 0) {
          ++startY;
        } else {
          ++startY;
          --startX;
        }
        break;
      case DOWN_RIGHT:
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
      boolean isSandbank = highlight != null && highlight.getType() == FieldType.SANDBANK;
      boolean isOpponent = (startX == opponentX && startY == opponentY);
      // To move onto a field it needs to be passable and it has to be not a
      // sanbank with the opponent on it, because pushing the opponent on a
      // sandbank is forbidden.
      if (highlight != null && Field.isPassable(highlight.getType()) && !(isSandbank && isOpponent)) {
        fields.add(highlight);
        if (isSandbank) {
          // A sandbank is always the last field onto which can be moved.
          return fields;
        }
        if (isOpponent) {
          // Moving onto opponent only assuming we want to push, and this costs
          // one more step (two steps if it is also a log field).
          int requiredMovement = (highlight.getType() == FieldType.LOG) ? 2 : 1;
          if (i + requiredMovement > step) {
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
    for (Map.Entry<ClickedButton, GuiButton> typeWithButton : allButtons.entrySet()) {
      if (typeWithButton.getValue().wouldBeClicked(mouseX, mouseY)) {
        return typeWithButton.getKey();
      }
    }
    return ClickedButton.NONE;
  }

  public void mouseMoved(int mouseX, int mouseY) {
    for (GuiButton button: allButtons.values()) {
      button.mouseMoved(mouseX, mouseY);
    }
  }

}
