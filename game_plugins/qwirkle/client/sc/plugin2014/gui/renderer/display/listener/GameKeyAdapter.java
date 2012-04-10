package sc.plugin2014.gui.renderer.display.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import sc.plugin2014.gui.renderer.display.FrameRenderer;
import sc.plugin2014.gui.renderer.game_configuration.RenderConfigurationDialog;

public class GameKeyAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {

        System.out.println("--------------1");
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            System.out.println("--------------2");

            new RenderConfigurationDialog(FrameRenderer.this);
            updateBuffer = true;
            repaint();

        }
    }
}
