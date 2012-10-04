package sc.plugin2014.gui.renderer.display;

import static sc.plugin2014.gui.renderer.configuration.GUIConstants.*;
import static sc.plugin2014.gui.renderer.configuration.RenderConfiguration.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.PlayerColor;
import sc.plugin2014.gui.renderer.components.GUIBoard;
import sc.plugin2014.gui.renderer.components.GUIStone;
import sc.plugin2014.gui.renderer.configuration.GUIConstants;
import sc.plugin2014.gui.renderer.util.ColorHelper;
import sc.plugin2014.laylogic.PointsCalculator;
import sc.plugin2014.moves.DebugHint;
import sc.plugin2014.moves.LayMove;
import sc.plugin2014.util.Constants;

public class Painter {
    public static void paintStaticComponents(Graphics2D g2, int width,
            int height, ImageObserver imgObs, Image scaledBgImage,
            GameState gameState, List<GUIStone> toLayStones,
            Component component, boolean dragging) {

        // hintergrundbild oder farbe
        if (OPTIONS[BACKGROUND] && (scaledBgImage != null)) {
            g2.drawImage(scaledBgImage, BORDER_SIZE, BORDER_SIZE, width
                    - (2 * BORDER_SIZE), height - (2 * BORDER_SIZE), imgObs);
        }
        else {
            g2.setColor(new Color(186, 217, 246));
            g2.fillRect(BORDER_SIZE, BORDER_SIZE, width - (2 * BORDER_SIZE),
                    height - (2 * BORDER_SIZE));
        }

        paintBoard(g2, width, height, gameState, toLayStones, component,
                dragging);

        g2.setColor(ColorHelper.getTransparentColor(Color.WHITE, 180));

        // fortschrittsleite, spielerinfo hintergrund
        int heightBar = PROGRESS_BAR_HEIGTH + (2 * STUFF_GAP) + STONE_HEIGHT
                + fmH3.getHeight();
        g2.fillRect(BORDER_SIZE, height - BORDER_SIZE - heightBar, width
                - (2 * BORDER_SIZE), heightBar);

        drawSideBar(g2, width, height, gameState, heightBar, toLayStones);
    }

    private static void drawSideBar(Graphics2D g2, int width, int height,
            GameState gameState, int heightBar, List<GUIStone> toLayStones) {
        g2.fillRect(width - BORDER_SIZE - SIDE_BAR_WIDTH, BORDER_SIZE,
                SIDE_BAR_WIDTH, height - (2 * BORDER_SIZE) - heightBar);

        boolean hints = OPTIONS[DEBUG_VIEW];
        int fontY = paintPlayerInfo(g2, (width - BORDER_SIZE - SIDE_BAR_WIDTH)
                + STUFF_GAP, (2 * BORDER_SIZE) + 10, PlayerColor.RED, hints,
                gameState, gameState.gameEnded(), toLayStones);
        fontY += hints ? 10 : 30;
        fontY = 25 + paintPlayerInfo(g2, (width - BORDER_SIZE - SIDE_BAR_WIDTH)
                + STUFF_GAP, fontY, PlayerColor.BLUE, hints, gameState,
                gameState.gameEnded(), toLayStones);

        if (hints && (gameState.getLastMove() != null)) {
            g2.setColor(Color.BLACK);
            g2.setFont(h5);
            int fontX = width - SIDE_BAR_WIDTH;

            for (DebugHint hint : gameState.getLastMove().getHints()) {
                g2.drawString(hint.getContent(), fontX, fontY);
                fontY += 20;
            }
        }
    }

    private static void paintBoard(Graphics2D g2, int width, int height,
            GameState gameState, List<GUIStone> toLayStones,
            Component component, boolean dragging) {
        GUIBoard.draw(g2, GUIConstants.BORDER_SIZE, GUIConstants.BORDER_SIZE,
                width - GUIConstants.BORDER_SIZE - GUIConstants.SIDE_BAR_WIDTH,
                height - STATUS_HEIGTH, toLayStones, gameState.getBoard(),
                component, dragging);
    }

