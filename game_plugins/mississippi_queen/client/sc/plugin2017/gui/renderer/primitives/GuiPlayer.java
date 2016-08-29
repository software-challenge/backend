package sc.plugin2017.gui.renderer.primitives;

import processing.core.PApplet;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GuiPlayer extends PrimitiveBase {


  private float x, y;

  /**
   * x position des Feldes innerhalb des Spielefeld arrays
   */
  private int fieldX;
  /**
   * y position des Feldes innerhalb des Spielefeld arrays
   */
  private int fieldY;

  public int coal;

  public int speed;

  public int movement; // TODO

  public int passenger;

  private float width;

  private float c;

  private float b;
  private float a;

  private PlayerColor color;

  private boolean currentPlayer;

  private int direction;

  public GuiPlayer(FrameRenderer parent) {
    super(parent);
  }

  @Override
  public void draw() {

    parent.pushStyle();
    parent.noStroke();
    parent.pushMatrix();
    // translate to the center of the field
    parent.translate(x + b, y + a + (c/2));

    if(parent.currentGameState != null && parent.currentGameState.getBoard() != null && parent.currentGameState.getBoard().getTiles().size() > 2) {
      parent.textFont(GuiConstants.fonts[3]);
      parent.textSize(GuiConstants.fontSizes[3]);
    }

    if(color == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
    } else {
      parent.fill(GuiConstants.colorBlue);
    }

    parent.rotate((float) Math.toRadians(direction * -60));

    parent.ellipseMode(PApplet.CENTER);
    parent.ellipse(0, 0, c, c);

    // direction arrow
    // +---
    // |   \
    // |   /
    // +---
    parent.beginShape();
    parent.vertex(0, -c/2);
    parent.vertex(c/2, -c/2);
    parent.vertex(c, 0);
    parent.vertex(c/2, c/2);
    parent.vertex(0, c/2);
    parent.endShape();

    // coal and speed
    int coalX = Math.round(-c/2);
    int coalY = Math.round(-a - (a/2));
    float coalWidth = b;
    float coalHeight = a;
    int speedX = Math.round(-c/2);
    int speedY = Math.round(a/2);
    float speedWidth = b;
    float speedHeight = a;
    parent.strokeWeight(width / 32);
    parent.fill(GuiConstants.colorBlack);
    parent.rect(coalX, coalY, coalWidth, coalHeight);
    parent.fill(GuiConstants.colorWhite);
    parent.rect(speedX, speedY, speedWidth, speedHeight);
    parent.noStroke();
    // text
    parent.pushMatrix();

    parent.translate(coalX, coalY);
    parent.rotate((float) Math.toRadians(90));
    parent.fill(GuiConstants.colorWhite);
    String coalString = String.format("%d", coal);
    // calculate optimal text size
    parent.textSize(12);
    parent.textSize(12/parent.textWidth(coalString) * coalHeight);
    parent.text(coalString, 0, -coalWidth, coalHeight, coalWidth);

    parent.popMatrix();

    parent.pushMatrix();

    parent.translate(speedX, speedY);
    parent.rotate((float) Math.toRadians(90));
    parent.fill(GuiConstants.colorBlack);
    String speedString = String.format("%d", speed);
    // calculate optimal text size
    parent.textSize(12);
    parent.textSize(12/parent.textWidth(speedString) * speedHeight);
    parent.text(speedString, 0, -speedWidth, speedHeight, speedWidth);

    parent.popMatrix();

    // draw passengers
    parent.fill(GuiConstants.colorPassenger);
    float passengerDiameter = width/4;
    if(passenger == 2) {
      parent.ellipse(passengerDiameter/2 , 0, passengerDiameter, passengerDiameter);
      parent.ellipse(-passengerDiameter/2 , 0, passengerDiameter, passengerDiameter);
    } else if(passenger == 1) {
      parent.ellipse(0 , 0, passengerDiameter, passengerDiameter);
    }

    parent.fill(0);
    parent.textFont(GuiConstants.fonts[0]);
    parent.textSize(GuiConstants.fontSizes[0]);
    parent.popMatrix();
    parent.popStyle();

  }

  public void resize(float startX, float startY, int offsetX, int offsetY, float width) {
    this.width = width;
    calcSize(width);
    float newX = startX;
    float newY = startY;
    if((fieldY % 2) != 0) {
      newX = newX - width / 2f;
    }
    newY += (offsetY + fieldY) * (c + HexField.calcA(width) + GuiConstants.BORDERSIZE);
    newX += (offsetX + fieldX) * (GuiConstants.BORDERSIZE + width);
    this.x = newX;
    this.y = newY;

  }

  private void calcSize(float width) {
    b = width / 2;
    c = b / PApplet.cos(PApplet.radians(30));
    a = b * PApplet.sin(PApplet.radians(30));
  }

  public void update(Player player, boolean currentPlayer) {
    this.fieldX = player.getX();
    this.fieldY = player.getY();
    continue here: need to update x and y!
    this.coal = player.getCoal();
    this.color = player.getPlayerColor();
    this.speed = player.getSpeed();
    this.passenger = player.getPassenger();
    this.currentPlayer = currentPlayer;
    this.direction = player.getDirection();
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

}
