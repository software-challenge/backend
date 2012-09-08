package sc.plugin2014.gui.renderer.display.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import sc.plugin2014.gui.renderer.display.FrameRenderer;
import sc.plugin2014.gui.renderer.game_configuration.RenderConfigurationDialog;

public class GameKeyAdapter extends KeyAdapter {

    private final FrameRenderer parent;

    public GameKeyAdapter(FrameRenderer parent) {
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
