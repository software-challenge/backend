package sc.plugin2014.gui.renderer.display;

import static sc.plugin2014.gui.renderer.game_configuration.RenderConfiguration.*;
import java.awt.Color;
import sc.plugin2014.entities.PlayerColor;

public class DisplayHelper {
    public static boolean inner(int x, int y, int[] xs, int[] ys) {

        boolean inner = true;
        double scalar;
        double ref = 0;
        int n = xs.length;

        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            scalar = ((ys[j] - ys[i]) * (x - xs[i]))
                    + ((xs[i] - xs[j]) * (y - ys[i]));

            if (i == 0) {
                ref = Math.signum(scalar);
            }

            if (Math.signum(scalar) != ref) {
                inner = false;
                break;
            }
        }

        return inner;

    }

    public static Color getTransparentColor(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(),
                OPTIONS[TRANSPARANCY] ? alpha : 255);
    }

    public static Color getPlayerColor(PlayerColor player,
            PlayerColor currentPlayer) {
        return getPlayerColor(player, false, currentPlayer);
    }

    public static Color getPlayerColor(PlayerColor player, boolean forced,
            PlayerColor currentPlayer) {
        Color color;

        if ((player == null) || ((player == currentPlayer) || forced)) {
            return Color.DARK_GRAY;
        }

        switch (player) {
            case RED:
                color = Color.RED;
                break;
            case BLUE:
                color = Color.BLUE;
                break;

            default:
                color = Color.DARK_GRAY;
        }

        return color;
    }

    public static Color getBrightPlayerColor(PlayerColor player,
            boolean forced, PlayerColor currentPlayer) {
        Color color;

        if ((player == null) || ((player != currentPlayer) && !forced)) {
            return Color.GRAY;
        }

        switch (player) {
            case RED:
                color = new Color(255, 60, 60);
                break;
            case BLUE:
                color = new Color(80, 80, 255);
                break;

            default:
                color = Color.GRAY;
        }

        return color;
    }

    public static Color grayer(Color color) {

        double FACTOR = 0.3;
        double ROTCAF = 1.0 - FACTOR;

        int r = color.getRed();
        if (r > 128) {
            r = 128 + (int) ((r - 128) * FACTOR);
        }
        else {
            r = r + (int) ((128 - r) * ROTCAF);
        }

        int g = color.getGreen();
        if (g > 128) {
            g = 128 + (int) ((g - 128) * FACTOR);
        }
        else {
            g = g + (int) ((128 - g) * ROTCAF);
        }

        int b = color.getBlue();
        if (b > 128) {
            b = 128 + (int) ((b - 128) * FACTOR);
        }
        else {
            b = b + (int) ((128 - b) * ROTCAF);
        }

        return new Color(r, g, b, color.getAlpha());

    }
}
