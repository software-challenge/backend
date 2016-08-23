package sc.plugin2017;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sc.plugin2017.util.InvalidMoveException;

public class MoveTest {

  private GameState state;
  private Board board;
  private Player red;
  private Player blue;

  @Before
  public void setupGameState() {

    state = new GameState();
    red = new Player(PlayerColor.RED);
    blue = new Player(PlayerColor.BLUE);

    state.addPlayer(red);
    state.addPlayer(blue);

    board = state.getBoard();
  }

  @Test(expected = InvalidMoveException.class)
  public void moveOntoBlockedField() throws InvalidMoveException {

    red.getField(board).getFieldInDirection(red.getDirection(), board).setType(FieldType.BLOCKED);

    Move move = new Move();
    Action action = new Step(1);
    move.actions.add(action);

    move.perform(state, red);
  }

  public boolean isEven(int i) {
    return ((Math.abs(i) % 2) == 0);
  }

  public String printTile(Tile tile) {
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
    assertEquals(-2, minX.intValue());
    assertEquals(-2, minY.intValue());
    assertEquals(2, maxX.intValue());
    assertEquals(2, maxY.intValue());
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
  
  public Tile parseTile(String tileString, int startX, int startY) {
    
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
      case 'W': 
        fields.add(new Field(FieldType.WATER, x, y));
        lineStart = false;
        lastWasField = true;
        x += 1;
        break;
      }
    }
    
    return new Tile(fields);
  }

  @Test
  public void testEven() {
    assertTrue(isEven(-2));
    assertFalse(isEven(-1));
    assertTrue(isEven(0));
    assertFalse(isEven(1));
    assertTrue(isEven(2));
  }

  @Test
  public void testPrintTile() {
    // first row has index -2, is therefore even and needs to be offset.
    assertEquals(
        ".W.W.W.W...\n" +
        "..W.W.W.W.\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W.\n" +
        ".W.W.W.W...\n",
        printTile(board.getTiles().get(0)));
  }

  @Test
  public void testParseTile() {
    // first row has index -2, is therefore even and needs to be offset.
    assertEquals(board.getTiles().get(0),
        parseTile(
        ".W.W.W.W...\n" +
        "..W.W.W.W.\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W.\n" +
        ".W.W.W.W...\n", -2, -2
        ));
  }
  
  @Test
  public void testCompareFields() {
    Field fOne = new Field(FieldType.WATER, -2, -2);
    Field fTwo = new Field(FieldType.BLOCKED, 0, 0);
    assertEquals(-1, fOne.compareTo(fTwo));
    assertTrue(false);
    // TODO finish comparable, sort fields of tiles to compare tiles in testParseTile
  }
}
