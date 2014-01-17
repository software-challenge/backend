package sc.plugin2014.gui.renderer.components;

import static sc.plugin2014.gui.renderer.configuration.GUIConstants.*;
import java.awt.*;
import sc.plugin2014.entities.*;
import sc.plugin2014.gui.renderer.configuration.GUIConstants;
import sc.plugin2014.gui.renderer.util.ColorHelper;
import sc.plugin2014.gui.renderer.util.RendererUtil;

public class GUIStone {

    private boolean     highlighted;
    private int         x, y;
    private final Stone stone;
    private Field       field;
    private Image       image;
    private final int   originalPositionOnHand;

    public GUIStone(Stone stone, int originalPositionOnHand) {
        this.stone = stone;
        this.originalPositionOnHand = originalPositionOnHand;
        setHighlighted(false);
    }

    public void moveTo(int x, int y) {
        rebuild(x, y);
    }

    public void rebuild(int x, int y) {
        setX(x);
        setY(y);
    }

    public boolean inner(int xClickPos, int yClickPos) {

        if (!(((xClickPos - getX()) >= 0) && ((xClickPos - getX()) <= GUIConstants.STONE_WIDTH))) {
            return false;
        }

        if (!(((yClickPos - getY()) >= 0) && ((yClickPos - getY()) <= GUIConstants.STONE_HEIGHT))) {
            return false;
        }

        return true;
    }

    public Stone getStone() {
        return stone;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void draw(Graphics2D g2, PlayerColor currentPlayerColor) {
        if (image == null) {
        	String shape = stone.getShape().toString().toLowerCase();
        	String color = stone.getColor().toString().toLowerCase();
            image = RendererUtil.getImage("resource/stones/"
                    + shape + "_"
                    + color + ".png");
        }
        if (isHighlighted()) {
            g2.drawImage(image, getX(), getY(), GUIConstants.STONE_WIDTH,
                    GUIConstants.STONE_HEIGHT, fmPanel);
            g2.setColor(ColorHelper.getActiveColor());
            g2.drawRect(getX(), getY(), GUIConstants.STONE_WIDTH,
                    GUIConstants.STONE_HEIGHT);
            g2.drawRect(getX() + 1, getY() + 1, GUIConstants.STONE_WIDTH - 2,
                    GUIConstants.STONE_HEIGHT - 2);
            g2.setColor(Color.WHITE);
        }
        else {
            g2.drawImage(image, getX(), getY(), GUIConstants.STONE_WIDTH,
                    GUIConstants.STONE_HEIGHT, fmPanel);
        }
    }

    public Field getField() {
        return field;
    }

    public void setField(Field belongingField) {
        field = belongingField;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public int getOriginalPositionOnHand() {
        return originalPositionOnHand;
    }
}
