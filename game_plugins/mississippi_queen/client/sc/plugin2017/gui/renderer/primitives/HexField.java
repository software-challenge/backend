package sc.plugin2017.gui.renderer.primitives;

import java.util.EnumMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import processing.core.PApplet;
import processing.core.PImage;
import sc.plugin2017.Field;
import sc.plugin2017.FieldType;
import sc.plugin2017.gui.renderer.FrameRenderer;

/**
 * Hexagon Primitve for explanation see
 * http://grantmuller.com/drawing-a-hexagon-in-processing-java/
 */
public class HexField extends PrimitiveBase{

	private static Logger logger = LoggerFactory.getLogger(HexField.class);
  // Fields
  protected float x, y;
  protected float a, b, c;

  protected float width;
  /**
   * x position des Feldes innerhalb des Spielefeld arrays
   */
  protected int fieldX;
  /**
   * y position des Feldes innerhalb des Spielefeld arrays
   */
  protected int fieldY;

  private FieldType type;

  // graphical variant, depends also on field type
  private long variant;

  private boolean highlighted = false;

  private static EnumMap<FieldType, PImage[]> images;

  public HexField(FrameRenderer parent, float width, float startX, float startY, int offsetX, int offsetY) {
    super(parent);
    fieldX = 0;
    fieldY = 0;
    this.width = width;
    calcSize();
    calculatePosition(startX, startY, offsetX, offsetY);
    variant = 0;
  }

  // needs to be called in setup method of frame renderer (before any draw methods are called)
  public static void initImages(FrameRenderer parent) {
    images = new EnumMap<>(FieldType.class);
    images.put(FieldType.BLOCKED, new PImage[] { parent.loadImage(GuiConstants.ISLAND_IMAGE_PATH),
        parent.loadImage(GuiConstants.PASSENGER0_INACTIVE_PATH),
        parent.loadImage(GuiConstants.PASSENGER1_INACTIVE_PATH),
        parent.loadImage(GuiConstants.PASSENGER2_INACTIVE_PATH),
        parent.loadImage(GuiConstants.PASSENGER3_INACTIVE_PATH),
        parent.loadImage(GuiConstants.PASSENGER4_INACTIVE_PATH),
        parent.loadImage(GuiConstants.PASSENGER5_INACTIVE_PATH)
        });
    images.put(FieldType.WATER, new PImage[] { parent.loadImage(GuiConstants.WATER_IMAGE_PATH) });
    images.put(FieldType.PASSENGER0, new PImage[] { parent.loadImage(GuiConstants.PASSENGER0_PATH) });
    images.put(FieldType.PASSENGER1, new PImage[] { parent.loadImage(GuiConstants.PASSENGER1_PATH) });
    images.put(FieldType.PASSENGER2, new PImage[] { parent.loadImage(GuiConstants.PASSENGER2_PATH) });
    images.put(FieldType.PASSENGER3, new PImage[] { parent.loadImage(GuiConstants.PASSENGER3_PATH) });
    images.put(FieldType.PASSENGER4, new PImage[] { parent.loadImage(GuiConstants.PASSENGER4_PATH) });
    images.put(FieldType.PASSENGER5, new PImage[] { parent.loadImage(GuiConstants.PASSENGER5_PATH) });
    images.put(FieldType.SANDBANK, new PImage[] { parent.loadImage(GuiConstants.SANDBANK_IMAGE_PATH) });
    images.put(FieldType.LOG, new PImage[] { parent.loadImage(GuiConstants.LOG_IMAGE_PATH) });
    images.put(FieldType.GOAL, new PImage[] { parent.loadImage(GuiConstants.GOAL_IMAGE_PATH) });
  }

  public void update(Field field) {
    fieldX = field.getX();
    fieldY = field.getY();
    type = field.getType();
    switch (type) {
    case PASSENGER0:
      variant = 1;
      break;
    case PASSENGER1:
      variant = 2;
      break;
    case PASSENGER2:
      variant = 3;
      break;
    case PASSENGER3:
      variant = 4;
      break;
    case PASSENGER4:
      variant = 5;
      break;
    case PASSENGER5:
      variant = 6;
      break;
    case BLOCKED:
      // do not change variant to not overwrite for islands which where passenger fields!
      if (variant < 1 || variant > 6) {
        variant = 0;
      }
      break;
    default:
      variant = 0;
    }
    highlighted = false;
  }

  private void drawHex() {
    parent.beginShape();
    parent.vertex(0, a);
    parent.vertex(b, 0);
    parent.vertex(2 * b, a);
    parent.vertex(2 * b, a + c);
    parent.vertex(b, 2 * a + c);
    parent.vertex(0, a + c);
    parent.vertex(0, a);
    parent.endShape();
  }

  // draws only highlight for field because highlights need to be drawn above (e.g. after) all other fields
  public void drawHighlight() {
    if (highlighted){
      parent.pushStyle();
      parent.pushMatrix();
      parent.translate(x, y);
      parent.noFill();
      parent.strokeWeight(width / 16);
      parent.stroke(GuiConstants.colorWhite);
      drawHex();
      parent.popMatrix();
      parent.popStyle();
    }
  }

  @Override
  public void draw() {
    parent.pushStyle();
    parent.noStroke();

    parent.pushMatrix();
    parent.translate(x, y);

    try {
      if (type.isPassenger()) {
        parent.image(images.get(type)[0], 0, 0, width, 2*a+c);
      } else {
        parent.image(images.get(type)[(int)variant], 0, 0, width, 2*a+c);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      logger.error("could not get {} with variant {}", type, variant);
    }

    // print coordinates
    parent.fill(0);
    parent.textSize(a * 0.6f);
    parent.text(fieldX + "," + fieldY, width * 0.08f, a + parent.textAscent() + parent.textDescent());
    parent.popMatrix();
    parent.popStyle();
  }

  protected void calcSize() {
    b = width / 2;
    c = b / PApplet.cos(PApplet.radians(30));
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
    calcSize();
    calculatePosition(startX, startY, offsetX, offsetY);
  }

  private void calculatePosition(float startX, float startY, int offsetX, int offsetY) {
    float newX = startX;
    float newY = startY;
    if((fieldY % 2) != 0) {
      newX = newX - width / 2f;
    }
    newY += (offsetY + fieldY) * (c + a + GuiConstants.BORDERSIZE * 0.5f);
    newX += (offsetX + fieldX) * (GuiConstants.BORDERSIZE + width);
    this.x = newX;
    this.y = newY;
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

  public boolean isHighlighted() {
    return highlighted;
  }

  public void setHighlighted(boolean highlighted) {
    this.highlighted = highlighted;
  }

  @Override
  public String toString() {
    return "X: " + fieldX + " Y: " + fieldY + " Type: " + type + " " + x + " " + y + " " + b;
  }

  public HexField getFieldCoordinates(int x, int y) {
    if(this.x < x && this.x + width > x && this.y + a < y && this.y + a + c > y) {
      return this;
    }
    return null;
  }

  public FieldType getType() {
    return type;
  }

}
