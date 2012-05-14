package sc.plugin2014.gui.renderer.display;

import static sc.plugin2014.util.Constants.*;
import java.util.List;
import sc.plugin2014.entities.Stone;

public class DrawAdditional {

    public static void recreatePlayerSegments(int width, int height,
            List<Stone> redStones, List<Stone> blueStones) {

        // spieler rot
        int x = GUIConstants.BORDER_SIZE
                + GUIConstants.STUFF_GAP
                + (STONES_PER_PLAYER * (GUIConstants.STONES_ON_HAND_WIDTH + GUIConstants.STUFF_GAP));
        int y = height - GUIConstants.BORDER_SIZE
                - GUIConstants.PROGRESS_BAR_HEIGTH - GUIConstants.STUFF_GAP;

        synchronized (redStones) {
            ;
        }

        // spieler blau
        x = width
                - GUIConstants.BORDER_SIZE
                - GUIConstants.STUFF_GAP
                - (STONES_PER_PLAYER * (GUIConstants.STONES_ON_HAND_WIDTH + GUIConstants.STUFF_GAP))
                - GUIConstants.TOWER_TOTAL_WIDTH;
        y = height - GUIConstants.BORDER_SIZE
                - GUIConstants.PROGRESS_BAR_HEIGTH - GUIConstants.STUFF_GAP;

        synchronized (blueStones) {
            ;
        }

    }
}
