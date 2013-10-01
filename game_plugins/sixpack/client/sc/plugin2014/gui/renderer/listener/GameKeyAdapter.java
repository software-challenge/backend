package sc.plugin2014.gui.renderer.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import sc.plugin2014.gui.renderer.components.RenderConfigurationDialog;
import sc.plugin2014.gui.renderer.display.GameRenderer;

public class GameKeyAdapter extends KeyAdapter {

    private final GameRenderer parent;

    public GameKeyAdapter(GameRenderer parent) {
        this.parent = parent;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            RenderConfigurationDialog renderConfigurationDialog = new RenderConfigurationDialog(
                    parent);
            renderConfigurationDialog.repaint();
            parent.updateView();
        }
    }
}
