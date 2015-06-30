package sc.plugin2016.util;
import sc.plugin2016.Board;
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
    System.out.println("\n\n\n\n\n\n\n******************************");
    try {
      for(int x = 0; x < Constants.SIZE; x++) {
        for(int y = 0; y < Constants.SIZE; y++) {
          FieldType fieldType;
          System.out.println("1");
          System.out.println(FieldType.NORMAL);
          if(reader.getAttribute("type").equals("NORMAL")) { // TODO hier entsteht noch ne nullpointerexception. (erste der 3 Ausgaben!)
            fieldType = FieldType.NORMAL;
            System.out.println("normal.");
          } else if(reader.getAttribute("type").equals("BLUE")) {
            fieldType = FieldType.BLUE;
            System.out.println("blue.");
          } else if(reader.getAttribute("type").equals("RED")) {
            fieldType = FieldType.RED;
            System.out.println("red.");
          } else if(reader.getAttribute("type").equals("SWAMP")) {
            fieldType = FieldType.SWAMP;
            System.out.println("swamp.");
          } else {
            System.out.println("error.");
            throw new IllegalArgumentException("FieldType is not known!");
          }
          System.out.println("2");
          f[x][y] = new Field(fieldType, x, y); // TODO hier auch x und y deserialisieren
          PlayerColor playerColor;
          if(reader.getAttribute("owner").equals("RED")) {
            playerColor = PlayerColor.RED;
            f[x][y].setOwner(playerColor);
          } else if(reader.getAttribute("owner").equals("BLUE")) {
            playerColor = PlayerColor.BLUE;
            f[x][y].setOwner(playerColor);
          }
          System.out.println("3");
          reader.moveDown();
          System.out.println("4");
          System.out.println(f[x][y]);
          System.out.println(f[x][y].getOwner());
          System.out.println(f[x][y].getType());
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return f;
  }

}