    public static int paintPlayerInfo(Graphics2D g2, int fontX, int fontY,
            PlayerColor player, boolean small, GameState gameState,
            boolean gameEnded, List<GUIStone> toLayStones) {

        int[] stats = gameState.getPlayerStats(player);

        g2.setColor(ColorHelper.getTransparentColor(
                ColorHelper.getPlayerColor(player,
                        gameState.getCurrentPlayerColor()), 174));
        String points = Integer.toString(stats[0]);

        LayMove layMove = new LayMove();
        for (GUIStone guistone : toLayStones) {
            layMove.layStoneOntoField(guistone.getStone(), guistone.getField());
        }

        String pointsThisRound = "0";

        if (gameState.getCurrentPlayerColor().equals(player)) {
            pointsThisRound = Integer.toString(PointsCalculator
                    .getPointsForMove(layMove.getStoneToFieldMapping(),
                            gameState.getBoard()));
        }

        if (small) {
            fontY += 20;
            g2.setFont(h3);
            g2.drawString(points + " Punkte", fontX, fontY);
            fontY += 20;
            g2.setFont(h4);
            g2.drawString("+ " + pointsThisRound + " Punkte", fontX, fontY);
        }
        else {
            fontY += 60;
            g2.setFont(h2);
            g2.drawString(points + " Punkte", fontX, fontY);
            fontY += 60;
            g2.setFont(h3);
            g2.drawString("+ " + pointsThisRound + " Punkte", fontX, fontY);
        }

        g2.setColor(Color.BLACK);

        return fontY;
    }

    public static void paintEndMessage(Graphics2D g2, GameState gameState,
            int width, int height) {
        String msg = "Das Spiel ist zu Ende!";

        PlayerColor winner = gameState.winner();
        if (winner == PlayerColor.RED) {
            msg = gameState.getPlayerNames()[0] + " hat gewonnen!";
        }
        else if (winner == PlayerColor.BLUE) {
            msg = gameState.getPlayerNames()[1] + " hat gewonnen!";
        }

        String info = gameState.winningReason();
        int delim = info.indexOf("\\n");

        String info1 = info;
        String info2 = "";
        if (delim >= 0) {
            info1 = info.substring(0, delim);
            info2 = info.substring(delim + 2);
        }

        int msgW = fmH2.stringWidth(msg);
        int msgH = fmH2.getHeight();
        int info1W = fmH4.stringWidth(info1);
        int info2W = fmH4.stringWidth(info2);
        int infoW = Math.max(info1W, info2W);
        int infoH = (2 * fmH4.getHeight()) + 3;
        int w = Math.max(msgW, infoW);
        int h = msgH + infoH;
        int xCenter = BORDER_SIZE + ((width - SIDE_BAR_WIDTH) / 2);

        g2.setColor(ColorHelper.getTransparentColor(new Color(255, 255, 255),
                192));
        g2.fillRoundRect(xCenter - (w / 2) - 20, (height / 2) - msgH - 5 - 100,
                w + 40, h + 15, 20, 20);

        h = (height / 2) - 5 - 100;
        g2.setFont(h2);
        g2.setColor(ColorHelper.getPlayerColor(winner, true,
                gameState.getCurrentPlayerColor()));
        g2.drawString(msg, xCenter - (msgW / 2), h);

        h += msgH - 10;
        g2.setFont(h4);
        g2.setColor(Color.BLACK);
        g2.drawString(info1, xCenter - (info1W / 2), h);

        h += 20;
        g2.drawString(info2, xCenter - (info2W / 2), h);

    }

    public static void paintSemiStaticComponents(Graphics2D g2, int width,
            int height, GameState gameState, Image progressIcon,
            ImageObserver imgObs) {
        drawProgressbar(g2, width, height, gameState, progressIcon, imgObs);

        drawCanvas(g2, width, height, gameState);
    }

