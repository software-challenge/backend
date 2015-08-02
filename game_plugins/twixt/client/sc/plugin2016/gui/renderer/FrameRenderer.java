/**
 * 
 */
package sc.plugin2016.gui.renderer;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import processing.core.PImage;
import sc.plugin2016.gui.renderer.RenderConfigurationDialog;
import sc.plugin2016.EPlayerId;
import sc.plugin2016.GameState;
import sc.plugin2016.Move;
import sc.plugin2016.PlayerColor;
import sc.plugin2016.gui.renderer.primitives.Background;
import sc.plugin2016.gui.renderer.primitives.BoardFrame;
import sc.plugin2016.gui.renderer.primitives.GameEndedDialog;
import sc.plugin2016.gui.renderer.primitives.GuiBoard;
import sc.plugin2016.gui.renderer.primitives.GuiConstants;
import sc.plugin2016.gui.renderer.primitives.GuiField;
import sc.plugin2016.gui.renderer.primitives.ProgressBar;
import sc.plugin2016.gui.renderer.primitives.SideBar;
import sc.plugin2016.util.Constants;

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

  public GameState currentGameState;
  private boolean isUpdated;
  private boolean humanPlayer;
  private boolean humanPlayerMaxTurn;
  private int maxTurn;
  boolean shouldResize = true;
  private boolean myMousePressed;

  private EPlayerId id;

  public GuiBoard guiBoard;
  /*
   * TODO add background picture
   * picture is missing so no background for now*/
  private Background background;
  
  private ProgressBar progressBar;
  private SideBar sideBar;
  private BoardFrame boardFrame;


  public FrameRenderer() {
    super();

    // logger.debug("calling frameRenderer.size()");
    this.humanPlayer = false;
    this.humanPlayerMaxTurn = false;
    isUpdated = false;
    this.id = EPlayerId.OBSERVER;

    RenderConfiguration.loadSettings();

    background = new Background(this);
    logger.debug("Dimension when creating board: (" + this.width + ","
        + this.height + ")");
    guiBoard = new GuiBoard(this);
    progressBar = new ProgressBar(this);
    sideBar = new SideBar(this);


    boardFrame = new BoardFrame(this);
    // logger.debug("Constructor finished");

    // load Images
    //currently no images
  }

  public void setup() {
    maxTurn = -1;
    myMousePressed = false;
    // choosing renderer from options - using P2D as default
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

    // initial draw
    GuiConstants.generateFonts(this);
    resize(this.width, this.height);
    noLoop(); // prevent thread from starving everything else

  }

  public void draw() {
    
	if(shouldResize) {
    	resize(this.displayWidth, this.displayHeight);
    }
    shouldResize = true;
    background.draw();
    guiBoard.draw();
    progressBar.draw();
    sideBar.draw();
    boardFrame.draw();
    if (currentGameState != null && currentGameState.gameEnded()) {
      GameEndedDialog.draw(this);
    }
  }

  public void updateGameState(GameState gameState) {
    int lastTurn = -1;
    if (currentGameState != null) {
      lastTurn = currentGameState.getTurn();
    }
    currentGameState = gameState;
    if (gameState != null && gameState.getBoard() != null)
      guiBoard.update(gameState.getBoard());
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
    isUpdated = true;
    redraw();
  }

  public void requestMove(int maxTurn, EPlayerId id) {
    while (!isUpdated) {
      loop();
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
      }
    }
    isUpdated = false;
    int turn = currentGameState.getTurn();
    this.id = id;
    if (turn % 2 == 1) {
      // System.out.println("Blauer Spieler ist dran");
      if (id == EPlayerId.PLAYER_ONE) {
        // System.out.println("Spielerupdate");
        this.id = EPlayerId.PLAYER_TWO;
      }
    }
    // this.maxTurn = maxTurn;
    this.humanPlayer = true;
    humanPlayerMaxTurn = true;
    redraw();
    noLoop();
  }

  public Image getImage() {
    // TODO return an Image of the current board
    return null;
  }

  public void mouseClicked(MouseEvent e) {
    loop();
    if (isHumanPlayer() && maxTurn == currentGameState.getTurn()) {
      int x = e.getX();
      int y = e.getY();
      int player;
      if (id == EPlayerId.PLAYER_ONE) {
        player = 0;
      } else {
        player = 1;
      }
      /*
       * is button pressed?
       */
      /*
      float buttonX = getWidth() / 2f - 50;
      float buttonY = getHeight() * GuiConstants.SIDE_BAR_HEIGHT + 5;
      if (this.currentGameState.getTurn() > 7 && x > buttonX
          && y > buttonY && x < buttonX + 100 && y < buttonY + 25) {
        // System.out.println("Aussetzknopf gedrückt");
        RenderFacade.getInstance().sendMove(new NullMove());
      }*/
    }
    noLoop();
  }

  public void mousePressed(MouseEvent e) {
    loop();
    if (isHumanPlayer() && maxTurn == currentGameState.getTurn()) {
      int x = e.getX();
      int y = e.getY();
      int player;
      if (id == EPlayerId.PLAYER_ONE) {
        player = 0;
      } else {
        player = 1;
      }
      /*
       * mark possible moves
       */
    }
    noLoop();
  }

  public void mouseReleased(MouseEvent e) {
    loop();
    if (isHumanPlayer() && maxTurn == currentGameState.getTurn()) {
      int x = e.getX();
      int y = e.getY();
      int player;
      Move move = null;
      if (id == EPlayerId.PLAYER_ONE) {
        player = 0;
      } else {
        player = 1;
      }
      int[] position = getFieldCoordinates(x, y);
      if(position != null && currentGameState.getPossibleMoves().contains(new Move(position[0], position[1]))) {
        move = new Move(position[0], position[1]);
        RenderFacade.getInstance().sendMove(move);
      }
       
    
      myMousePressed = false;
      try {
        Thread.sleep(20);
      } catch (Exception ex){
        
      }
      redraw();
    }
    noLoop();
  }

  private int[] getFieldCoordinates(int x, int y) {
    for (int i = 0; i < 24; i++) {
      for (int j = 0; j < 24; j++) {
        GuiField guiField = guiBoard.getGuiFields()[i][j];
        if (x >= guiField.getX() - guiField.getWidth() / 2.0f
            && x <= guiField.getX() + guiField.getWidth() / 2.0f
            && y >= guiField.getY() - guiField.getWidth() / 2.0f
            && y <= guiField.getY() + guiField.getWidth() / 2.0f
            ) {
          return new int[] {
              guiField.getField().getX(),
              guiField.getField().getY() };
        }
      }
    }
    return null;
  }

  private boolean isFieldClicked(GuiField field, int x, int y) {
    if (x >= field.getX() - field.getWidth() / 2
        && x <= field.getX() + field.getWidth() / 2
        && y >= field.getY() - field.getWidth() / 2
        && y <= field.getY() + field.getWidth() / 2
        ) {
      return true;
    }
    return false;
  }

  public void resize(int width, int height) {
    loop();
    background.resize(width, height);
    guiBoard.resize(width, height);
    
    shouldResize = false;
  }

  /*
   * Hack! wenn das Fenster resized wird, wird setBounds aufgerufen. hier
   * rufen wir resize auf um die Komponenten auf die richtige größe zu
   * bringen.
   */
  public void setBounds(int x, int y, int width, int height) {
    // System.out.println("got an setBounds- x:" + x + ",y: " + y +
    // ",width: "
    // + width + ",height: " + height);
    super.setBounds(x, y, width, height);
    this.resize(width, height);
  }

  public void keyPressed() {
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
      GuiField[][] hf = guiBoard.getGuiFields();
      for(int i = 0; i < 8; i++) {
        for(int j = 0; j < 8; j++) {
          if(hf[i][j] != null) {
            hf[i][j].kill();
          }
        }
      }
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
}
