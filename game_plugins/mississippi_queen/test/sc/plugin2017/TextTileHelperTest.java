package sc.plugin2017;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TextTileHelperTest {

  @Test
  public void testEven() {
    assertTrue(TextTileHelper.isEven(-2));
    assertFalse(TextTileHelper.isEven(-1));
    assertTrue(TextTileHelper.isEven(0));
    assertFalse(TextTileHelper.isEven(1));
    assertTrue(TextTileHelper.isEven(2));
  }

  @Test
  public void testPrintTile() {
    Board board = new Board();
    // first row has index -2, is therefore even and needs to be offset.
    assertEquals(
        ".W.W.W.W...\n" +
        "..W.W.W.W.\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W.\n" +
        ".W.W.W.W...\n",
        TextTileHelper.printTile(board.getTiles().get(0)));
  }

  @Test
  public void testParseTile() {
    Board board = new Board();
    // first row has index -2, is therefore even and needs to be offset.
    assertEquals(board.getTiles().get(0),
        TextTileHelper.parseTile(
        ".W.W.W.W...\n" +
        "..W.W.W.W.\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W.\n" +
        ".W.W.W.W...\n", -2, -2
        ));
    {
      Tile tile = TextTileHelper.parseTile(
          ".W.W.W.W...\n" +
          "..b.B.W.W.\n" +
          "...2.W.L.W.\n" +
          "..W.S.W.W.\n" +
          ".W.W.W.W...\n", -2, -2
          );
      assertEquals(FieldType.WATER, tile.getField(-1, -1).getType());
      assertEquals(FieldType.PASSENGER2, tile.getField(-1, 0).getType());
      assertTrue(tile.isVisible());
    }
    {
      Tile tile = TextTileHelper.parseTile(
          ".W.W.W.W...\n" +
          "..W.b.W.W.\n" +
          "...r.W.W.W.\n" +
          "..W.W.W.W.\n" +
          ".W.W.W.W...\n", -2, -2
          );
      assertEquals(FieldType.WATER, tile.getField(0, -1).getType());
      assertEquals(FieldType.WATER, tile.getField(-1, 0).getType());
      assertTrue(tile.isVisible());
    }
  }

}
