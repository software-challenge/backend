package sc.plugin2014.gui.renderer.display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import sc.plugin2014.entities.Board;
import sc.plugin2014.entities.Field;

public class GUIBoard {
    public static void draw(Graphics2D g2, int xStart, int yStart,
            List<GUIStone> toLayStones, Board board) {
        for (Field field : board.getFields()) {
            if (!field.isFree()) {
                GUIStone stone = new GUIStone(field.getStone());
                stone.setX((field.getPosX() * GUIConstants.STONE_WIDTH)
                        + xStart);
                stone.setY((field.getPosY() * GUIConstants.STONE_HEIGHT)
                        + yStart);
                stone.draw(g2);
            }
        }

        for (GUIStone toLayStone : toLayStones) {
            Field field = toLayStone.getField();
            if (field != null) {
                toLayStone.setX((field.getPosX() * GUIConstants.STONE_WIDTH)
                        + xStart);
                toLayStone.setY((field.getPosY() * GUIConstants.STONE_HEIGHT)
                        + yStart);
                toLayStone.draw(g2);
            }
        }
    }

    public static void drawGrid(Graphics2D g2, int xStart, int yStart,
            int width, int height) {
        g2.setColor(DisplayHelper.getTransparentColor(Color.WHITE, 174));
        int x = xStart + GUIConstants.STONE_WIDTH;
        while (x < width) {
            g2.drawLine(x, yStart, x, yStart + height);
            x += GUIConstants.STONE_WIDTH;
        }

        int y = yStart + GUIConstants.STONE_HEIGHT;
        while (y < height) {
            g2.drawLine(xStart, y, xStart + width, y);
            y += GUIConstants.STONE_HEIGHT;
        }
    }

    public static Field getBelongingField(Board board, int xStart, int yStart,
            GUIStone stone) {
        int x = (stone.getX() - xStart) / GUIConstants.STONE_WIDTH;
        int y = (stone.getY() - yStart) / GUIConstants.STONE_HEIGHT;

        try {
            return board.getField(x, y);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}
