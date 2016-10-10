package sc.plugin2017.gui.renderer.primitives;

import java.util.ArrayList;

import sc.plugin2017.Tile;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GuiTile extends PrimitiveBase {

  private ArrayList<HexField> fields;

  private boolean visible;

  public GuiTile(FrameRenderer parent) {
    super(parent);
    fields = new ArrayList<HexField>();
  }

  public GuiTile(FrameRenderer parent, int index, float width, float startX, float startY, int offsetX, int offsetY) {
    super(parent);
    visible = false;
    fields = new ArrayList<HexField>();
    // create GuiFields
    for(int i = 0; i < 20; i++) {
      HexField field = new HexField(parent, width, startX, startY, offsetX, offsetY, index);
      fields.add(field);
    }
  }

  @Override
  public void draw() {
    if(visible) {
      for (HexField field : fields) {
        field.draw();
      }
    }
  }

  // draws only highlight for field because highlights need to be drawn above (e.g. after) all other fields
  public void drawHighlights() {
    if(visible) {
      for (HexField field : fields) {
        field.drawHighlight();
      }
    }
  }

  public void resize(float startX, float startY, int offsetX, int offsetY, float width) {
    for (HexField field : fields) {
      field.resize(startX, startY, offsetX, offsetY, width);
    }
  }

  public void update(Tile tile) {
    if(tile == null) {
      visible = false;
      return;
    }
    visible = true;
    int i = 0;
    if(!fields.isEmpty() && !tile.fields.isEmpty()) {
      for (HexField field : fields) {
        field.update(tile.fields.get(i));
        i++;
      }
    }
  }

  @Override
  public void kill() {
    for (HexField field : fields) {
      if(field != null) {
        field.kill();
      }
    }
  }

  public HexField getHexField(int x, int y) {
    if(visible) {
      for (HexField field : fields) {
        if(field.getFieldX() == x && field.getFieldY() == y) {
          return field;
        }
      }
    }
    return null;
  }

  public HexField getFieldCoordinates(int x, int y) {
    for (HexField field : fields) {
      HexField coordinates = field.getFieldCoordinates(x,y);
      if(coordinates != null) {
        return coordinates;
      }
    }
    return null;
  }

  public void setVisible(boolean visibility) {
    visible = visibility;
  }
}
