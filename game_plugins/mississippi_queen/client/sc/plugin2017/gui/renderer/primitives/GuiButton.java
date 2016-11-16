package sc.plugin2017.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PShape;
import sc.plugin2017.gui.renderer.FrameRenderer;

/**
 * quadratic image button
 */
public class GuiButton extends PrimitiveBase {

  // side length of button
  private int size;

  // center coordinates of button
  private int x;
  private int y;

  private boolean enabled = true;
  private boolean hovered = false;

  private String imagePath;
  private PShape image;

  private String toolTip;

  public GuiButton(FrameRenderer parent, String imagePath, String toolTip, int x, int y, int size) {
    super(parent);
    this.imagePath = imagePath;
    this.x = x;
    this.y = y;
    this.size = size;
    this.toolTip = toolTip;
  }

  public void setup() {
    this.image = parent.loadShape(this.imagePath);
  }

  @Override
  public void draw() {
    if (image != null && enabled) {
      parent.shapeMode(PApplet.CENTER);
      parent.pushStyle();
      /*
       * changing the style of a loaded shape can only be done when using P2D as
       * renderer (not with the Java renderer), see
       * https://github.com/processing/processing/issues/2035#issuecomment-23725447
      image.disableStyle();
      image.setStroke(parent.color(100));
      */
      int buttonSize;
      if (hovered && enabled) {
        buttonSize = Math.round(size * 1.3f);
      } else {
        buttonSize = size;
      }
      parent.shape(image, x, y, buttonSize, buttonSize);
      parent.popStyle();
    }
  }

  /**
   * Method to determine if given mouse coordinates are inside the bounds of the button.
   * @param mouseX X-coordinate of mouse pointer.
   * @param mouseY X-coordinate of mouse pointer.
   * @return true if coordinates are inside the bounds of the button.
   */
  public boolean hover(int mouseX, int mouseY) {
    return (Math.abs(x - mouseX) < size / 2) && (Math.abs(y - mouseY) < size / 2);
  }

  public void resize(int newSize) {
    size = newSize;
  }

  /**
   * Tests if a click at given coordinated would activate the button.
   * @param mouseX X-coordinate of mouse pointer.
   * @param mouseY X-coordinate of mouse pointer.
   * @return true if a button on current mouse coordinated would be clicked.
   */
  public boolean wouldBeClicked(int mouseX, int mouseY) {
    return hover(mouseX, mouseY) && enabled;
  }

  public void moveTo(double newX, double newY) {
    x = (int) Math.round(newX);
    y = (int) Math.round(newY);
  }

  public void setEnabled(boolean e) {
    enabled = e;
  }

  public void mouseMoved(int mouseX, int mouseY) {
    hovered = hover(mouseX, mouseY);
  }

}
