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
    if (parent.getWidth() > 0 && parent.getHeight() > 0) {
      parent.image(img, 0, 0, parent.getWidth(), parent.getHeight());
    }
  }

}
