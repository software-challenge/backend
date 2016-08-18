package sc.plugin2017.gui.renderer.primitives;

import java.util.ArrayList;

import sc.plugin2017.Field;
import sc.plugin2017.Tile;
import sc.plugin2017.gui.renderer.FrameRenderer;

public class GuiTile extends PrimitiveBase {

  public ArrayList<HexField> fields;
  
  public boolean visible;
  
  public float startX;
  public float startY;
  
  public int direction;
  
  public int index;
  
  public Tile tile;
  
  public GuiTile(FrameRenderer parent) {
    super(parent);
    fields = new ArrayList<HexField>();
    // TODO Auto-generated constructor stub
  }

  public GuiTile(FrameRenderer parent, int index) {
    super(parent);
    visible = false;
    fields = new ArrayList<HexField>();
    this.index = index; 
    // create GuiFields
    for(int i = 0; i < 20; i++) {
      HexField field = new HexField(parent);
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

  public void resize(float startX, float startY, int offsetX, int offsetY, float width) {
    
    for (HexField field : fields) {
      field.resize(startX, startY, offsetX, offsetY, width);
    }
  }

  public void update(Tile tile) {
    this.tile = tile;
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
        if(field.fieldX == x && field.fieldY == y) {
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
}
