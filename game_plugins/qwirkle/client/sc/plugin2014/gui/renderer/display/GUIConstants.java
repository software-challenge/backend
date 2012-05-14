package sc.plugin2014.gui.renderer.display;

import java.awt.*;
import javax.swing.JPanel;

public class GUIConstants {
    public final static int         BORDER_SIZE           = 6;
    public static final int         PROGRESS_ICON_SIZE    = 60;
    public static final int         PROGRESS_BAR_HEIGTH   = 36;

    public static final int         SIDE_BAR_WIDTH        = 200;

    public static final int         TOWER_LEFT_WIDTH      = 25;
    public static final int         TOWER_LEFT_HEIGTH     = 10;
    public static final int         TOWER_RIGHT_WIDTH     = 15;
    public static final int         TOWER_RIGHT_HEIGTH    = 15;
    public static final int         TOWER_TOTAL_WIDTH     = TOWER_LEFT_WIDTH
                                                                  + TOWER_RIGHT_WIDTH;
    public static final int         TOWER_STORIE_HEIGTH   = 14;

    public static final int         STUFF_GAP             = 8;
    public static final int         GAP_SIZE              = 10;
    public static final int         GAP_TOWER_TOP         = TOWER_RIGHT_HEIGTH;
    public static final int         GAP_TOWER_BOTTOM      = TOWER_LEFT_HEIGTH;
    public static final int         DIST_TOWER_SELECT     = 15;

    public static final int         DIAGONAL_GAP          = 10;
    public static final int         CITY_GAP              = 42;

    public static final int         STONES_ON_HAND_WIDTH  = 60;
    public static final int         STONES_ON_HAND_HEIGHT = 60;

    // schrift
    public static final Font        h0                    = new Font(
                                                                  "Helvetica",
                                                                  Font.BOLD, 73);
    public static final Font        h1                    = new Font(
                                                                  "Helvetica",
                                                                  Font.BOLD, 42);
    public static final Font        h2                    = new Font(
                                                                  "Helvetica",
                                                                  Font.BOLD, 27);
    public static final Font        h3                    = new Font(
                                                                  "Helvetica",
                                                                  Font.BOLD, 23);
    public static final Font        h4                    = new Font(
                                                                  "Helvetica",
                                                                  Font.BOLD, 14);
    public static final Font        h5                    = new Font(
                                                                  "Helvetica",
                                                                  Font.PLAIN,
                                                                  13);

    public static final JPanel      fmPanel               = new JPanel();
    public static final FontMetrics fmH0                  = fmPanel
                                                                  .getFontMetrics(h0);
    // public static final FontMetrics fmH1 = fmPanel.getFontMetrics(h1);
    public static final FontMetrics fmH2                  = fmPanel
                                                                  .getFontMetrics(h2);
    public static final FontMetrics fmH3                  = fmPanel
                                                                  .getFontMetrics(h3);
    public static final FontMetrics fmH4                  = fmPanel
                                                                  .getFontMetrics(h4);
    public static final FontMetrics fmH5                  = fmPanel
                                                                  .getFontMetrics(h5);

    public static final Stroke      stroke10              = new BasicStroke(1f);
    public static final Stroke      stroke15              = new BasicStroke(
                                                                  1.5f);
    public static final Stroke      stroke20              = new BasicStroke(2f);
    public static final Stroke      stroke30              = new BasicStroke(3f);
    public static final Stroke      stroke40              = new BasicStroke(4f);

    public static final String      SELECT_STRING         = "6 Bauelemente auswählen";
    public static final int         MIN_DIALOG_SIZE       = fmH2.stringWidth(SELECT_STRING)
                                                                  + (4 * GAP_SIZE);
    public static final int         STATUS_HEIGTH         = PROGRESS_BAR_HEIGTH
                                                                  + (2 * STUFF_GAP)
                                                                  + STONES_ON_HAND_HEIGHT
                                                                  + fmH3.getHeight();

    public static final int[]       js                    = new int[] { 0, 0,
            0, 2, 1, 0                                   };
}
