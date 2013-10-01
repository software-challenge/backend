package sc.plugin2014.util;

import sc.plugin2014.GamePlugin;

/**
 * Klasse, welche Konstanten beherbergt.
 * @author ffi
 *
 */
public class Constants {

    /**
     * Gibt das Rundenlimit an
     */
    public final static int ROUND_LIMIT                = GamePlugin.MAX_TURN_COUNT;

    /**
     * Gibt an, wieviele Steine ein Spieler auf der Hand halten darf.
     */
    public static final int STONES_PER_PLAYER          = 6;

    public static final int POINTS_AT_END              = 6;

    /**
     * Gibt die Feldanzahl in x-Richtung an.
     */
    public static final int FIELDS_IN_X_DIM            = 16;
    /**
     * Gibt die Feldanzahl in y-Richtung an.
     */
    public static final int FIELDS_IN_Y_DIM            = 16;

    /**
     * Gibt die Anzahl der Spielsteinfarben an.
     */
    public static final int STONES_COLOR_COUNT         = 6;
    /**
     * Gibt die Anzahl der Spielsteinformen an
     */
    public static final int STONES_SHAPE_COUNT         = 6;
    /**
     * Gibt an wie viele von gleicher Form/Farbe existieren
     */
    public static final int STONES_SAME_KIND_COUNT     = 3;

    /**
     * Gibt an wie viele Steine des Beutels einsehbar sind.
     */
    public static final int STONES_OPEN_FROM_BAG_COUNT = 12;
}