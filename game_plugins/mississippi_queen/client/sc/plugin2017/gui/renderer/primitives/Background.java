package sc.plugin2017.gui.renderer.primitives;

import processing.core.PImage;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class Background extends PrimitiveBase {

  private PImage img;
  private int width;
  private int height;

  public Background(FrameRenderer parent) {
    super(parent);
    img = parent.loadImage(GuiConstants.BACKGROUND_IMAGE);
    resize(parent.width, parent.height);
  }

  @Override
  public void draw(){
    parent.background(GuiConstants.colorBackGround);
    if (width > 0 && height > 0) {
      parent.image(img, 0, 0, width, height);
    }
  }

  public void resize(int width, int height){
    this.width = width;
    this.height = height;
  }

}
