package sc.plugin2017.gui.renderer.primitives;

import sc.plugin2017.gui.renderer.FrameRenderer;

public class CircularGuiButton extends GuiButton {

  public CircularGuiButton(FrameRenderer parent, String message) {
    super(parent, message);
  }

  @Override
  public void resize(int newWidth) {
    width = newWidth;
    height = newWidth;
  }
}
