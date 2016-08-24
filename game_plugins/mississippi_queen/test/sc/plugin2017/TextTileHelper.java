package sc.plugin2017;

import java.util.ArrayList;
import java.util.List;

public class TextTileHelper {

  public static boolean isEven(int i) {
    return ((Math.abs(i) % 2) == 0);
  }

  /**
   * Converts a Tile to a textual representation.
   * @param tile The tile to be converted.
   * @return String representation of given Tile.
   */
  public static String printTile(Tile tile) {
    // get boundaries (top left, bottom right)

    Integer maxX = Integer.MIN_VALUE;
    Integer minX = Integer.MAX_VALUE;
    Integer maxY = Integer.MIN_VALUE;
    Integer minY = Integer.MAX_VALUE;
    for (Field field : tile.fields) {
      maxX = Math.max(maxX, field.getX());
      minX = Math.min(minX, field.getX());
      maxY = Math.max(maxY, field.getY());
      minY = Math.min(minY, field.getY());
    }
    // iterate over field in boundaries, printing fields if existent
    String result = "";
    for (int y = minY; y <= maxY; y++) {
      if (isEven(y)) {
        // add one space for even rows in y direction (even-r horizontal layout)
        result += ".";
      }
      for (int x = minX; x <= maxX; x++) {
        Field f = tile.getField(x, y);
        if (f == null) {
          // coordinates not occupied by a field
          result += ".";
        } else {
          result += (f.getType().toString().substring(0, 1));
        }
        // one space between fields to allow offset
        result += ".";
      }
      result += "\n";
    }
    return result;
  }

  /**
   * Converts a String to a Tile.
   * @param tileString The Tile definition. "." for unoccupied fields and spaces
   * between fields, uppercase character or number for field type (see below).
   * You may use "r" and "b" to mark players positions, these are translated
   * into water fields, because a tile does not hold player positions. You have
   * to make sure that real player positions match your markers.
   * @param startX X-coordinate of upper left field in Tile definition (also considering non-occupied fields)
   * @param startY Y-coordinate
   * @return Tile for given definition.
   */
  public static Tile parseTile(String tileString, int startX, int startY) {

    List<Field> fields = new ArrayList<Field>();
    int x = startX;
    int y = startY;
    boolean lineStart = true;
    boolean lastWasField = false;
    // NOTE that this will likely not work for UTF8 characters
    for (int i = 0; i < tileString.length(); i++) {
      char c = tileString.charAt(i);
      switch (c) {
      case '.':
        if (lineStart && isEven(y)) {
          // offset
          lineStart = false;
          lastWasField = false;
        } else if (lastWasField) {
          // fieldseparator
          lastWasField = false;
        } else {
          // unoccupied field
          lastWasField = true;
          x += 1;
        }
        break;
      case '\n':
        lineStart = true;
        lastWasField = false;
        x = startX;
        y += 1;
        break;
      default:
        FieldType type = null;
        switch (c) {
        case 'W':
        case 'r':
        case 'b':
          // 'r' and 'b' may be used to mark players positions
          type = FieldType.WATER;
          break;
        case 'B': type = FieldType.BLOCKED; break;
        case 'S': type = FieldType.SANDBANK; break;
        case 'L': type = FieldType.LOG; break;
        case 'G': type = FieldType.GOAL; break;
        case '0': type = FieldType.PASSENGER0; break;
        case '1': type = FieldType.PASSENGER1; break;
        case '2': type = FieldType.PASSENGER2; break;
        case '3': type = FieldType.PASSENGER3; break;
        case '4': type = FieldType.PASSENGER4; break;
        case '5': type = FieldType.PASSENGER5; break;
        default: throw new IllegalArgumentException(String.format("unexpected field type '%c' at (%d, %d)", c, x, y));
        }
        fields.add(new Field(type, x, y));
        lineStart = false;
        lastWasField = true;
        x += 1;
        break;
      }
    }

    Tile tile = new Tile(fields);
    tile.setVisibility(true);
    return tile;
  }

}
