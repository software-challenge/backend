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
      float xLength = (viewPortSize.width / (this.maxFieldsInX + 1f))
          /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      float yLength = (viewPortSize.height / (this.maxFieldsInY + 1f))
          /* 1+ für eventuelle Verschiebung */ - GuiConstants.BORDERSIZE;
      this.width = Math.min(xLength, yLength);
      this.offsetX = -lowX;
      this.offsetY = -lowY;
      float sizeX = (this.width + GuiConstants.BORDERSIZE);
      float sizeY = (HexField.calcA(this.width) + HexField.calcC(this.width) + GuiConstants.BORDERSIZE);
      this.startX = (viewPortSize.width - (sizeX * this.maxFieldsInX)) / 2f;
      this.startY = (viewPortSize.height - (sizeY * this.maxFieldsInY)) / 2f;
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


  private void drawOvertakeLine(Player currentPlayer, Player opponentPlayer) {
  // TODO Auto-generated method stub

    logger.debug("drawing overtake line");
    // drawing the overtakeline on the left and right (in respect to front most tile direction)
    // of the enemy player with either
    // left zig-zag (straight line, then first turn is to the left)
    // right zig-zag (first turn is to the right)
    // if player is slower, then right version on left and left version on right
    // if player is faster, then left                      right
    // same speed, but more / less coal (see above)
    // same speed, same coal -> right / down direction are new cases


    Field opponentField = opponentPlayer.getField(currentBoard);
    int fieldX = opponentField.getX();
    int fieldY = opponentField.getY();
    // these are the pixel coordinates of the center
    float pixelX = calculateXPosition(fieldX, fieldY);
    float pixelY = calculateYPosition(fieldX, fieldY);
    pixelX += width / 2f;
    pixelY += (HexField.calcC(width) + 2f * HexField.calcA(width)) / 2f;
    logger.debug("opponent field pixel coordinates: " + pixelX + ", " + pixelY);
    // the direction in degrees
    int direction = currentBoard.getTiles().get(currentBoard.getTiles().size() - 1).getDirection() * -60;
    int speedDif = currentPlayer.getSpeed() - opponentPlayer.getSpeed();
    int coalDif = currentPlayer.getCoal() - opponentPlayer.getCoal();
    if (speedDif > 0) {
      drawLeftZigZag(pixelX, pixelY, direction - 90);
      drawRightZigZag(pixelX, pixelY, direction + 90);
    } else if (speedDif < 0) {
      drawRightZigZag(pixelX, pixelY, direction - 90);
      drawLeftZigZag(pixelX, pixelY, direction + 90);

    } else {

    }
  }

  private void drawRightZigZag(float pixelX, float pixelY, int direction) {
    // TODO Auto-generated method stub
    parent.pushStyle();

    parent.pushMatrix();
    parent.translate(pixelX, pixelY);
    float c = HexField.calcC(width);
    float rightTurn = (float) Math.toRadians(60);
    float leftTurn = (float) Math.toRadians(-60);

    parent.rotate((float) Math.toRadians(direction));
    logger.debug("direction in degrees: {}", direction);
    parent.translate(HexField.calcA(width) + c / 2f, 0);
    parent.stroke(GuiConstants.colorBlack);
    parent.strokeWeight(width / 32);
    parent.line(0, 0, c, 0);
    parent.translate(c, 0);
    parent.rotate(rightTurn);
    parent.line(0, 0, c, 0);
    parent.translate(c, 0);
    parent.rotate(leftTurn);
    parent.line(0, 0, c, 0);
    parent.translate(c, 0);
    parent.rotate(leftTurn);
    parent.line(0, 0, c, 0);
    parent.translate(c, 0);
    parent.rotate(rightTurn);
    parent.line(0, 0, c, 0);
    parent.translate(c, 0);

    parent.popMatrix();
    parent.popStyle();
  }

  private void drawLeftZigZag(float pixelX, float pixelY, int direction) {
    // TODO Auto-generated method stub
    parent.pushStyle();

    parent.pushMatrix();
    parent.translate(pixelX, pixelY);




    parent.popMatrix();
    parent.popStyle();
  }

  /**
   * Calculates the pixel x coordinate for given 2d hex field coordinates
   */
  private float calculateXPosition(int fieldX, int fieldY) {
    float newX = startX;
    if((fieldY % 2) != 0) {
      newX = newX - width / 2f;
    }
    newX += (offsetX + fieldX) * (GuiConstants.BORDERSIZE + width);
    return newX;
  }

  /**
   * Calculates the pixel y coordinate for given 2d hex field coordinates
   */
  private float calculateYPosition(int fieldX, int fieldY) {
    float newY = startY;
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
      int centerX = Math.round(currentGuiPlayer.getX() + (this.width / 2));
      int centerY = Math.round(currentGuiPlayer.getY() + (this.width / 2));

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
    float xDimension = this.parent.getWidth() * GuiConstants.GUI_BOARD_WIDTH;
    float yDimension = this.parent.getHeight() * GuiConstants.GUI_BOARD_HEIGHT;
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
    if(parent.endOfRound()) { // only draw this if overtaking would change player (new round starts)
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
    return Math.max(1, Math.round(this.width / 3));
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
