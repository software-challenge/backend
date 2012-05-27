package sc.plugin2014.gui.renderer.display;

import static sc.plugin2014.gui.renderer.display.GUIConstants.*;
import java.awt.Graphics2D;
import java.awt.Image;
import sc.plugin2014.entities.Field;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.gui.renderer.RendererUtil;

public class GUIStone {

    private final boolean highlighted;
    private int           x, y;
    private final Stone   stone;
    private Field         field;

    public GUIStone(Stone stone) {
        this.stone = stone;
        highlighted = false;
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
        Image image = RendererUtil.getImage("resource/stones/"
                + stone.getShape().toString().toLowerCase() + "_"
                + stone.getColor().toString().toLowerCase() + ".png");
        if (highlighted) {
            g2.drawImage(image, getX(), getY(), GUIConstants.STONE_WIDTH,
                    GUIConstants.STONE_HEIGHT, fmPanel); // TODO make
                                                         // highlighted
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
}
