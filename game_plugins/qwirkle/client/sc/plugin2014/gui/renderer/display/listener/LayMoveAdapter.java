package sc.plugin2014.gui.renderer.display.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.plugin2014.gui.renderer.display.FrameRenderer;
import sc.plugin2014.gui.renderer.display.GUIStone;

public class LayMoveAdapter extends MouseAdapter {

    private static final Logger logger = LoggerFactory
                                               .getLogger(LayMoveAdapter.class);

    private final FrameRenderer parent;

    public LayMoveAdapter(FrameRenderer parent) {
        this.parent = parent;

    }

    @Override
    public void mousePressed(MouseEvent e) {

        // requestFocusInWindow();
        if (e.getButton() == MouseEvent.BUTTON1) {
            int x = e.getX();
            int y = e.getY();

            for (GUIStone stone : parent.sensetiveStones) {
                if (stone.inner(x, y)) {
                    parent.selectedStone = stone;
                    parent.removeStone(stone);
                    parent.ox = stone.getX();
                    parent.oy = stone.getY();
                    parent.dx = x - parent.ox;
                    parent.dy = y - parent.oy;

                    parent.updateView();
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (parent.selectedStone != null) {
            parent.selectedStone.moveTo(e.getX() - parent.dx, e.getY()
                    - parent.dy);
            parent.updateView();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (parent.selectedStone != null) {
                parent.selectedStone.moveTo(e.getX() - parent.dx, e.getY()
                        - parent.dy);

                parent.layStone(parent.selectedStone);

                parent.selectedStone = null;
            }
            parent.updateView();

        }
    }
}
