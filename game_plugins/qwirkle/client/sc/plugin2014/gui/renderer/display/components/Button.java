package sc.plugin2014.gui.renderer.display.components;

import java.awt.*;
import sc.plugin2014.gui.renderer.display.GUIConstants;

public class Button extends Component {
    private static final long serialVersionUID = 3600449047261620589L;
    private final String      label;

    public Button(String label) {
        this.label = label;
    }

    @Override
    public void paint(Graphics g) {
        if (isEnabled()) {
            g.setColor(new Color(210, 210, 210));
        }
        else {
            g.setColor(new Color(120, 120, 120));
        }
        g.fillRect(getX(), getY(), getWidth(), getHeight());

        if (isEnabled()) {
            g.setColor(Color.BLACK);
        }
        else {
            g.setColor(Color.GRAY);
        }
        g.setFont(GUIConstants.h4);
        g.drawString(label, getX() + 25, getY() + 20);
    }
}
