package sc.plugin2017.gui.renderer.primitives;

import processing.core.PApplet;
import sc.plugin2017.Direction;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GuiPlayer extends HexField {


  private int coal;

  private int speed;

  private int movement; // TODO

  private int passenger;

  private PlayerColor color;

  private boolean currentPlayer;

  private Direction direction;

  // player has to remember this to caclulate new positions
  private double startX;
  private double startY;
  private int offsetX;
  private int offsetY;

  public GuiPlayer(FrameRenderer parent, double width, double startX, double startY, int offsetX, int offsetY) {
    super(parent, width, startX, startY, offsetX, offsetY, 0);
    this.startX = startX;
    this.startY = startY;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  @Override
  public void draw() {

    parent.pushStyle();
    parent.noStroke();
    parent.pushMatrix();
    // translate to the center of the field
    parent.translate((float) (x + b), (float) (y + a + (c/2)));

    if(color == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
    } else {
      parent.fill(GuiConstants.colorBlue);
    }

    if (direction != null) {
      parent.rotate((float) Math.toRadians(direction.getValue() * -60));
    }

    parent.ellipseMode(PApplet.CENTER);
    parent.ellipse(0, 0, (float) c, (float) c);

    // direction arrow
    // +---
    // |   \
    // |   /
    // +---
    parent.beginShape();
    parent.vertex(0, (float) (-c/2));
    parent.vertex((float) (c/2), (float) (-c/2));
    parent.vertex((float) c, 0);
    parent.vertex((float) (c/2), (float) (c/2));
    parent.vertex(0, (float) (c/2));
    parent.endShape();

    // coal and speed
    int coalX = Math.round((float) (-c/2));
    int coalY = Math.round((float) (-a - (a/2)));
    double coalWidth = b;
    double coalHeight = a;
    int speedX = Math.round((float) (-c/2));
    int speedY = Math.round((float) (a/2));
    double speedWidth = b;
    double speedHeight = a;
    parent.strokeWeight((float) (width / 32));
    parent.fill(GuiConstants.colorBlack);
    parent.rect(coalX, coalY, (float) coalWidth, (float) coalHeight);
    parent.fill(GuiConstants.colorWhite);
    parent.rect(speedX, speedY, (float) speedWidth, (float) speedHeight);
    parent.noStroke();
    // text
    parent.pushMatrix();

    parent.translate(coalX, coalY);
    parent.rotate((float) Math.toRadians(90));
    parent.fill(GuiConstants.colorWhite);
    String coalString = String.format("%d", coal);
    // calculate optimal text size
    parent.textSize(12);
    parent.textSize((float) (12/parent.textWidth(coalString) * coalHeight));
    parent.text(coalString, 0, -parent.textDescent());

    parent.popMatrix();

    parent.pushMatrix();

    parent.translate(speedX, speedY);
    parent.rotate((float) Math.toRadians(90));
    parent.fill(GuiConstants.colorBlack);
    String speedString = String.format("%d", speed);
    // calculate optimal text size
    parent.textSize(12);
    parent.textSize((float) (12/parent.textWidth(speedString) * speedHeight));
    parent.text(speedString, 0, -parent.textDescent());

    parent.popMatrix();

    // draw passengers
    parent.fill(GuiConstants.colorPassenger);
    double passengerDiameter = width/4;
    if(passenger == 2) {
      parent.ellipse((float) (passengerDiameter/2) , 0, (float) passengerDiameter, (float) passengerDiameter);
      parent.ellipse((float) (-passengerDiameter/2) , 0, (float) passengerDiameter, (float) passengerDiameter);
    } else if(passenger == 1) {
      parent.ellipse(0 , 0, (float) passengerDiameter, (float) passengerDiameter);
    }
    parent.popMatrix();
    parent.popStyle();
  }

  @Override
  public void resize(double startX, double startY, int offsetX, int offsetY, double width){
    this.startX = startX;
    this.startY = startY;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    super.resize(startX, startY, offsetX, offsetY, width);
  }

  public void update(Player player, boolean currentPlayer) {
    this.fieldX = player.getX();
    this.fieldY = player.getY();
    resize(startX, startY, offsetX, offsetY, width);
    this.coal = player.getCoal();
    this.color = player.getPlayerColor();
    this.speed = player.getSpeed();
    this.passenger = player.getPassenger();
    this.currentPlayer = currentPlayer;
    this.direction = player.getDirection();
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public Direction getDirection() {
    return direction;
  }

}
