package sc.plugin2014.gui.renderer.display;

import java.awt.*;
import java.util.List;
import sc.plugin2014.entities.Board;
import sc.plugin2014.entities.Field;
import sc.plugin2014.util.Constants;

public class GUIBoard {
    public static void draw(Graphics2D g2, int xStart, int yStart, int width,
            int height, List<GUIStone> toLayStones, Board board,
            Component component, boolean dragging) {
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

        if (dragging) {
            Field fieldFromXY = getFieldUnderMouse(component, offsetX, offsetY,
                    board, xStart, yStart, width, height);

            if (fieldFromXY != null) {
                int fieldToHighlightX = (fieldFromXY.getPosX() * GUIConstants.STONE_WIDTH)
                        + offsetX;
                int fieldToHighlightY = (fieldFromXY.getPosY() * GUIConstants.STONE_HEIGHT)
                        + offsetY;

                g2.setColor(Color.GREEN);
                g2.fillRect(fieldToHighlightX, fieldToHighlightY,
                        GUIConstants.STONE_WIDTH, GUIConstants.STONE_HEIGHT);
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
            int width, int height, Board board, Component component) {
        int offsetX = calculateOffsetX(xStart, width);
        int offsetY = calculateOffsetY(yStart, height);

        g2.setColor(ColorHelper.getTransparentColor(Color.WHITE, 174));
        int maxInXDim = Constants.FIELDS_IN_X_DIM + 1;

        for (int i = 0; i < maxInXDim; i++) {
            int x = offsetX + (i * GUIConstants.STONE_WIDTH);
            g2.drawLine(x, offsetY, x, offsetY
                    + (Constants.FIELDS_IN_Y_DIM * GUIConstants.STONE_HEIGHT));
        }

        int maxInYDim = Constants.FIELDS_IN_Y_DIM + 1;

        for (int i = 0; i < maxInYDim; i++) {
            int y = offsetY + (i * GUIConstants.STONE_HEIGHT);
            g2.drawLine(offsetX, y, offsetX
                    + (Constants.FIELDS_IN_X_DIM * GUIConstants.STONE_WIDTH), y);
        }

        Field fieldFromXY = getFieldUnderMouse(component, offsetX, offsetY,
                board, xStart, yStart, width, height);

        if (fieldFromXY != null) {
            g2.drawString(
                    "Legen auf: (" + fieldFromXY.getPosX() + ", "
                            + fieldFromXY.getPosY() + ")",
                    offsetX
                            + (Constants.FIELDS_IN_X_DIM * GUIConstants.STONE_WIDTH)
                            + 30,
                    (offsetY + (Constants.FIELDS_IN_Y_DIM * GUIConstants.STONE_HEIGHT)) - 5);
        }
    }

    private static Field getFieldUnderMouse(Component component, int offsetX,
            int offsetY, Board board, int xStart, int yStart, int width,
            int height) {
        Point mouseLocation = component.getMousePosition();

        if ((mouseLocation != null)
                && (mouseLocation.x >= offsetX)
                && (mouseLocation.x <= ((Constants.FIELDS_IN_X_DIM * GUIConstants.STONE_WIDTH) + offsetX))) {
            if ((mouseLocation.y >= offsetY)
                    && (mouseLocation.y <= ((Constants.FIELDS_IN_Y_DIM * GUIConstants.STONE_HEIGHT) + offsetY))) {
                return getBelongingFieldFromXY(board, xStart, yStart, width,
                        height, mouseLocation.x, mouseLocation.y);
            }
        }
        return null;
    }

    public static Field getBelongingField(Board board, int xStart, int yStart,
            int width, int height, GUIStone stone) {
        return getBelongingFieldFromXY(board, xStart, yStart, width, height,
                stone.getX(), stone.getY());
    }

    private static Field getBelongingFieldFromXY(Board board, int xStart,
            int yStart, int width, int height, int x, int y) {
        int offsetX = calculateOffsetX(xStart, width);
        int offsetY = calculateOffsetY(yStart, height);

        int xCalculated = (x - offsetX) / GUIConstants.STONE_WIDTH;
        int yCalculated = (y - offsetY) / GUIConstants.STONE_HEIGHT;

        try {
            return board.getField(xCalculated, yCalculated);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}
