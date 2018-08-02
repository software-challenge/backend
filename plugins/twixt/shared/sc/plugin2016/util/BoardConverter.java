package sc.plugin2016.util;
import sc.plugin2016.Field;
import sc.plugin2016.FieldType;
import sc.plugin2016.PlayerColor;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BoardConverter implements Converter {

  @Override
  public boolean canConvert(Class arg0) {
    return arg0.equals(Field[][].class);
  }

  @Override
  public void marshal(Object field, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    Field[][] f = (Field[][]) field;
    for(int x = 0; x < f.length; x++) {
      for(int y = 0; y < f.length; y++) {
        writer.startNode("field");
        if(f[x][y].getOwner() != null) {
          writer.addAttribute("owner", f[x][y].getOwner().toString());
        }
        writer.addAttribute("type", f[x][y].getType().toString());
        writer.addAttribute("x", Integer.toString(x));
        writer.addAttribute("y", Integer.toString(y)); 
        writer.endNode();
      }
    }
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    Field[][] f = new Field[Constants.SIZE][Constants.SIZE];
    try {
      while(reader.hasMoreChildren()) {
        reader.moveDown();
        FieldType fieldType;
        if(reader.getAttribute("type").equals("NORMAL")) {
          fieldType = FieldType.NORMAL;
        } else if(reader.getAttribute("type").equals("BLUE")) {
          fieldType = FieldType.BLUE;
        } else if(reader.getAttribute("type").equals("RED")) {
          fieldType = FieldType.RED;
        } else if(reader.getAttribute("type").equals("SWAMP")) {
          fieldType = FieldType.SWAMP;
        } else {
          throw new IllegalArgumentException("FieldType is not known!");
        }
        int realX = Integer.parseInt(reader.getAttribute("x"));
        int realY = Integer.parseInt(reader.getAttribute("y"));
        f[realX][realY] = new Field(fieldType, realX, realY);
        PlayerColor playerColor;
        if(reader.getAttribute("owner") != null) {
          if(reader.getAttribute("owner").equals("RED")) {
            playerColor = PlayerColor.RED;
            f[realX][realY].setOwner(playerColor);
          } else if(reader.getAttribute("owner").equals("BLUE")) {
            playerColor = PlayerColor.BLUE;
            f[realX][realY].setOwner(playerColor);
          }
        }
        reader.moveUp();
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return f;
  }

}