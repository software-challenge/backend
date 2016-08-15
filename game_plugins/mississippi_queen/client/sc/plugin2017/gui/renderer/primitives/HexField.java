package sc.plugin2017.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PImage;
import sc.plugin2017.Field;
import sc.plugin2017.FieldType;
import sc.plugin2017.gui.renderer.FrameRenderer;

/**
 * Hexagon Primitve for explanation see
 * http://grantmuller.com/drawing-a-hexagon-in-processing-java/
 * 
 * @author soeren
 * 
 */
public class HexField extends PrimitiveBase{
  
  // Fields
  private float x, y;
  private float a, b, c;
  /**
   * x position des Feldes innerhalb des Spielefeld arrays
   */
  private int fieldX;
  /**
   * y position des Feldes innerhalb des Spielefeld arrays
   */
  private int fieldY;

  private FieldType type;
  
  private boolean highlighted = false;

  public HexField(FrameRenderer parent, float startX, float startY, float width, int fieldX, int fieldY, FieldType type) {
    super(parent);
    setX(startX);
    setY(startY);
    calcSize(width);
    setFieldX(fieldX);
    setFieldY(fieldY);
  }

  public void update(Field field) {
    // TODO
    type = field.getType();
      
  }

  public void draw() {
      parent.pushStyle();
      parent.noStroke();
      System.out.println("Got here, gameState is " + parent.currentGameState.getVisibleBoard());
      //parent.text("" + this.fieldX + " " + this.fieldY, 25, 25);
      //parent.text("" + numFish, 25, 50);
      if(Field.isPassable(type)) {
        if(highlighted){
          parent.fill(GuiConstants.colorHexFieldsHighlight);
        } else {
          parent.fill(GuiConstants.colorHexFields);
        }
      } else {
        parent.fill(GuiConstants.colorHexFieldIsland);
      }
      
      parent.pushMatrix();
      parent.translate(getX(), getY());
  
      parent.beginShape();
      parent.vertex(0, a);
      parent.vertex(b, 0);
      parent.vertex(2 * b, a);
      parent.vertex(2 * b, a + getC());
      parent.vertex(b, 2 * a + getC());
      parent.vertex(0, a + getC());
      parent.vertex(0, a);
      parent.endShape();
      
      if(Field.isPassengerField(type)) {
        // TODO place image of passenger
      }
      
      parent.fill(0);
      parent.textFont(GuiConstants.fonts[0]);
      parent.textSize(GuiConstants.fontSizes[0]);
      parent.popMatrix();
      parent.popStyle();
  }

  private void calcSize(float width) {
    b = width / 2;
    setC(b / PApplet.cos(PApplet.radians(30)));
    a = b * PApplet.sin(PApplet.radians(30));
  }
  
  public void resize(float startX, float startY, float width){
    // TODO even and odd rows
    setX(startX + (width + GuiConstants.BORDERSIZE) * fieldX);
    setY(startY + (width + GuiConstants.BORDERSIZE) * fieldY);
    calcSize(width);
    
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getA() {
    return this.a;
  }

  public float getB() {
    return this.b;
  }

  public int getFieldX() {
    return fieldX;
  }

  public void setFieldX(int fieldX) {
    this.fieldX = fieldX;
  }

  public int getFieldY() {
    return fieldY;
  }

  public void setFieldY(int fieldY) {
    this.fieldY = fieldY;
  }

  /**
   * @return the c
   */
  public float getC() {
    return c;
  }

  /**
   * @param c the c to set
   */
  private void setC(float c) {
    this.c = c;
  }

  public boolean isHighlighted() {
    return highlighted;
  }

  public void setHighlighted(boolean highlighted) {
    this.highlighted = highlighted;
  }

}
