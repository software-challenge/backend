/**
 *
 */
package sc.plugin2017.gui.renderer;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.EnumMap;
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
import sc.plugin2017.WinCondition;
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

  private GuiBoard guiBoard;

  private Background background;

  private ProgressBar progressBar;
  private SideBar sideBar;
  private BoardFrame boardFrame;

  private boolean initialized = false;

  private LinkedHashMap<HexField, Action> stepPossible;
  private WinCondition winCondition;
  private EnumMap<EPlayerId, Boolean> humanPlayers;
  public FrameRenderer() {
    super();

    RenderConfiguration.loadSettings();

    // RenderFacade will tell us when a human player joins so that we can save
    // it.
    this.humanPlayers = new EnumMap<>(EPlayerId.class);
    for (EPlayerId val : EPlayerId.values()) {
      this.humanPlayers.put(val, Boolean.FALSE);
    }

    this.background = new Background(this);
    this.guiBoard = new GuiBoard(this);
    this.progressBar = new ProgressBar(this);
    this.sideBar = new SideBar(this);
    this.boardFrame = new BoardFrame(this);
    this.stepPossible = new LinkedHashMap<HexField, Action>();
  }

  @Override
  public void setup() {
    super.setup();
    logger.debug("Dimension when creating board: (" + this.width + ","
        + this.height + ")");
    // choosing renderer from options - using P2D as default (currently it seems
    // that only the java renderer works).
    //
    // NOTE that setting the size needs to be the first action of the setup
    // method (as stated in the processing reference).
    if (RenderConfiguration.optionRenderer.equals("JAVA2D")) {
      logger.debug("Using Java2D as Renderer");
      size(this.width, this.height, JAVA2D);
    } else if (RenderConfiguration.optionRenderer.equals("P3D")) {
      logger.debug("Using P3D as Renderer");
      size(this.width, this.height, P3D);
    } else {
      logger.debug("Using P2D as Renderer");
      size(this.width, this.height, P2D);
    }
    smooth(RenderConfiguration.optionAntiAliasing); // Anti Aliasing

    GuiConstants.generateFonts(this);
    // same font is used everywhere
    textFont(GuiConstants.font);

    HexField.initImages(this);
    this.guiBoard.setup();
    // only draw when needed (application calls redraw() if needed). Letting the loop run results in 100% (or high) CPU activity
    noLoop();
    this.initialized = true;
  }

  @Override
  public void draw() {
    if (!this.initialized) {
      // do not try to draw before setup method was not called
      return;
    }
    this.background.draw();
    this.guiBoard.draw();
    this.progressBar.draw();
    this.sideBar.draw();
    this.boardFrame.draw();
    if (!gameActive()) {
      drawEndGameScreen(this.winCondition);
    }
  }

  public void endGame(WinCondition condition) {
    this.winCondition = condition;
    redraw();
  }

  private void drawEndGameScreen(WinCondition condition) {
    String winnerName = null;
    if (condition.getWinner() == PlayerColor.RED) {
      winnerName = this.currentGameState.getRedPlayer().getDisplayName();
    } else if (condition.getWinner() == PlayerColor.BLUE) {
      winnerName = this.currentGameState.getBluePlayer().getDisplayName();
    }
    GameEndedDialog.draw(this, condition, winnerName);
  }

  public void updateGameState(GameState gameState) {
    // FIXME: winCondition determines if the game end screen is drawn, when
    // going back in the replay/game, it has to be cleared. Setting it to null
    // here works, but there has to be a better way.
    this.winCondition = null;
    try {
      this.currentGameState = gameState.clone();
    } catch (CloneNotSupportedException e) {
      logger.error("Problem cloning gamestate", e);
    }
    this.currentMove = new Move();
    // needed for simulation of actions
    this.currentGameState.getRedPlayer().setMovement(this.currentGameState.getRedPlayer().getSpeed());
    this.currentGameState.getBluePlayer().setMovement(this.currentGameState.getBluePlayer().getSpeed());
    this.currentGameState.getCurrentPlayer().setFreeTurns(this.currentGameState.isFreeTurn() ? 2 : 1);
    this.currentGameState.getCurrentPlayer().setFreeAcc(1);
    // make backup of gameState
    try {
      this.backUp = this.currentGameState.clone();
    } catch (CloneNotSupportedException e) {
      logger.error("Clone of Backup failed", e);
    }

    if (gameState != null && gameState.getBoard() != null) {
      logger.debug("updating gui board gamestate");
      updateView(this.currentGameState);
    } else {
      logger.error("got gamestate without board");
    }

    redraw();
  }

  /**
   * Is called when a human player should input a move.
   * @param maxTurn TODO
   * @param id The player who needs to move.
   */
  public void requestMove(int maxTurn, EPlayerId id) {
    logger.debug("request move with {} for player {}", maxTurn, id);
    updateView(this.currentGameState);
  }

  public Image getImage() {
    // TODO return an Image of the current board
    return null;
  }

  private void updateView(GameState gameState) {
    if (gameState != null && gameState.getBoard() != null) {
      gameState.getRedPlayer().setPoints(gameState.getPointsForPlayer(PlayerColor.RED));
      gameState.getBluePlayer().setPoints(gameState.getPointsForPlayer(PlayerColor.BLUE));
      this.boardFrame.update(gameState.getCurrentPlayerColor());
      this.sideBar.update(gameState.getCurrentPlayerColor(), gameState.getRedPlayer().getDisplayName(), gameState.getPointsForPlayer(PlayerColor.RED), gameState.getBluePlayer().getDisplayName(), gameState.getPointsForPlayer(PlayerColor.BLUE));
      this.guiBoard.update(gameState.getVisibleBoard(), gameState.getRedPlayer(),
          gameState.getBluePlayer(), gameState.getCurrentPlayerColor(), this.currentMove);
    } else {
      this.boardFrame.update(null);
      this.sideBar.update(null);
    }
    redraw();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    super.mouseMoved(e);
    this.guiBoard.mouseMoved(this.mouseX, this.mouseY);
    redraw();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    super.mouseClicked(e);
    if (currentPlayerIsHuman()) {

      boolean onSandbank = this.currentGameState.getCurrentPlayer().getField( this.currentGameState.getBoard()).getType() == FieldType.SANDBANK;
      int currentSpeed = this.currentGameState.getCurrentPlayer().getSpeed();
      switch (this.guiBoard.getClickedButton(this.mouseX, this.mouseY)) {
      case LEFT:
        if (!onSandbank) {
          this.currentMove.actions.add(new Turn(1));
        }
        break;
      case RIGHT:
        if (!onSandbank) {
          this.currentMove.actions.add(new Turn(-1));
        }
        break;
      case SPEED_UP:
        if (!onSandbank && currentSpeed < 6) {
          if (!this.currentMove.actions.isEmpty() && this.currentMove.actions.get(this.currentMove.actions.size() - 1).getClass() == Acceleration.class) {
            // if last action was acceleration, increase value
            Acceleration a = (Acceleration)this.currentMove.actions.get(this.currentMove.actions.size() - 1);
            if (a.acc == -1) {
              this.currentMove.actions.remove(a);
            } else {
              a.acc += 1;
            }
          } else {
            this.currentMove.actions.add(new Acceleration(1));
          }
        }
        break;
      case SPEED_DOWN:
        if (!onSandbank && currentSpeed > 1) {
          if (!this.currentMove.actions.isEmpty() && this.currentMove.actions.get(this.currentMove.actions.size() - 1).getClass() == Acceleration.class) {
            // if last action was acceleration, decrease value
            Acceleration a = (Acceleration)this.currentMove.actions.get(this.currentMove.actions.size() - 1);
            if (a.acc == 1) {
              this.currentMove.actions.remove(a);
            } else {
              a.acc -= 1;
            }
          } else {
            this.currentMove.actions.add(new Acceleration(-1));
          }
        }
        break;
      case SEND:
        sendMove();
        break;
      case CANCEL:
        try {
          this.currentGameState = this.backUp.clone();
          this.currentMove = new Move();
        } catch (CloneNotSupportedException ex) {
          logger.error("Clone of backup failed", ex);
        }
        updateGameState(this.currentGameState);
        break;
      case NONE:
        // if no button was clicked, check if a hex field was clicked
        HexField clicked = getFieldCoordinates(this.mouseX, this.mouseY);
        if (this.stepPossible.containsKey(clicked)) {
          this.currentMove.actions.add(this.stepPossible.get(clicked));
        }
        break;
      }

      // Gamestate needs always be reset, even when action list is empty because
      // the emptyness may be the result of a removed acceleration in which case
      // the velocity needs to be reset.
      try {
        this.currentGameState = this.backUp.clone();
      } catch (CloneNotSupportedException ex) {
        logger.error("Clone of backup failed", ex);
      }
      if (!this.currentMove.actions.isEmpty()) {
        try {
          // perform actions individually because it is a partial move and should not be checked for validity
          for (Action action : this.currentMove.actions) {
            action.perform(this.currentGameState, this.currentGameState.getCurrentPlayer());
          }
        } catch (InvalidMoveException invalMove) {
          logger.error("Failed to perform move of user, please report if this happens", invalMove);
        }
      }
      updateView(this.currentGameState);
    }
  }

  private void sendMove() {
    this.currentMove.setOrderInActions();
    if (!currentMoveValid(this.currentMove)) {
      if (JOptionPane.showConfirmDialog(null, "Der Zug ist ungültig. Durch senden des aktuellen Zuges werden Sie disqualifiziert. Zug wirklich senden?", "Senden", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
        // do not send move
        return;
      }
    }
    RenderFacade.getInstance().sendMove(this.currentMove);
  }

  // NOTE that this method assumes the given move was already performed on the currentGameState!
  private boolean currentMoveValid(Move move) {
    boolean allMovementPointsUsed = this.currentGameState.getCurrentPlayer().getMovement() == 0;
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

    for (GuiTile tile : this.guiBoard.getTiles()) {
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
    if (this.key == 'c' || this.key == 'C') {
      new RenderConfigurationDialog(FrameRenderer.this);
      redraw();
    }
  }

  public EPlayerId getId() {
    return RenderFacade.getInstance().getActivePlayer();
  }

  public void killAll() {
    noLoop();
    if(this.background != null) {
      this.background.kill();
    }
    if(this.guiBoard != null) {
      this.guiBoard.kill();
    }
    if(this.progressBar != null) {
      this.progressBar.kill();
    }
    if(this.sideBar != null) {
      this.sideBar.kill();
    }
    if(this.boardFrame != null) {
      this.boardFrame.kill();
    }
  }

  public boolean currentPlayerIsHuman() {
    return this.humanPlayers.get(getId());
  }

  public Player getCurrentPlayer() {
    if (this.currentGameState != null) {
      return this.currentGameState.getCurrentPlayer();
    } else {
      return null;
    }
  }

  public void setPossibleSteps(LinkedHashMap<HexField, Action> add) {
    this.stepPossible = add;
  }

  public Field getCurrentPlayerField() {
    if (this.currentGameState != null && this.currentGameState.getBoard() != null) {
      return this.currentGameState.getCurrentPlayer().getField(this.currentGameState.getBoard());
    } else {
      return null;
    }
  }

  public int getCurrentRound() {
    if (this.currentGameState != null) {
      return this.currentGameState.getRound();
    } else {
      return 0;
    }
  }

  public boolean gameActive() {
    return this.winCondition == null;
  }

  public List<DebugHint> getCurrentHints() {
    if (this.currentGameState != null && this.currentGameState.getLastMove() != null) {
      return this.currentGameState.getLastMove().getHints();
    } else {
      return Collections.emptyList();
    }
  }

  public List<Action> getCurrentActions() {
    if (this.currentMove != null && this.currentMove.actions != null) {
      return this.currentMove.actions;
    } else {
      return Collections.emptyList();
    }
  }

  public Player getCurrentOpponent() {
    if (this.currentGameState != null) {
      return this.currentGameState.getOtherPlayer();
    } else {
      return null;
    }
  }

  public boolean playerControlsEnabled() {
    return currentPlayerIsHuman();
  }

  public void setHuman(EPlayerId target) {
    this.humanPlayers.put(target, Boolean.TRUE);
  }
}
