package sc.plugin2014.gui.renderer.display.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import sc.plugin2014.moves.LayMove;

public class LayMoveAdapter extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {

        requestFocusInWindow();
        if (e.getButton() == MouseEvent.BUTTON1) {
            int x = e.getX();
            int y = e.getY();

            for (TowerData tower : sensetiveSegments) {
                if (inner(x, y, tower.xs, tower.ys)) {
                    selectedSegment = tower;
                    ox = tower.x;
                    oy = tower.y;
                    dx = x - ox;
                    dy = y - oy;

                    for (LayMove move : gameState.getPossibleMoves()) {
                        if (move.size == tower.size) {
                            TowerData data = cityTowers[move.city][move.slot];
                            sensetiveTowers.add(data);
                            data.highlited = true;
                        }
                    }

                    updateBuffer = true;
                    repaint();
                    break;
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (selectedSegment != null) {
            selectedSegment.moveTo(e.getX() - dx, e.getY() - dy);

            TowerData oldSelectedTower = selectedTower;
            selectedTower = null;
            for (TowerData tower : sensetiveTowers) {
                if (inner(selectedSegment.xs[1], selectedSegment.ys[1],
                        tower.xs, tower.ys)) {
                    if (tower.slot > 0) {
                        TowerData clipper = cityTowers[tower.city][tower.slot - 1];
                        if (!inner(selectedSegment.xs[1],
                                selectedSegment.ys[1], clipper.xs, clipper.ys)) {
                            selectedTower = tower;
                            break;
                        }
                    }
                    else {
                        selectedTower = tower;
                        break;
                    }
                }
            }

            if (selectedTower != oldSelectedTower) {
                updateBuffer = true;
            }
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (selectedSegment != null) {
                sensetiveTowers.clear();
                for (int i = 0; i < CITIES; i++) {
                    for (int j = 0; j < SLOTS; j++) {
                        cityTowers[i][j].highlited = false;
                    }
                }
                if (selectedTower != null) {
                    TowerData data = selectedTower;
                    droppedSegment = selectedSegment;
                    selectedTower = null;
                    selectedSegment = null;
                    sendMove(new LayMove(data.city, data.slot,
                            droppedSegment.size));
                }
                else {
                    selectedSegment.moveTo(ox, oy);
                    selectedSegment = null;
                }
                updateBuffer = true;
                repaint();
            }
        }
    }
}
