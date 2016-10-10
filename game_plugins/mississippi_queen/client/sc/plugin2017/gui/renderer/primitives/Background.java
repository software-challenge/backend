package sc.plugin2017.gui.renderer.primitives;

import processing.core.PImage;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class Background extends PrimitiveBase {

  private PImage img;

  public Background(FrameRenderer parent) {
    super(parent);
    img = parent.loadImage(GuiConstants.BACKGROUND_IMAGE);
  }

  @Override
  public void draw(){
    parent.background(GuiConstants.colorBackGround);
    for (int y = 0; y < parent.getHeight(); y += img.height) {
      for (int x = 0; x < parent.getWidth(); x += img.width) {
        parent.image(img, x, y);
      }
    }
  }

}
