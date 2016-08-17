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
      System.out.println("\n\n Found visible tile \n\n");
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
    System.out.println("\n\n\nUpdated tile " + index);
    this.tile = tile;
    visible = true;
    int i = 0;
    for (HexField field : fields) {
      field.update(tile.fields.get(i));
      i++;
    }
  }

}
