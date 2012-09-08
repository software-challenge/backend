package sc.plugin2014.gui.renderer.display;

import static sc.plugin2014.gui.renderer.display.GUIConstants.*;
import java.awt.*;
import sc.plugin2014.entities.Field;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.gui.renderer.RendererUtil;

public class GUIStone {

    private boolean     highlighted;
    private int         x, y;
    private final Stone stone;
    private Field       field;
    private Image       image;

    public GUIStone(Stone stone) {
        this.stone = stone;
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

    public void draw(Graphics2D g2) {
        if (image == null) {
            image = RendererUtil.getImage("resource/stones/"
                    + stone.getShape().toString().toLowerCase() + "_"
                    + stone.getColor().toString().toLowerCase() + ".png");
        }
        if (isHighlighted()) {
            g2.drawImage(image, getX(), getY(), GUIConstants.STONE_WIDTH,
                    GUIConstants.STONE_HEIGHT, fmPanel);
            g2.setColor(ColorHelper.getTransparentColor(Color.WHITE, 160));
            g2.drawRect(getX(), getY(), GUIConstants.STONE_WIDTH,
                    GUIConstants.STONE_HEIGHT);
            g2.drawRect(getX() + 1, getY() + 1, GUIConstants.STONE_WIDTH - 2,
                    GUIConstants.STONE_HEIGHT - 2);
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
}
