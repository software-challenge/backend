package sc.plugin2014.util;

import sc.plugin2014.GamePlugin;

public class Constants {

    public final static int   ROUND_LIMIT              = GamePlugin.MAX_TURN_COUNT;

    public static final int   CITIES                   = 4;
    public static final int   SLOTS                    = 5;
    public static final int   CARDS_PER_SLOT           = 3;
    public static final int   CARDS_PER_PLAYER         = 4;

    public static final int   SELECTION_SIZE           = 6;
    public static final int   MAX_SEGMENT_SIZE         = 4;
    public static final int[] SEGMENT_AMOUNTS          = new int[] { 11, 6, 4,
            3                                         };

    public static final int   POINTS_PER_HIGHEST_TOWER = 3;
    public static final int   POINTS_PER_OWEND_CITY    = 2;
    public static final int   POINTS_PER_TOWER         = 1;

}