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
  
  public HexField(FrameRenderer parent) {
    super(parent);
    fieldX = 0;
    fieldY = 0;
  }

  public void update(Field field) {
    fieldX = field.getX();
    fieldY = field.getY();
    type = field.getType();
      
  }

  public void draw() {

    System.out.println(this);
    parent.pushStyle();
    parent.noStroke();
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
    parent.translate(x, y);

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
    if(true) {
      parent.text(fieldX + "," + fieldY, 0, 30);
    }
    parent.popMatrix();
    parent.popStyle();
  }

  private void calcSize(float width) {
    b = width / 2;
    setC(b / PApplet.cos(PApplet.radians(30)));
    a = b * PApplet.sin(PApplet.radians(30));
  }
  

  public static float calcA(float width) {
    
    return (width / 2f) * PApplet.sin(PApplet.radians(30));
  }
  
  public static float calcB(float width) {
      
      return (width / 2f);
    }
  
  public static float calcC(float width) {
    
    return (width / 2f) / PApplet.cos(PApplet.radians(30));
  }
    
  public void resize(float startX, float startY, int offsetX, int offsetY, float width){
    // TODO check
    calcSize(width);
    float newX = startX;
    float newY = startY;
    if((fieldY % 2) != 0) {
      newX = newX - width / 2f;
    }
    newY += (offsetY + fieldY) * (c + a + GuiConstants.BORDERSIZE);
    newX += (offsetX + fieldX) * (GuiConstants.BORDERSIZE + width);
    this.x = newX;
    this.y = newY;
    
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
  
  public String toString() {
    return "X: " + fieldX + " Y: " + fieldY + " Type: " + type + " " + x + " " + y + " " + b;
  }
}
