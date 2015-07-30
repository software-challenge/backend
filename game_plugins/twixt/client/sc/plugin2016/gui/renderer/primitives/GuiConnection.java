package sc.plugin2016.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PImage;
import sc.plugin2016.Connection;
import sc.plugin2016.PlayerColor;
import sc.plugin2016.gui.renderer.FrameRenderer;

/**
 * 
 * 
 * @author soeren
 * 
 */
public class GuiConnection extends PrimitiveBase{
  
  private float x1, y1, x2, y2;
  /**
   * x position des Feldes innerhalb des Spielefeld arrays
   */
  // private int fieldX1, fieldX2; not needed
  /**
   * y position des Feldes innerhalb des Spielefeld arrays
   */
  // private int fieldY1, fieldY2; not needed
  
  private boolean highlighted = false;
  Connection connection;

  public GuiConnection(FrameRenderer parent, Connection c) {
    super(parent);
    connection = c;
    calculatePosition(c);
  }

  private void calculatePosition(Connection c) {
    GuiField g1 = parent.guiBoard.getField(c.x1, c.y1);
    x1 = g1.getX();
    y1 = g1.getY();
    g1 = parent.guiBoard.getField(c.x2, c.y2);
    x2 = g1.getX();
    y2 = g1.getY();
    
  }

  public void update(Connection c) {
    
    this.connection = c;
      
  }

  public void draw() {
    parent.pushStyle();
    parent.noStroke();
    if(highlighted){
      parent.fill(GuiConstants.colorHighLighted);
      parent.fill(GuiConstants.colorHighLighted);
    } else if(connection.owner == PlayerColor.RED) {
      parent.fill(GuiConstants.colorRed);
      parent.stroke(GuiConstants.colorRed);
    } else {
      parent.fill(GuiConstants.colorBlue);
      parent.stroke(GuiConstants.colorBlue);
    }
  
    parent.pushMatrix();
    parent.strokeWeight(4);
    /*
    parent.line(x1, y1, x2, y2);*/
    //draw a line to the other point
    parent.curve(x1, y1, x1, y1, x2, y2, x2, y2);
    /*parent.beginShape();
    parent.vertex(x1-1, y1-1);
    parent.vertex(x1+1, y1+1);
    parent.vertex(x2+1, y2+1);
    parent.vertex(x2-1, y2-1);
    parent.vertex(x1-1, y1-1);
    parent.endShape();*/
    
    parent.fill(0);
    parent.textFont(GuiConstants.fonts[0]);
    parent.textSize(GuiConstants.fontSizes[0]);

    parent.popMatrix();
    parent.popStyle();

  }
  /*
  public void resize(float startX, float startY, float width){
    setX(startX);
    setY(startY);
    setWidth(width);
    
  }*/

  public boolean isHighlighted() {
    return highlighted;
  }

  public void setHighlighted(boolean highlighted) {
    this.highlighted = highlighted;
  }

}
