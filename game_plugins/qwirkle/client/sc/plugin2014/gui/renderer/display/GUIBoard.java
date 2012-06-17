package sc.plugin2014.gui.renderer.display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import sc.plugin2014.entities.Board;
import sc.plugin2014.entities.Field;
import sc.plugin2014.util.Constants;

public class GUIBoard {
    public static void draw(Graphics2D g2, int xStart, int yStart, int width,
            int height, List<GUIStone> toLayStones, Board board) {
        int offsetX = calculateOffsetX(xStart, width);
        int offsetY = calculateOffsetY(yStart, height);

        for (Field field : board.getFields()) {
            if (!field.isFree()) {
                GUIStone stone = new GUIStone(field.getStone());
                stone.setX((field.getPosX() * GUIConstants.STONE_WIDTH)
                        + offsetX);
                stone.setY((field.getPosY() * GUIConstants.STONE_HEIGHT)
                        + offsetY);
                stone.draw(g2);
            }
        }

        for (GUIStone toLayStone : toLayStones) {
            Field field = toLayStone.getField();
            if (field != null) {
                toLayStone.setX((field.getPosX() * GUIConstants.STONE_WIDTH)
                        + offsetX);
                toLayStone.setY((field.getPosY() * GUIConstants.STONE_HEIGHT)
                        + offsetY);
                toLayStone.draw(g2);
            }
        }
    }

    private static int calculateOffsetX(int xStart, int width) {
        int boardWidth = GUIConstants.STONE_WIDTH * Constants.FIELDS_IN_X_DIM;
        int deltaWidth = width - boardWidth;
        int gapWidth = deltaWidth / 2;

        return xStart + gapWidth;
    }

    private static int calculateOffsetY(int yStart, int height) {
        int boardHeight = GUIConstants.STONE_HEIGHT * Constants.FIELDS_IN_Y_DIM;
        int deltaHeight = height - boardHeight;
        int gapHeight = deltaHeight / 2;
        return yStart + gapHeight;
    }

    public static void drawGrid(Graphics2D g2, int xStart, int yStart,
            int width, int height) {
        int offsetX = calculateOffsetX(xStart, width);
        int offsetY = calculateOffsetY(yStart, height);

        g2.setColor(DisplayHelper.getTransparentColor(Color.WHITE, 174));
        for (int i = 1; i < Constants.FIELDS_IN_X_DIM; i++) {
            int x = offsetX + (i * GUIConstants.STONE_WIDTH);
            g2.drawLine(x, offsetY, x, offsetY
                    + (Constants.FIELDS_IN_Y_DIM * GUIConstants.STONE_HEIGHT));
        }

        for (int i = 1; i < Constants.FIELDS_IN_Y_DIM; i++) {
            int y = offsetY + (i * GUIConstants.STONE_HEIGHT);
            g2.drawLine(offsetX, y, offsetX
                    + (Constants.FIELDS_IN_X_DIM * GUIConstants.STONE_WIDTH), y);
        }
    }

    public static Field getBelongingField(Board board, int xStart, int yStart,
            int width, int height, GUIStone stone) {
        int offsetX = calculateOffsetX(xStart, width);
        int offsetY = calculateOffsetY(yStart, height);

        int x = (stone.getX() - offsetX) / GUIConstants.STONE_WIDTH;
        int y = (stone.getY() - offsetY) / GUIConstants.STONE_HEIGHT;

        try {
            return board.getField(x, y);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}
