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
    parent.translate(x, y);
    
    if(color == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
    } else {
      parent.fill(GuiConstants.colorBlue);
    }
    parent.ellipse(width / 2, 17 * width / 32, c, c);
    switch (direction) {
    case 0:
      parent.beginShape();
      parent.vertex(width / 2, a);
      parent.vertex( 3 * width / 4, a);
      parent.vertex(width, a + c / 2);
      parent.vertex(3 * width / 4, a + c);
      parent.vertex(width / 2, a + c);
      parent.endShape();
      // coal and speed
      parent.strokeWeight(width / 32);
      if(color == PlayerColor.RED) {
        parent.stroke(GuiConstants.colorRed);
      } else {
        parent.stroke(GuiConstants.colorBlue);
      }
      parent.fill(GuiConstants.colorBlack);
      parent.rect(width / 4, width / 8, b, a);
      parent.fill(GuiConstants.colorWhite);
      parent.rect(width / 4, 15 * width / 16 - a,b,a);
      parent.noStroke();
      // text
      parent.rotate((float) Math.toRadians(90));
      parent.fill(GuiConstants.colorWhite);
      parent.text(coal,   5 * width / 32, - 3 * width / 8);
      parent.fill(GuiConstants.colorBlack);
      parent.text(speed, 23 * width / 32 ,- 3 * width / 8);
      parent.rotate((float) Math.toRadians(-90));
      // draw passengers
      parent.fill(GuiConstants.colorPassenger);
      if(passenger == 2) {
        parent.ellipse(3 * width / 8, 17 * width / 32 , width / 4, width / 4);
        parent.ellipse(5 * width / 8, 17 * width / 32,  width / 4, width / 4);
      } else if(passenger == 1) {
        parent.ellipse(width / 2, 17 * width / 32 , width / 4, width / 4);
      }
      break;
    case 1:
      parent.rotate((float) Math.toRadians(-60));
      parent.translate(-  23 * width / 32, 5 * width / 32);
      parent.beginShape();
      parent.vertex(width / 2, a);
      parent.vertex( 3 * width / 4, a);
      parent.vertex(width, a + c / 2);
      parent.vertex(3 * width / 4, a + c);
      parent.vertex(width / 2, a + c);
      parent.endShape();
      // coal and speed
      parent.strokeWeight(width / 32);
      if(color == PlayerColor.RED) {
        parent.stroke(GuiConstants.colorRed);
      } else {
        parent.stroke(GuiConstants.colorBlue);
      }
      parent.fill(GuiConstants.colorBlack);
      parent.rect(width / 4, width / 8, b, a);
      parent.fill(GuiConstants.colorWhite);
      parent.rect(width / 4, 15 * width / 16 - a,b,a);
      parent.noStroke();
      // text
      parent.rotate((float) Math.toRadians(90));
      parent.fill(GuiConstants.colorWhite);
      parent.text(coal,   5 * width / 32, - 3 * width / 8);
      parent.fill(GuiConstants.colorBlack);
      parent.text(speed, 23 * width / 32 ,- 3 * width / 8);
      parent.rotate((float) Math.toRadians(-90));
      // draw passengers
      parent.fill(GuiConstants.colorPassenger);
      if(passenger == 2) {
        parent.ellipse(3 * width / 8, 17 * width / 32 , width / 4, width / 4);
        parent.ellipse(5 * width / 8, 17 * width / 32,  width / 4, width / 4);
      } else if(passenger == 1) {
        parent.ellipse(width / 2, 17 * width / 32 , width / 4, width / 4);
      }
      break;
    case 2:
      parent.rotate((float) Math.toRadians(-120));
      parent.translate( - 39 * width / 32, -  3 * width / 8);
      parent.beginShape();
      parent.vertex(width / 2, a);
      parent.vertex( 3 * width / 4, a);
      parent.vertex(width, a + c / 2);
      parent.vertex(3 * width / 4, a + c);
      parent.vertex(width / 2, a + c);
      parent.endShape();
      // coal and speed
      parent.strokeWeight(width / 32);
      if(color == PlayerColor.RED) {
        parent.stroke(GuiConstants.colorRed);
      } else {
        parent.stroke(GuiConstants.colorBlue);
      }
      parent.fill(GuiConstants.colorBlack);
      parent.rect(width / 4, width / 8, b, a);
      parent.fill(GuiConstants.colorWhite);
      parent.rect(width / 4, 15 * width / 16 - a,b,a);
      parent.noStroke();
      // text
      parent.rotate((float) Math.toRadians(90));
      parent.fill(GuiConstants.colorWhite);
      parent.text(coal,   5 * width / 32, - 3 * width / 8);
      parent.fill(GuiConstants.colorBlack);
      parent.text(speed, 23 * width / 32 ,- 3 * width / 8);
      parent.rotate((float) Math.toRadians(-90));
      // draw passengers
      parent.fill(GuiConstants.colorPassenger);
      if(passenger == 2) {
        parent.ellipse(3 * width / 8, 17 * width / 32 , width / 4, width / 4);
        parent.ellipse(5 * width / 8, 17 * width / 32,  width / 4, width / 4);
      } else if(passenger == 1) {
        parent.ellipse(width / 2, 17 * width / 32 , width / 4, width / 4);
      }
      break;
    case 3:
      parent.rotate((float) Math.toRadians(180));
      parent.translate(- width,- 17 * width / 16);
      parent.beginShape();
      parent.vertex(width / 2, a);
      parent.vertex( 3 * width / 4, a);
      parent.vertex(width, a + c / 2);
      parent.vertex(3 * width / 4, a + c);
      parent.vertex(width / 2, a + c);
      parent.endShape();
      // coal and speed
      parent.strokeWeight(width / 32);
      if(color == PlayerColor.RED) {
        parent.stroke(GuiConstants.colorRed);
      } else {
        parent.stroke(GuiConstants.colorBlue);
      }
      parent.fill(GuiConstants.colorBlack);
      parent.rect(width / 4, width / 8, b, a);
      parent.fill(GuiConstants.colorWhite);
      parent.rect(width / 4, 15 * width / 16 - a,b,a);
      parent.noStroke();
      // text
      parent.rotate((float) Math.toRadians(90));
      parent.fill(GuiConstants.colorWhite);
      parent.text(coal,   5 * width / 32, - 3 * width / 8);
      parent.fill(GuiConstants.colorBlack);
      parent.text(speed, 23 * width / 32 ,- 3 * width / 8);
      parent.rotate((float) Math.toRadians(-90));
      // draw passengers
      parent.fill(GuiConstants.colorPassenger);
      if(passenger == 2) {
        parent.ellipse(3 * width / 8, 17 * width / 32 , width / 4, width / 4);
        parent.ellipse(5 * width / 8, 17 * width / 32,  width / 4, width / 4);
      } else if(passenger == 1) {
        parent.ellipse(width / 2, 17 * width / 32 , width / 4, width / 4);
      }
      break;
    case 4:
      parent.rotate((float) Math.toRadians(120));
      parent.translate(- 9 * width / 32, -  79 * width / 64);
      parent.beginShape();
      parent.vertex(width / 2, a);
      parent.vertex( 3 * width / 4, a);
      parent.vertex(width, a + c / 2);
      parent.vertex(3 * width / 4, a + c);
      parent.vertex(width / 2, a + c);
      parent.endShape();
      // coal and speed
      parent.strokeWeight(width / 32);
      if(color == PlayerColor.RED) {
        parent.stroke(GuiConstants.colorRed);
      } else {
        parent.stroke(GuiConstants.colorBlue);
      }
      parent.fill(GuiConstants.colorBlack);
      parent.rect(width / 4, width / 8, b, a);
      parent.fill(GuiConstants.colorWhite);
      parent.rect(width / 4, 15 * width / 16 - a,b,a);
      parent.noStroke();
      // text
      parent.rotate((float) Math.toRadians(90));
      parent.fill(GuiConstants.colorWhite);
      parent.text(coal,   5 * width / 32, - 3 * width / 8);
      parent.fill(GuiConstants.colorBlack);
      parent.text(speed, 23 * width / 32 ,- 3 * width / 8);
      parent.rotate((float) Math.toRadians(-90));
      // draw passengers
      parent.fill(GuiConstants.colorPassenger);
      if(passenger == 2) {
        parent.ellipse(3 * width / 8, 17 * width / 32 , width / 4, width / 4);
        parent.ellipse(5 * width / 8, 17 * width / 32,  width / 4, width / 4);
      } else if(passenger == 1) {
        parent.ellipse(width / 2, 17 * width / 32 , width / 4, width / 4);
      }
      break;
    case 5:
      parent.rotate((float) Math.toRadians(60));
      parent.translate( 13 * width / 64, - 45 * width / 64);
      parent.beginShape();
      parent.vertex(width / 2, a);
      parent.vertex( 3 * width / 4, a);
      parent.vertex(width, a + c / 2);
      parent.vertex(3 * width / 4, a + c);
      parent.vertex(width / 2, a + c);
      parent.endShape();
      // coal and speed
      parent.strokeWeight(width / 32);
      if(color == PlayerColor.RED) {
        parent.stroke(GuiConstants.colorRed);
      } else {
        parent.stroke(GuiConstants.colorBlue);
      }
      parent.fill(GuiConstants.colorBlack);
      parent.rect(width / 4, width / 8, b, a);
      parent.fill(GuiConstants.colorWhite);
      parent.rect(width / 4, 15 * width / 16 - a,b,a);
      parent.noStroke();
      // text
      parent.rotate((float) Math.toRadians(90));
      parent.fill(GuiConstants.colorWhite);
      parent.text(coal,   5 * width / 32, - 3 * width / 8);
      parent.fill(GuiConstants.colorBlack);
      parent.text(speed, 23 * width / 32 ,- 3 * width / 8);
      parent.rotate((float) Math.toRadians(-90));
      // draw passengers
      parent.fill(GuiConstants.colorPassenger);
      if(passenger == 2) {
        parent.ellipse(3 * width / 8, 17 * width / 32 , width / 4, width / 4);
        parent.ellipse(5 * width / 8, 17 * width / 32,  width / 4, width / 4);
      } else if(passenger == 1) {
        parent.ellipse(width / 2, 17 * width / 32 , width / 4, width / 4);
      }
      break;
    default:
      break;
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
    this.coal = player.getCoal();
    this.color = player.getPlayerColor();
    this.speed = player.getSpeed();
    this.passenger = player.getPassenger();
    this.currentPlayer = currentPlayer;
    this.direction = player.getDirection();
  }

}
