package sc.plugin2014.gui.renderer.display.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.plugin2014.gui.renderer.display.FrameRenderer;
import sc.plugin2014.gui.renderer.game_configuration.RenderConfigurationDialog;

public class GameKeyAdapter extends KeyAdapter {

    private static final Logger logger = LoggerFactory
                                               .getLogger(GameKeyAdapter.class);

    private final FrameRenderer parent;

    public GameKeyAdapter(FrameRenderer parent) {
        this.parent = parent;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        logger.debug("--------------1");
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            logger.debug("--------------2");

            RenderConfigurationDialog renderConfigurationDialog = new RenderConfigurationDialog(
                    parent);
            renderConfigurationDialog.repaint();
            parent.updateView();
        }
    }
}
