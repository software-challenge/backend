package sc.plugin2014.gui.renderer.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import sc.plugin2014.gui.renderer.components.GUIStone;
import sc.plugin2014.gui.renderer.display.EMoveMode;
import sc.plugin2014.gui.renderer.display.GameRenderer;

public class LayMoveAdapter extends MouseAdapter {

    private final GameRenderer parent;

    public LayMoveAdapter(GameRenderer parent) {
        this.parent = parent;

    }

    @Override
    public void mousePressed(MouseEvent e) {

        // requestFocusInWindow();
        if (e.getButton() == MouseEvent.BUTTON1) {
            int x = e.getX();
            int y = e.getY();

            for (GUIStone stone : parent.sensetiveStones) {
                if (checkIfInner(x, y, stone)) {
                    if (parent.moveMode != EMoveMode.EXCHANGE) {
                        parent.removeStone(stone);
                    }
                    parent.updateView();
                    return;
                }
            }

            if (parent.moveMode != EMoveMode.EXCHANGE) {
                for (GUIStone stone : parent.toLayStones) {
                    if (checkIfInner(x, y, stone)) {
                        parent.removeStone(stone);
                        parent.updateView();
                        return;
                    }
                }
            }
        }
    }

    private boolean checkIfInner(int x, int y, GUIStone stone) {
        if (stone.inner(x, y)) {
            parent.selectedStone = stone;
            parent.ox = stone.getX();
            parent.oy = stone.getY();
            parent.dx = x - parent.ox;
            parent.dy = y - parent.oy;

            return true;
        }
        return false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (parent.moveMode != EMoveMode.EXCHANGE) {
            if (parent.selectedStone != null) {
                parent.selectedStone.moveTo(e.getX() - parent.dx, e.getY()
                        - parent.dy);
            }
        }

        parent.updateView();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (parent.selectedStone != null) {
                if (parent.moveMode == EMoveMode.NONE) {
                    if ((Math.abs(e.getX() - parent.ox) < 30)
                            && (Math.abs(e.getY() - parent.oy) < 30)) {
                        parent.moveMode = EMoveMode.EXCHANGE;
                        parent.actionButton
                                .setLabel(GameRenderer.EXCHANGE_STONE_LABEL);
                        parent.addStone(parent.selectedStone);
                    }
                    else {
                        parent.moveMode = EMoveMode.LAY;
                        parent.actionButton
                                .setLabel(GameRenderer.LAY_STONE_LABEL);
                    }
                }

                if (parent.moveMode == EMoveMode.LAY) {
                    parent.selectedStone.moveTo(e.getX(), e.getY());

                    parent.layStone(parent.selectedStone);

                    parent.selectedStone = null;
                }

                if (parent.moveMode == EMoveMode.EXCHANGE) {
                    parent.toogleExchangeStone(parent.selectedStone);
                    parent.selectedStone = null;
                }

                parent.updateView();
            }
        }
    }
}
