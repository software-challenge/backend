/**
 *
 */
package sc.plugin2017.gui.renderer;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import sc.plugin2017.Acceleration;
import sc.plugin2017.Action;
import sc.plugin2017.DebugHint;
import sc.plugin2017.EPlayerId;
import sc.plugin2017.Field;
import sc.plugin2017.FieldType;
import sc.plugin2017.GameState;
import sc.plugin2017.Move;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.Turn;
import sc.plugin2017.gui.renderer.primitives.Background;
import sc.plugin2017.gui.renderer.primitives.BoardFrame;
import sc.plugin2017.gui.renderer.primitives.GameEndedDialog;
import sc.plugin2017.gui.renderer.primitives.GuiBoard;
import sc.plugin2017.gui.renderer.primitives.GuiConstants;
import sc.plugin2017.gui.renderer.primitives.GuiTile;
import sc.plugin2017.gui.renderer.primitives.HexField;
import sc.plugin2017.gui.renderer.primitives.ProgressBar;
import sc.plugin2017.gui.renderer.primitives.SideBar;
import sc.plugin2017.util.InvalidMoveException;

/**
 * @author soeren
 */

public class FrameRenderer extends PApplet {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory
      .getLogger(FrameRenderer.class);

  private GameState currentGameState;
  private GameState backUp;
  private Move currentMove;
  private boolean humanPlayer;
  private boolean humanPlayerMaxTurn;
  private int maxTurn;
  private EPlayerId id;

  private GuiBoard guiBoard;

  private Background background;

  private ProgressBar progressBar;
  private SideBar sideBar;
  private BoardFrame boardFrame;

  private boolean initialized = false;

  private LinkedHashMap<HexField, Action> stepPossible;

  public FrameRenderer() {
    super();

    this.humanPlayer = false;
    this.humanPlayerMaxTurn = false;
    this.id = EPlayerId.OBSERVER;

    RenderConfiguration.loadSettings();

    background = new Background(this);
    guiBoard = new GuiBoard(this);
    progressBar = new ProgressBar(this);
    sideBar = new SideBar(this);
    boardFrame = new BoardFrame(this);
    stepPossible = new LinkedHashMap<HexField, Action>();
  }

  @Override
  public void setup() {
    super.setup();
    logger.debug("Dimension when creating board: (" + width + ","
        + height + ")");
    maxTurn = -1;
    // choosing renderer from options - using P2D as default (currently it seems
    // that only the java renderer works).
    //
    // NOTE that setting the size needs to be the first action of the setup
    // method (as stated in the processing reference).
    if (RenderConfiguration.optionRenderer.equals("JAVA2D")) {
      logger.debug("Using Java2D as Renderer");
      size(width, height, JAVA2D);
    } else if (RenderConfiguration.optionRenderer.equals("P3D")) {
      logger.debug("Using P3D as Renderer");
      size(width, height, P3D);
    } else {
      logger.debug("Using P2D as Renderer");
      size(width, height, P2D);
    }
    smooth(RenderConfiguration.optionAntiAliasing); // Anti Aliasing

    GuiConstants.generateFonts(this);
    // same font is used everywhere
    textFont(GuiConstants.font);

    HexField.initImages(this);
    guiBoard.setup();
    initialized = true;
  }

  @Override
  public void draw() {
    if (!initialized) {
      // do not try to draw before setup method was not called
      return;
    }
    background.draw();
    guiBoard.draw();
    progressBar.draw();
    sideBar.draw();
    boardFrame.draw();
    if (currentGameState != null && currentGameState.gameEnded()) {
      GameEndedDialog.draw(this, currentGameState);
    }
  }

