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
  public float x, y;
  private float a, b, c;
  
  private float width;
  /**
   * x position des Feldes innerhalb des Spielefeld arrays
   */
  public int fieldX;
  /**
   * y position des Feldes innerhalb des Spielefeld arrays
   */
  public int fieldY;

  public FieldType type;
  
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
    highlighted = false;
      
  }

  public void draw() {
    parent.pushStyle();
    parent.noStroke();
    //parent.text("" + this.fieldX + " " + this.fieldY, 25, 25);
    //parent.text("" + numFish, 25, 50);
    
    if(type == FieldType.WATER) {
      parent.fill(GuiConstants.colorHexFields);
      
    } else if(type == FieldType.SANDBANK){
      parent.fill(GuiConstants.colorHexFieldSANDBANK);
    } else if(type == FieldType.LOG){
      parent.fill(GuiConstants.colorHexFieldLOG);
    } else if(type == FieldType.BLOCKED || Field.isPassengerField(type)){
      parent.fill(GuiConstants.colorHexFieldIsland);
    } else if(type == FieldType.GOAL){
      parent.fill(GuiConstants.colorHexFieldGOAL);
    }
    if(highlighted){
      parent.strokeWeight(width / 16);
      parent.stroke(GuiConstants.colorWhite);
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
    parent.noStroke();
    if(Field.isPassengerField(type)) {
      parent.fill(GuiConstants.colorPassenger);
      // TODO if passengerfield draw passenger and step
      parent.ellipse(width / 2, 17 * width / 32, width / 4, width / 4);
      parent.fill(GuiConstants.colorHexFieldLOG);
      if(type == FieldType.PASSENGER0) {
        parent.beginShape();
        parent.vertex(3 * width / 4, a);
        parent.vertex(3 * width / 4, a + c);
        parent.vertex(width, a + c);
        parent.vertex(width, a);
        parent.endShape();
      } else if(type == FieldType.PASSENGER1) {
        parent.beginShape();
        parent.vertex(b , 0);
        parent.vertex(width , a);
        parent.vertex(width - a / 2, 2 * a);
        parent.vertex(b - a / 2, a);
        parent.endShape();
        
      } else if(type == FieldType.PASSENGER2) {
        parent.beginShape();
        parent.vertex(0, a);
        parent.vertex(b, 0);
        parent.vertex(b + a / 2, a);
        parent.vertex(a / 2, 2 * a);
        parent.endShape();
        
      } else if(type == FieldType.PASSENGER3) {
        parent.beginShape();
        parent.vertex(0, a);
        parent.vertex(b / 2 , a);
        parent.vertex(b / 2, a + c);
        parent.vertex(0, a + c);
        parent.endShape();
        
      } else if(type == FieldType.PASSENGER4) {
        parent.beginShape();
        parent.vertex(b, c + a + a);
        parent.vertex(0, a + c);
        parent.vertex(a / 2, c);
        parent.vertex(b + a / 2, c + a);
        parent.endShape();
        
      } else if(type == FieldType.PASSENGER5) {
        parent.beginShape();
        parent.vertex(b, c + 2 * a);
        parent.vertex(width, a + c);
        parent.vertex(width - a / 2, c);
        parent.vertex(b - a / 2, a + c);
        parent.endShape();
        
      }
    }
    
    parent.fill(0);
    parent.textFont(GuiConstants.fonts[3]);
    parent.textSize(GuiConstants.fontSizes[3]);
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
    this.width = width;
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

  public HexField getFieldCoordinates(int x, int y) {
    if(this.x < x && this.x + width > x && this.y + a < y && this.y + a + c > y) {
      return this;
    }
    return null;
  }
}
