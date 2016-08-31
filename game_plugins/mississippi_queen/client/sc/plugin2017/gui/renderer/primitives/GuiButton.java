package sc.plugin2017.gui.renderer.primitives;

import processing.core.PApplet;
import processing.core.PShape;
import sc.plugin2017.gui.renderer.FrameRenderer;

/**
 * circular button
 */
public class GuiButton extends PrimitiveBase {

  // diameter of button
  private int size;

  // center coordinates of button
  private int x;
  private int y;

  private boolean enabled = true;

  private String imagePath;
  private PShape image;

  public GuiButton(FrameRenderer parent, String imagePath, int x, int y, int size) {
    super(parent);
    this.imagePath = imagePath;
    this.image = parent.loadShape(imagePath);
    this.x = x;
    this.y = y;
    this.size = size;
  }

  @Override
  public void draw() {
    if (image != null) {
      parent.shapeMode(PApplet.CENTER);
      parent.pushStyle();
      parent.fill(100);
      parent.stroke(100);
      /*
       * changing the style of a loaded shape can only be done when using P2D as
       * renderer (not with the Java renderer), see
       * https://github.com/processing/processing/issues/2035#issuecomment-23725447
      image.disableStyle();
      image.setStroke(parent.color(100));
      */
      parent.shape(image, x, y, size, size);
      parent.popStyle();
    }
  }

  /**
   * Method to determine if given mouse coordinates are inside the bounds of the button.
   * @param mouseX
   * @param mouseY
   * @return true if mouseX and mouseY are inside the bounds of the button.
   */
  public boolean hover(int mouseX, int mouseY) {
    return PApplet.dist(x, y, mouseX, mouseY) <= (size / 2);
  }

  public void resize(int newSize) {
    size = newSize;
  }

  public void moveTo(float newX, float newY) {
    x = Math.round(newX);
    y = Math.round(newY);
  }

  public void setEnabled(boolean e) {
    enabled = e;
  }

}