  public void updateGameState(GameState gameState) {
    int lastTurn = -1;
    if (currentGameState != null) {
      lastTurn = currentGameState.getTurn();
    }
    currentGameState = gameState;
    currentMove = new Move();
    // needed for simulation of actions
    currentGameState.getRedPlayer().setMovement(currentGameState.getRedPlayer().getSpeed());
    currentGameState.getBluePlayer().setMovement(currentGameState.getBluePlayer().getSpeed());
    currentGameState.getCurrentPlayer().setFreeTurns(currentGameState.isFreeTurn() ? 2 : 1);
    currentGameState.getCurrentPlayer().setFreeAcc(1);
    // make backup of gameState
    try {
      backUp = currentGameState.clone();
    } catch (CloneNotSupportedException e) {
      logger.error("Clone of Backup failed", e);
    }

    // TODO document what this code does
    if ((currentGameState == null || lastTurn == currentGameState.getTurn() - 1)) {

      if (maxTurn == currentGameState.getTurn() - 1) {

        maxTurn++;
        humanPlayerMaxTurn = false;
      }
    }
    humanPlayer = false;
    if (currentGameState != null && maxTurn == currentGameState.getTurn()
        && humanPlayerMaxTurn) {
      humanPlayer = true;
    }

    if (gameState != null && gameState.getBoard() != null) {
      logger.debug("updating gui board gamestate");
      updateView(currentGameState);
    } else {
      logger.error("got gamestate without board");
    }

    redraw();
  }

  public void requestMove(int maxTurn, EPlayerId id) {
    int turn = currentGameState.getTurn();
    this.id = id;
    if (turn % 2 == 1) {
      if (id == EPlayerId.PLAYER_ONE) {
        this.id = EPlayerId.PLAYER_TWO;
      }
    }
    // this.maxTurn = maxTurn;
    this.humanPlayer = true;
    humanPlayerMaxTurn = true;
    updateView(currentGameState);
  }

  public Image getImage() {
    // TODO return an Image of the current board
    return null;
  }

  private void updateView(GameState gameState) {
    if (gameState != null && gameState.getBoard() != null) {
      gameState.getRedPlayer().setPoints(gameState.getPointsForPlayer(PlayerColor.RED));
      gameState.getBluePlayer().setPoints(gameState.getPointsForPlayer(PlayerColor.BLUE));
      boardFrame.update(gameState.getCurrentPlayerColor());
      sideBar.update(gameState.getCurrentPlayerColor(), gameState.getRedPlayer().getDisplayName(), gameState.getPointsForPlayer(PlayerColor.RED), gameState.getBluePlayer().getDisplayName(), gameState.getPointsForPlayer(PlayerColor.BLUE));
      guiBoard.update(gameState.getVisibleBoard(), gameState.getRedPlayer(),
          gameState.getBluePlayer(), gameState.getCurrentPlayerColor(), currentMove);
    } else {
      boardFrame.update(null);
      sideBar.update(null);
    }
    redraw();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    super.mouseMoved(e);
    if (guiBoard.hoversButton(mouseX, mouseY)) {
      cursor(HAND);
    } else {
      cursor(ARROW);
    }
    redraw();
  }

  public boolean playerControlsEnabled() {
    // current player needs to be human and the current turn needs to be the
    // last one already played (because we can jump forward and backward)
    return isHumanPlayer() && maxTurn == currentGameState.getTurn();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    super.mouseClicked(e);
    if(playerControlsEnabled()) {

      // first the gui buttons
      Action action = null;
      boolean onSandbank = currentGameState.getCurrentPlayer().getField( currentGameState.getBoard()).getType() == FieldType.SANDBANK;
      int currentSpeed = currentGameState.getCurrentPlayer().getSpeed();
      switch (guiBoard.getClickedButton(mouseX, mouseY)) {
      case LEFT:
        if (!onSandbank) action = new Turn(1);
        break;
      case RIGHT:
        if (!onSandbank) action = new Turn(-1);
        break;
      case SPEED_UP:
        if (!onSandbank && currentSpeed < 6) action = new Acceleration(1);
        break;
      case SPEED_DOWN:
        if (!onSandbank && currentSpeed > 1) action = new Acceleration(-1);
        break;
      case SEND:
        sendMove();
        break;
      case CANCEL:
        try {
          currentGameState = backUp.clone();
        } catch (CloneNotSupportedException ex) {
          logger.error("Clone of backup failed", ex);
        }
        updateGameState(currentGameState);
        break;
      case NONE:
        // do nothing
        break;
      }

      // then field clicks
      if (action == null) {
        HexField clicked = getFieldCoordinates(mouseX, mouseY);
        action = stepPossible.get(clicked);
      }

      if (action != null) {
          currentMove.actions.add(action);
          try {
            action.perform(currentGameState, currentGameState.getCurrentPlayer());
          } catch (InvalidMoveException invalMove) {
            logger.error("Failed to perform move of user, please report if this happens", invalMove);
          }
      }
      updateView(currentGameState);
      redraw();
    }
  }

