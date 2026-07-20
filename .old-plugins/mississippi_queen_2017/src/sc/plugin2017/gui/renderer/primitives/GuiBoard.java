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
  private double startX;
  private double startY;
  private int offsetX;
  private int offsetY;
  /**
   * Width of one field
   */
  private double width;

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

    this.red = new GuiPlayer(parent, this.width, this.startX, this.startY, this.offsetX, this.offsetY);
    this.blue = new GuiPlayer(parent, this.width, this.startX, this.startY, this.offsetX, this.offsetY);
    this.tiles = new LinkedList<GuiTile>();
    for (int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
      this.tiles.add(new GuiTile(parent, i, this.width, this.startX, this.startY, this.offsetX, this.offsetY));
    }
    createButtons(calculateButtonSize());
  }

  public void setup() {
    for (GuiButton button : this.allButtons.values()) {
      button.setup();
    }
  }

  private void createButtons(int size) {
    this.allButtons = new EnumMap<>(ClickedButton.class);
    this.left = new GuiButton(this.parent, GuiConstants.ROTATE_LEFT_IMAGE_PATH, "Drehung nach links", 0, 0, size);
    this.allButtons.put(ClickedButton.LEFT, this.left);
    this.right = new GuiButton(this.parent, GuiConstants.ROTATE_RIGHT_IMAGE_PATH, "Drehung nach rechts", 0, 0, size);
    this.allButtons.put(ClickedButton.RIGHT, this.right);
    this.speedUp = new GuiButton(this.parent, GuiConstants.INCREASE_IMAGE_PATH, "Beschleunigen", 0, 0, size);
    this.allButtons.put(ClickedButton.SPEED_UP, this.speedUp);
    this.speedDown = new GuiButton(this.parent, GuiConstants.DECREASE_IMAGE_PATH, "Abbremsen", 0, 0, size);
    this.allButtons.put(ClickedButton.SPEED_DOWN, this.speedDown);
    this.send = new GuiButton(this.parent, GuiConstants.OKAY_IMAGE_PATH, "Zug beenden", 0, 0, size);
    this.allButtons.put(ClickedButton.SEND, this.send);
    this.cancel = new GuiButton(this.parent, GuiConstants.CANCEL_IMAGE_PATH, "Zug erneut eingeben", 0, 0, size);
    this.allButtons.put(ClickedButton.CANCEL, this.cancel);
  }

  /**
   * sets width, maxFieldsInX, maxFieldsInY, startX, startY, offset according to
   * dim and currentBoard
   */
  private void calcHexFieldSize(Dimension viewPortSize) {
    // before getting the initial board we can't (and don't have to) calculate sizes
    if (this.currentBoard != null) {
      int lowX = 500;
      int highX = -500;
      int lowY = 500;
      int highY = -500;
      for (Tile tile : this.currentBoard.getTiles()) {
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
      this.maxFieldsInX = highX - lowX + 1;
      this.maxFieldsInY = highY - lowY + 1;
      double xLength = viewPortSize.width / (this.maxFieldsInX + 1.0)
          /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      double yLength = viewPortSize.height / (this.maxFieldsInY + 1.0)
          /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      this.width = Math.min(xLength, yLength);
      this.offsetX = -lowX;
      this.offsetY = -lowY;
      double sizeX = this.width + GuiConstants.BORDERSIZE;
      double sizeY = HexField.calcA(this.width) + HexField.calcC(this.width) + GuiConstants.BORDERSIZE;
      this.startX = (viewPortSize.width - (sizeX * this.maxFieldsInX)) / 2.0;
      this.startY = (viewPortSize.height - (sizeY * this.maxFieldsInY)) / 2.0;
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
   * @param currentMove
   *          the currently entered (maybe incomplete) move
   */
  public void update(Board board, Player red, Player blue, PlayerColor current, Move currentMove) {
    if (board == null) {
      logger.error("got no board in update");
    }
    this.currentBoard = board;
    calculateSize();
    Player currentPlayer = (current == PlayerColor.RED) ? red : blue;
    this.red.update(red, current == PlayerColor.RED);
    this.blue.update(blue, current == PlayerColor.BLUE);
    updateButtonPositions(getCurrentGuiPlayer());
    updateButtonAvailability(currentPlayer, this.currentBoard, currentMove);
    if (!this.currentBoard.getTiles().isEmpty()) {

      // I think this sets old tiles invisible (because they are no longer
      // included in the currentBoard and updates visible tiles.
      // TODO hard to understand, refactor
      int toUpdate = 0;
      int index = this.currentBoard.getTiles().get(0).getIndex();
      for (int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
        if (index != i) {
          this.tiles.get(i).setVisible(false);
        } else {
          this.tiles.get(index).setVisible(true);
          this.tiles.get(index).update(this.currentBoard.getTiles().get(toUpdate));
          ++toUpdate;
          if (toUpdate < this.currentBoard.getTiles().size()) {
            index = this.currentBoard.getTiles().get(toUpdate).getIndex();
          }
        }
      }

      if (this.parent.playerControlsEnabled()) {
        LinkedList<HexField> toHighlight = new LinkedList<>();
        LinkedHashMap<HexField, Action> add = new LinkedHashMap<>();
        Field redField = red.getField(this.currentBoard);
        Field blueField = blue.getField(this.currentBoard);
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
        } else if (currentPlayer.getField(this.currentBoard).getType() != FieldType.SANDBANK) {
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
          if (this.parent.getCurrentPlayer().getMovement() != 0) {
            HexField step = getPassableGuiFieldInDirection(currentPlayer.getX(), currentPlayer.getY(),
                currentPlayer.getDirection(), this.parent.getCurrentPlayer().getMovement());
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
        this.parent.setPossibleSteps(add);
      } else {
        logger.debug("no human player");
      }
    } else {
      logger.debug("no tiles");
    }
  }


  /**
   * drawing the overtake-line on the left and right (in respect to front most tile direction)
   * of the enemy player with either
   * left zig-zag (straight line, then first turn is to the left)
   * right zig-zag (first turn is to the right)
   * if player is slower, then right version on left and left version on right
   * if player is faster, then left                      right
   * same speed, but more / less coal (see above)
   * same speed, same coal -> right / down direction are new cases
   *
   * this method does NOT work with these following special cases:
   * - When a new Tile would be added, the overtake-line may not represent the "real" overtake-line,
   *   since the direction of the last tile could change
   * - a field that is represented as "in front" of the other player may not lead to overtaking, if
   *   the field that is stepped on is a log- or sandbank-field, since the speed is changed in that case.
   */
  private void drawOvertakeLine(Player currentPlayer, Player opponentPlayer) {
    logger.debug("drawing overtake line");
    Field opponentField = opponentPlayer.getField(currentBoard);
    int fieldX = opponentField.getX();
    int fieldY = opponentField.getY();
    // these are the pixel coordinates of the center
    double pixelX = calculateXPosition(fieldX, fieldY);
    double pixelY = calculateYPosition(fieldY);
    pixelX += width / 2f;
    pixelY += (HexField.calcC(width) + 2f * HexField.calcA(width)) / 2f;
    // the direction in degrees
    int direction = currentBoard.getTiles().get(currentBoard.getTiles().size() - 1).getDirection();
    direction = ((direction  * -60) + 360) % 360; // direction in degrees between +0 and +360 degrees
    int speedDiff = currentPlayer.getSpeed() - opponentPlayer.getSpeed();
    int coalDiff = currentPlayer.getCoal() - opponentPlayer.getCoal();
    int leftLine = (direction + 270) % 360;
    int rightLine = (direction + 90) % 360;
    if (speedDiff > 0) {
      drawLeftZigZag(pixelX, pixelY, leftLine);
      drawRightZigZag(pixelX, pixelY, rightLine);
    } else if (speedDiff < 0) {
      drawRightZigZag(pixelX, pixelY, leftLine);
      drawLeftZigZag(pixelX, pixelY, rightLine);
    } else if (coalDiff > 0) {
      drawLeftZigZag(pixelX, pixelY, leftLine);
      drawRightZigZag(pixelX, pixelY, rightLine);
    } else if (coalDiff < 0) {
      drawRightZigZag(pixelX, pixelY, leftLine);
      drawLeftZigZag(pixelX, pixelY, rightLine);
    } else if (direction == 60 || direction == 120 || direction == 180) { // all these three cases have the same behavior
      drawLeftZigZag(pixelX, pixelY, leftLine);
      drawLeftZigZag(pixelX, pixelY, rightLine);
    } else { // direction in {0, 240, 300}
      drawRightZigZag(pixelX, pixelY, leftLine);
      drawRightZigZag(pixelX, pixelY, rightLine);
    }
  }

  /**
   * This draws a zig zag line between the corners of fields beginning with a right turn
   */
  private void drawRightZigZag(double pixelX, double pixelY, int direction) {
    drawZigZag(pixelX, pixelY, direction, false);
  }

  /**
   * This draws a zig zag line between the corners of fields beginning with a left turn
   */
  private void drawLeftZigZag(double pixelX, double pixelY, int direction) {
    drawZigZag(pixelX, pixelY, direction, true);
  }

  private void drawZigZag(double pixelX, double pixelY, int direction, boolean left) {
    parent.pushStyle();

    parent.pushMatrix();
    parent.translate((float) pixelX, (float) pixelY);
    int rightTurn = 60;
    int leftTurn = 300; // 300 degrees to the right is 60 to the left in java
    moveToCorner(direction);
    parent.stroke(GuiConstants.colorOvertakeLine);
    parent.strokeWeight((float) (width / 32));
    for (int i = 0; i < 10; ++i) {
      if (left) {
        drawLine(direction);
        drawLine((direction + leftTurn) % 360);
        drawLine(direction);
        drawLine((direction + rightTurn) % 360);
      } else {
        drawLine(direction);
        drawLine((direction + rightTurn) % 360);
        drawLine(direction);
        drawLine((direction + leftTurn) % 360);
      }
    }

    parent.popMatrix();
    parent.popStyle();
  }

  /**
   * draws a single line matching the field grid in specified direction (in degrees between 0 and 360)
   */
  private void drawLine(int direction) {
    double a = HexField.calcA(width);
    double b = HexField.calcB(width);
    double c = HexField.calcC(width);

    // we don't use rotation here because the drawn hex fields are not equilateral hexagons.
    // this would cause angles in the grid to be not exactly 60 degrees
    switch(direction) {
    case 30:
      parent.line(0, 0, (float) b, (float) a);
      parent.translate((float) b, (float) a);
      break;
    case 90:
      parent.line(0, 0, 0, (float) c);
      parent.translate(0, (float) c);
      break;
    case 150:
      parent.line(0, 0, (float) (-b), (float) a);
      parent.translate((float) (-b), (float) a);
      break;
    case 210:
      parent.line(0, 0, (float) (-b), (float) (-a));
      parent.translate((float) (-b), (float) (-a));
      break;
    case 270:
      parent.line(0, 0, 0, (float) (-c));
      parent.translate(0, (float) (-c));
      break;
    case 330:
      parent.line(0, 0, (float) b, (float) (-a));
      parent.translate((float) b, (float) (-a));
      break;
    default:
      logger.error("Drawing overtakeline has occured an error");
    }
  }

  /**
   * Moves the parent from the center of a field to the corner specified by the direction (in degrees between 0 and 360)
   */
  private void moveToCorner(int direction) {
    double a = HexField.calcA(width);
    double b = HexField.calcB(width);
    double c = HexField.calcC(width);

    // we don't use rotation here because the drawn hex fields are not equilateral hexagons.
    // this would cause angles in the grid to be not exactly 60 degrees
    switch(direction) {
    case 30:
      parent.translate((float) b, (float) (c / 2));
      break;
    case 90:
      parent.translate(0, (float) (c / 2 + a));
      break;
    case 150:
      parent.translate((float) (-b), (float) (c / 2));
      break;
    case 210:
      parent.translate((float) (-b), (float) (-c / 2));
      break;
    case 270:
      parent.translate(0, (float) (-c / 2 - a));
      break;
    case 330:
      parent.translate((float) b, (float) (-c / 2));
      break;
    default:
      logger.error("Drawing overtakeline has occured an error");
    }
  }

  /**
   * Calculates the pixel x coordinate for given 2d hex field coordinates
   */
  private double calculateXPosition(int fieldX, int fieldY) {
    double newX = startX;
    if((fieldY % 2) != 0) {
      newX = newX - width / 2f;
    }
    newX += (offsetX + fieldX) * (GuiConstants.BORDERSIZE + width);
    return newX;
  }

  /**
   * Calculates the pixel y coordinate for given 2d hex field coordinates
   */
  private double calculateYPosition(int fieldY) {
    double newY = startY;
    newY += (offsetY + fieldY) * (HexField.calcC(width) + HexField.calcA(width) + GuiConstants.BORDERSIZE * 0.5f);
    return newY;
  }

  private GuiPlayer getCurrentGuiPlayer() {
    Player currentPlayer = parent.getCurrentPlayer();
    if (currentPlayer != null) {
      if (currentPlayer.getPlayerColor() == PlayerColor.RED) {
        return this.red;
      } else {
        return this.blue;
      }
    } else {
      return null;
    }
  }

  private void updateButtonPositions(GuiPlayer currentGuiPlayer) {
    if (currentGuiPlayer != null) {
      int centerX = (int) Math.round(currentGuiPlayer.getX() + (this.width / 2));
      int centerY = (int) Math.round(currentGuiPlayer.getY() + (this.width / 2));

      this.left.moveTo(centerX - (this.width / 2), centerY - (this.width / 2));
      this.right.moveTo(centerX + (this.width / 2), centerY - (this.width / 2));

      this.speedUp.moveTo(centerX + (this.width / 2), centerY - (this.width / 6));
      this.speedDown.moveTo(centerX + (this.width / 2), centerY + (this.width / 6));

      this.send.moveTo(centerX + (this.width / 5), centerY + (this.width / 2));
      this.cancel.moveTo(centerX - (this.width / 5), centerY + (this.width / 2));
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

    this.speedUp.setEnabled(!maxSpeed && !onSandbank && accelerationPossible && firstAction);
    this.speedDown.setEnabled(!minSpeed && !onSandbank && accelerationPossible && firstAction);
    this.left.setEnabled(rotationPossible && !onSandbank);
    this.right.setEnabled(rotationPossible && !onSandbank);
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
   */
  private void calculateSize() {
    double xDimension = this.parent.getWidth() * GuiConstants.GUI_BOARD_WIDTH;
    double yDimension = this.parent.getHeight() * GuiConstants.GUI_BOARD_HEIGHT;
    Dimension viewPortSize = new Dimension((int) xDimension, (int) yDimension);
    calcHexFieldSize(viewPortSize);
  }

  @Override
  public void draw() {
    resize();
    for (GuiTile tile : this.tiles) {
      tile.draw();
    }
    // highlights need to be drawn above fields
    if (this.parent.playerControlsEnabled()) {
      for (GuiTile tile : this.tiles) {
        tile.drawHighlights();
      }
    }
    // draw players
    this.red.draw();
    this.blue.draw();

    // draw overtake line
    if(parent.endOfRound() && parent.currentPlayerIsHuman()) { // only draw this if overtaking would change player (new round starts)
      drawOvertakeLine(parent.getCurrentPlayer(), parent.getCurrentOpponent());
    }

    // buttons
    if (this.parent.playerControlsEnabled()) {
      this.left.draw();
      this.right.draw();
      this.speedUp.draw();
      this.speedDown.draw();
      this.send.draw();
      this.cancel.draw();
    }
  }

  /**
   * should be called when the parents dimensions change
   */
  public void resize() {
    calculateSize();
    for (GuiTile tile : this.tiles) {
      tile.resize(this.startX, this.startY, this.offsetX, this.offsetY, this.width);
    }
    this.red.resize(this.startX, this.startY, this.offsetX, this.offsetY, this.width);
    this.blue.resize(this.startX, this.startY, this.offsetX, this.offsetY, this.width);
    resizeButtons();
    updateButtonPositions(getCurrentGuiPlayer());
  }

  private void resizeButtons() {
    if (this.left != null && this.right != null && this.speedUp != null && this.speedDown != null && this.send != null && this.cancel != null) {
      int newSize = calculateButtonSize();
      this.left.resize(newSize);
      this.right.resize(newSize);
      this.speedUp.resize(newSize);
      this.speedDown.resize(newSize);
      this.send.resize(newSize);
      this.cancel.resize(newSize);
    }
  }

  private int calculateButtonSize() {
    return Math.max(1, (int) Math.round(this.width / 3));
  }

  @Override
  public void kill() {

    if (this.red != null) {
      this.red.kill();
    }
    if (this.blue != null) {
      this.blue.kill();
    }
    for (GuiTile tile : this.tiles) {
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
    Player opponent = this.parent.getCurrentOpponent();
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
    for (GuiTile tile : this.tiles) {
      HexField field = tile.getHexField(x, y);
      if (field != null) {
        return field;
      }
    }
    return null;
  }

  public List<GuiTile> getTiles() {
    return this.tiles;
  }

  public ClickedButton getClickedButton(int mouseX, int mouseY) {
    for (Map.Entry<ClickedButton, GuiButton> typeWithButton : this.allButtons.entrySet()) {
      if (typeWithButton.getValue().wouldBeClicked(mouseX, mouseY)) {
        return typeWithButton.getKey();
      }
    }
    return ClickedButton.NONE;
  }

  public void mouseMoved(int mouseX, int mouseY) {
    for (GuiButton button: this.allButtons.values()) {
      button.mouseMoved(mouseX, mouseY);
    }
  }

}
