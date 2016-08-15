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

  public GuiTile(FrameRenderer parent, int i) {
    super(parent);
    visible = false;
    index = i; 
    // TODO make fields
    }

  @Override
  public void draw() {
    if(visible) {
      for (HexField field : fields) {
        field.draw();
      }
    }

  }

  public void resize(int offsetX, int offsetY, int width) {
    if(visible) {
      for (HexField field : fields) {
        field.resize(offsetX, offsetY, width);
      }
    }
    
  }

  public void update(Tile tile2) {
    // TODO Auto-generated method stub
    
  }

}