    private static void drawProgressbar(Graphics2D g2, int width, int height,
            GameState gameState, Image progressIcon, ImageObserver imgObs) {
        g2.setColor(Color.BLACK);
        g2.setFont(h4);
        int left = fmH4.stringWidth("Spielfortschritt:") + BORDER_SIZE + 30;
        int right = width - left - 30;
        int fontY = ((height - BORDER_SIZE - (PROGRESS_BAR_HEIGTH / 2)) + (fmH4
                .getHeight() / 2)) - 4;
        g2.drawString("Spielfortschritt:", BORDER_SIZE + 10, fontY);

        int round = gameState.getRound() + 1;
        String roundString = Integer.toString(gameState.getRound() + 1);
        if (round > Constants.ROUND_LIMIT) {
            roundString = Integer.toString(Constants.ROUND_LIMIT);
        }

        g2.drawString("Runde " + roundString + " von " + Constants.ROUND_LIMIT,
                right + 30, fontY);

        int progress = (gameState.getTurn() * (right - left))
                / (2 * Constants.ROUND_LIMIT);
        int progressTop = (height - BORDER_SIZE - PROGRESS_BAR_HEIGTH) + 8;

        g2.setColor(Color.GRAY);
        g2.fillRoundRect(left, progressTop, right - left,
                PROGRESS_BAR_HEIGTH - 16, 10, 10);

        g2.setColor(Color.GREEN);
        g2.fillRoundRect(left, progressTop, progress, PROGRESS_BAR_HEIGTH - 16,
                10, 10);

        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(left, progressTop, right - left,
                PROGRESS_BAR_HEIGTH - 16, 10, 10);

        g2.drawImage(progressIcon,
                ((left + progress) - (PROGRESS_ICON_SIZE / 2)) + 3, height
                        - PROGRESS_ICON_SIZE - 10, PROGRESS_ICON_SIZE,
                PROGRESS_ICON_SIZE, imgObs);
    }

    private static void drawCanvas(Graphics2D g2, int width, int height,
            GameState gameState) {
        g2.setStroke(stroke15);
        g2.setColor(ColorHelper.getPlayerColor(
                gameState.getCurrentPlayerColor(),
                gameState.getCurrentPlayerColor()));
        g2.fillRect(0, 0, width, BORDER_SIZE);
        g2.fillRect(0, height - BORDER_SIZE, width, BORDER_SIZE);
        g2.fillRect(0, 0, BORDER_SIZE, height);
        g2.fillRect(width - BORDER_SIZE, 0, BORDER_SIZE, height);
    }

    private static void drawRedPlayer(Graphics2D g2, int height,
            GameState gameState, List<GUIStone> redStones) {
        Player player = gameState.getRedPlayer();
        int x = BORDER_SIZE + STUFF_GAP;
        int y = height - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP
                - STONE_HEIGHT;

        for (GUIStone stone : redStones) {
            stone.setX(x);
            stone.setY(y);
            stone.draw(g2);
            x += STONE_WIDTH + STUFF_GAP;
        }

        g2.setFont(h3);
        y -= (STUFF_GAP + 5);
        g2.setColor(ColorHelper.getPlayerColor(PlayerColor.RED,
                gameState.getCurrentPlayerColor()));
        g2.drawString(player.getDisplayName(), 2 * BORDER_SIZE, y);
    }

    private static void drawBluePlayer(Graphics2D g2, int width, int height,
            GameState gameState, List<GUIStone> blueStones) {
        Player player = gameState.getBluePlayer();
        int x = width - BORDER_SIZE - STUFF_GAP - STONE_WIDTH;
        int y = height - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP
                - STONE_HEIGHT;

        for (GUIStone stone : blueStones) {
            stone.setX(x);
            stone.setY(y);
            stone.draw(g2);
            x -= STONE_WIDTH + STUFF_GAP;
        }

        g2.setFont(h3);
        y -= (STUFF_GAP + 5);
        g2.setColor(ColorHelper.getPlayerColor(PlayerColor.BLUE,
                gameState.getCurrentPlayerColor()));
        int nameWidth = fmH3.stringWidth(player.getDisplayName());
        g2.drawString(player.getDisplayName(), width - (2 * BORDER_SIZE)
                - nameWidth, y);
    }

    public static void paintDynamicComponents(Graphics2D g2,
            GUIStone selectedStone, int width, int height, GameState gameState,
            List<GUIStone> redStones, List<GUIStone> blueStones,
            Component component) {

        if (selectedStone != null) {
            GUIBoard.drawGrid(g2, BORDER_SIZE, BORDER_SIZE, width - BORDER_SIZE
                    - SIDE_BAR_WIDTH, height - STATUS_HEIGTH,
                    gameState.getBoard(), component);

            selectedStone.draw(g2);
        }

        drawRedPlayer(g2, height, gameState, redStones);

        drawBluePlayer(g2, width, height, gameState, blueStones);

    }
}