  private void sendMove() {
    Move move = new Move();
    Acceleration acceleration = null;
    for (Action action : currentMove.actions) {
      // bundle accelerations which are at the beginning of the move
      if (action.getClass() == Acceleration.class) {
        if (acceleration == null) {
          acceleration = (Acceleration)action;
        } else {
          acceleration.acc += ((Acceleration)action).acc;
        }
      } else {
        if (acceleration != null) {
          move.actions.add(acceleration);
          acceleration = null;
        }
        move.actions.add(action);
      }
    }
    move.setOrderInActions();
    if (!currentMoveValid(move)) {
      if (JOptionPane.showConfirmDialog(null, "Der Zug ist ungültig. Durch senden des aktuellen Zuges werden Sie disqualifiziert. Zug wirklich senden?", "Senden", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
        // do not send move
        return;
      }
    }
    RenderFacade.getInstance().sendMove(move);
  }

  private boolean currentMoveValid(Move move) {
    boolean allMovementPointsUsed = currentGameState.getCurrentPlayer().getMovement() == 0;
    boolean accelerationFirst = true;
    // test if any action after the first one is an acceleration action
    for (int i = 1; i < move.actions.size(); i++) {
      if (move.actions.get(i).getClass() == Acceleration.class) {
        accelerationFirst = false;
      }
    }
    return allMovementPointsUsed && accelerationFirst;
  }

  private HexField getFieldCoordinates(int x, int y) {
    HexField coordinates;

    for (GuiTile tile : guiBoard.getTiles()) {
      coordinates = tile.getFieldCoordinates(x,y);
      if(coordinates != null) {
        return coordinates;
      }
    }
    return null;
  }

  @Override
  public void keyPressed() {
    super.keyPressed();
    if (key == 'c' || key == 'C') {
      new RenderConfigurationDialog(FrameRenderer.this);
    }
  }

  public boolean isHumanPlayer() {
    return humanPlayer;
  }

  public EPlayerId getId() {
    return id;
  }

  public void killAll() {
    noLoop();
    if(background != null) {
      background.kill();
    }
    if(guiBoard != null) {
      guiBoard.kill();
    }
    if(progressBar != null) {
      progressBar.kill();
    }
    if(sideBar != null) {
      sideBar.kill();
    }
    if(boardFrame != null) {
      boardFrame.kill();
    }
  }

  public boolean currentPlayerIsHuman() {
    return humanPlayer;
  }

  public Player getCurrentPlayer() {
    if (currentGameState != null) {
      return currentGameState.getCurrentPlayer();
    } else {
      return null;
    }
  }

  public void setPossibleSteps(LinkedHashMap<HexField, Action> add) {
    stepPossible = add;
  }

  public Field getCurrentPlayerField() {
    if (currentGameState != null && currentGameState.getBoard() != null) {
      return currentGameState.getCurrentPlayer().getField(currentGameState.getBoard());
    } else {
      return null;
    }
  }

  public int getCurrentRound() {
    if (currentGameState != null) {
      return currentGameState.getRound();
    } else {
      return 0;
    }
  }

  public boolean gameActive() {
    if (currentGameState != null) {
      return !currentGameState.gameEnded();
    } else {
      return false;
    }
  }

  public List<DebugHint> getCurrentHints() {
    if (currentGameState != null && currentGameState.getLastMove() != null) {
      return currentGameState.getLastMove().getHints();
    } else {
      return Collections.emptyList();
    }
  }

  public List<Action> getCurrentActions() {
    if (currentMove != null && currentMove.actions != null) {
      return currentMove.actions;
    } else {
      return Collections.emptyList();
    }
  }

  public Player getCurrentOpponent() {
    if (currentGameState != null) {
      return currentGameState.getOtherPlayer();
    } else {
      return null;
    }
  }
}
