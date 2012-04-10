package sc.plugin2014.gui.renderer.display;

import static sc.plugin2014.util.Constants.*;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.PlayerColor;

public class DrawAdditional {

    public static void createPlayerStones() {

        // spieler rot
        Player player = gameState.getRedPlayer();
        redSegments.clear();
        for (int i = MAX_SEGMENT_SIZE; i > 0; i--) {
            int usable = player.getSegment(i).getUsable();
            for (int j = 0; j < usable; j++) {
                TowerData tower = new TowerData(i);
                tower.owner = PlayerColor.RED;
                redSegments.add(tower);
            }
        }

        // spieler blau
        player = gameState.getBluePlayer();
        synchronized (blueSegments) {
            blueSegments.clear();
            for (int i = MAX_SEGMENT_SIZE; i > 0; i--) {
                int usable = player.getSegment(i).getUsable();
                for (int j = 0; j < usable; j++) {
                    TowerData tower = new TowerData(i);
                    tower.owner = PlayerColor.BLUE;
                    blueSegments.add(tower);
                }
            }
        }
    }

    private void recreatePlayerSegments() {

        // spieler rot
        int x = BORDER_SIZE + STUFF_GAP
                + (STONES_PER_PLAYER * (CARD_WIDTH + STUFF_GAP));
        int y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP;

        synchronized (redSegments) {
            for (TowerData tower : redSegments) {
                if (tower != selectedSegment) {
                    tower.moveTo(x, y);
                }
                x += TOWER_TOTAL_WIDTH + STUFF_GAP;
            }
        }

        // spieler blau
        x = getWidth() - BORDER_SIZE - STUFF_GAP
                - (STONES_PER_PLAYER * (CARD_WIDTH + STUFF_GAP))
                - TOWER_TOTAL_WIDTH;
        y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP;

        synchronized (blueSegments) {
            for (TowerData tower : blueSegments) {
                if (tower != selectedSegment) {
                    tower.moveTo(x, y);
                }
                x -= TOWER_TOTAL_WIDTH + STUFF_GAP;
            }
        }

    }
}
