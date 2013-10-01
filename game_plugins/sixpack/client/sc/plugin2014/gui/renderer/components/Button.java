package sc.plugin2014.gui.renderer.components;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import sc.plugin2014.gui.renderer.configuration.GUIConstants;

public class Button extends Component {
    private static final long serialVersionUID = 3600449047261620589L;
    private String            label;

    public Button(String label) {
        setLabel(label);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(210, 210, 210));

        g.fillRect(getX(), getY(), getWidth(), getHeight());

        if (isEnabled()) {
            g.setColor(Color.BLACK);
        }
        else {
            g.setColor(new Color(160, 160, 160));
        }
        g.setFont(GUIConstants.h4);
        Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(
                getLabel(), g);
        int xStart = (int) ((getWidth() - stringBounds.getWidth()) / 2);
        g.drawString(getLabel(), getX() + xStart, getY() + 20);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
