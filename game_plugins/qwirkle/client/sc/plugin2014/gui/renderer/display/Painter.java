package sc.plugin2014.gui.renderer.display;

import static sc.plugin2014.gui.renderer.display.GUIConstants.*;
import static sc.plugin2014.gui.renderer.game_configuration.RenderConfiguration.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.*;
import sc.plugin2014.gui.renderer.RendererUtil;
import sc.plugin2014.moves.DebugHint;
import sc.plugin2014.util.Constants;

public class Painter {
    public static void paintStaticComponents(Graphics2D g2, int width,
            int height, ImageObserver imgObs, Image scaledBgImage,
            GameState gameState) {

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

        // fortschrittsleite, spielerinfo und seitenleiste
        g2.setColor(DisplayHelper.getTransparentColor(new Color(255, 255, 255),
                180));

        // fortschrittsleite, spielerinfo hintergrund
        int heightBar = PROGRESS_BAR_HEIGTH + (2 * STUFF_GAP)
                + STONES_ON_HAND_HEIGHT + fmH3.getHeight();
        g2.fillRect(BORDER_SIZE, height - BORDER_SIZE - heightBar, width
                - (2 * BORDER_SIZE), heightBar);

        // seitenleiste, hintergrund
        g2.fillRect(width - BORDER_SIZE - SIDE_BAR_WIDTH, BORDER_SIZE,
                SIDE_BAR_WIDTH, height - (2 * BORDER_SIZE) - heightBar);

        // seitenleiste, info
        boolean hints = OPTIONS[DEBUG_VIEW];
        int fontY = paintPlayerInfo(g2, (width - BORDER_SIZE - SIDE_BAR_WIDTH)
                + STUFF_GAP, (2 * BORDER_SIZE) + 10, PlayerColor.RED, hints,
                gameState, gameState.gameEnded());
        fontY += hints ? 10 : 30;
        fontY = 25 + paintPlayerInfo(g2, (width - BORDER_SIZE - SIDE_BAR_WIDTH)
                + STUFF_GAP, fontY, PlayerColor.BLUE, hints, gameState,
                gameState.gameEnded());

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

    public static int paintPlayerInfo(Graphics2D g2, int fontX, int fontY,
            PlayerColor player, boolean small, GameState gameState,
            boolean gameEnded) {

        int[] stats = gameState.getPlayerStats(player);

        g2.setColor(DisplayHelper.getTransparentColor(
                DisplayHelper.getPlayerColor(player,
                        gameState.getCurrentPlayerColor()), 174));
        String points = Integer.toString(stats[0]);
        if (small) {
            fontY += 20;
            g2.setFont(h3);
            g2.drawString(points + " Punkte", fontX, fontY);
        }
        else {
            fontY += 60;
            g2.setFont(h2);
            g2.drawString(points + " Punkte", fontX, fontY);
        }

        g2.setColor(Color.BLACK);

        // fontY += 20;
        // g2.setFont(h5);
        // g2.drawString(s, fontX, fontY);

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

        g2.setColor(DisplayHelper.getTransparentColor(new Color(255, 255, 255),
                192));
        g2.fillRoundRect(xCenter - (w / 2) - 20, (height / 2) - msgH - 5 - 100,
                w + 40, h + 15, 20, 20);

        h = (height / 2) - 5 - 100;
        g2.setFont(h2);
        g2.setColor(DisplayHelper.getPlayerColor(winner, true,
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

        // fortschrittsbalken
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

        g2.setStroke(stroke15);

        // rahmen
        g2.setColor(DisplayHelper.getPlayerColor(
                gameState.getCurrentPlayerColor(),
                gameState.getCurrentPlayerColor()));
        g2.fillRect(0, 0, width, BORDER_SIZE);
        g2.fillRect(0, height - BORDER_SIZE, width, BORDER_SIZE);
        g2.fillRect(0, 0, BORDER_SIZE, height);
        g2.fillRect(width - BORDER_SIZE, 0, BORDER_SIZE, height);

        // spielerinfo rot
        Player player = gameState.getRedPlayer();
        int x = BORDER_SIZE + STUFF_GAP;
        int y = height - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP
                - STONES_ON_HAND_HEIGHT;

        for (Stone stone : player.getStones()) {
            paintStoneOnHand(g2, x, y, stone);
            x += STONES_ON_HAND_WIDTH + STUFF_GAP;
        }

        // synchronized (redSegments) {
        // for (TowerData tower : redSegments) {
        // if (tower != selectedSegment) {
        // paintTower(g2, tower);
        // }
        // }
        // }

        g2.setFont(h3);
        y -= (STUFF_GAP + 5);
        g2.setColor(DisplayHelper.getPlayerColor(PlayerColor.RED,
                gameState.getCurrentPlayerColor()));
        g2.drawString(player.getDisplayName(), 2 * BORDER_SIZE, y);

        // spielerinfo blau
        player = gameState.getBluePlayer();
        x = width - BORDER_SIZE - STUFF_GAP - STONES_ON_HAND_WIDTH;
        y = height - BORDER_SIZE - PROGRESS_BAR_HEIGTH - STUFF_GAP
                - STONES_ON_HAND_HEIGHT;

        for (Stone stone : player.getStones()) {
            paintStoneOnHand(g2, x, y, stone);
            x -= STONES_ON_HAND_WIDTH + STUFF_GAP;
        }

        // synchronized (blueSegments) {
        // for (TowerData tower : blueSegments) {
        // if (tower != selectedSegment) {
        // paintTower(g2, tower);
        // }
        // }
        // }

        g2.setFont(h3);
        y -= (STUFF_GAP + 5);
        g2.setColor(DisplayHelper.getPlayerColor(PlayerColor.BLUE,
                gameState.getCurrentPlayerColor()));
        int nameWidth = fmH3.stringWidth(player.getDisplayName());
        g2.drawString(player.getDisplayName(), width - (2 * BORDER_SIZE)
                - nameWidth, y);

        // // staedte
        // g2.setFont(h5);
        // for (int i = CITIES - 1; i >= 0; i--) {
        // for (int j = SLOTS - 1; j >= 0; j--) {
        // TowerData data = cityTowers[i][j];
        // paintTower(g2, data, true, data == selectedTower);
        //
        // if (data.size > 0) {
        // g2.setColor(Color.DARK_GRAY.darker());
        // g2.fillRoundRect(data.xs[1], data.ys[1] - fmH5.getHeight()
        // - 6, TOWER_RIGHT_WIDTH, fmH5.getHeight(), 8, 8);
        // g2.setColor(data.diff > MAX_SEGMENT_SIZE ? Color.YELLOW
        // : Color.WHITE);
        // String s = Integer.toString(data.diff);
        // g2.drawString(
        // s,
        // data.xs[1]
        // + ((TOWER_RIGHT_WIDTH - fmH5.stringWidth(s)) / 2),
        // data.ys[1] - 8);
        // }
        // }
        // }
        //
        // for (int i = 0; i < Constants.CITIES; i++) {
        // // stadtnamen
        // g2.setFont(h2);
        // g2.setColor(Color.GREEN);
        //
        // TowerData base = cityTowers[i][0];
        // int ownership = 0;
        // for (int j = 0; j < Constants.SLOTS; j++) {
        // if (cityTowers[i][j].owner == PlayerColor.RED) {
        // ownership++;
        // }
        // else if (cityTowers[i][j].owner == PlayerColor.BLUE) {
        // ownership--;
        // }
        // }
        // PlayerColor owner = ownership > 0 ? PlayerColor.RED
        // : (ownership < 0 ? PlayerColor.BLUE : null);
        // g2.setColor(getPlayerColor(owner, true));
        //
        // g2.translate(base.xs[2] + 20, base.ys[1] + 10);
        // g2.rotate(-0.785);
        // g2.shear(0.42, 0);
        //
        // g2.drawString(CITY_NAMES[i], 0, 0);
        // g2.setTransform(new AffineTransform());
        // }

        // fortschirttsbalken, icon
        g2.drawImage(progressIcon,
                ((left + progress) - (PROGRESS_ICON_SIZE / 2)) + 3, height
                        - PROGRESS_ICON_SIZE - 3, PROGRESS_ICON_SIZE,
                PROGRESS_ICON_SIZE, imgObs);

    }

    public static void paintDynamicComponents(Graphics2D g2) {

        // if (selectedSegment != null) {
        // paintTower(g2, selectedSegment);
        // }

    }

    public static void paintSelectDialog(Graphics2D g2) {

        // g2.setColor(getTransparentColor(new Color(255, 255, 255), 192));
        // g2.fillRoundRect(selectX, selectY, selectWidth, selectHeight, 25,
        // 25);
        //
        // String msg = SELECT_STRING;
        // int msgW = fmH2.stringWidth(msg);
        // int msgH = fmH2.getHeight();
        //
        // // menueueberschrift
        // g2.setFont(h2);
        // g2.setColor(currentPlayerColor);
        // g2.drawString(msg, (getWidth() - SIDE_BAR_WIDTH - msgW) / 2, (selectY
        // + GAP_SIZE + msgH) - 5);
        //
        // // tuerme zeichnen
        // synchronized (selectTowers) {
        // for (TowerData tower : selectTowers) {
        // paintTower(g2, tower);
        // }
        // }
        //
        // if (selectionOkay) {
        //
        // // button
        // String okay = "Abschicken";
        // int okayW = fmH4.stringWidth(okay);
        //
        // g2.setColor(selectionPressed ? new Color(50, 130, 50) : new Color(
        // 40, 140, 40));
        // g2.fillRoundRect(selectButton[0], selectButton[1], selectButton[2],
        // selectButton[3], 15, 15);
        //
        // g2.setFont(h4);
        // g2.setColor(Color.DARK_GRAY);
        // g2.drawString(okay, (getWidth() - SIDE_BAR_WIDTH - okayW) / 2,
        // (selectY + selectHeight) - GAP_SIZE - 10);
        //
        // g2.setStroke(stroke15);
        // g2.setColor(Color.DARK_GRAY);
        // g2.drawRoundRect(selectButton[0], selectButton[1], selectButton[2],
        // selectButton[3], 15, 15);
        //
        // }
        // else {
        //
        // String remark;
        // if (selectionSize > Constants.SELECTION_SIZE) {
        // int dif = selectionSize - Constants.SELECTION_SIZE;
        // if (dif == 1) {
        // remark = "Es muss noch 1 Bauelement abgewählt werden.";
        // }
        // else {
        // remark = "Es müssen noch " + dif
        // + " Bauelemente abgewählt werden.";
        // }
        // }
        // else {
        // int dif = Constants.SELECTION_SIZE - selectionSize;
        // if (dif == 1) {
        // remark = "Es muss noch 1 Bauelement ausgewählt werden.";
        // }
        // else {
        // remark = "Es müssen noch " + dif
        // + " Bauelemente ausgewählt werden.";
        // }
        // }
        //
        // g2.setFont(h4);
        // int remarkW = fmH4.stringWidth(remark);
        // g2.setColor(currentPlayerColor);
        // g2.drawString(remark, (getWidth() - SIDE_BAR_WIDTH - remarkW) / 2,
        // (selectY + selectHeight) - GAP_SIZE - 10);
        //
        // }

    }

    // public static void paintTower(Graphics2D g2, TowerData tower) {
    //
    // paintTower(g2, tower, false, false);
    //
    // }
    //
    // public static void paintTower(Graphics2D g2, TowerData tower,
    // boolean cityTower, boolean glow) {
    //
    // boolean drawCrane = OPTIONS[CRANES] && cityTower && (tower.size > 0)
    // && !gameEnded;
    // drawCrane = drawCrane
    // && (tower.diff <= getHighestSegment(tower.owner.getOpponent(),
    // true));
    // int frameDisplacement = tower.diff * TOWER_STORIE_HEIGTH;
    // if (drawCrane) {
    // int craneX = (tower.xs[4] + tower.xs[5]) / 2;
    // int craneY = (tower.ys[4] + tower.ys[5]) / 2;
    // int craneJibLeftX = craneX - 10;
    // int craneJibLeftY = craneY - 4 - frameDisplacement - 30;
    // int craneJibRightX = ((tower.xs[3] + tower.innerX) / 2) + 10;
    // int craneJibRightY = (((tower.ys[3] + tower.innerY) / 2) + 4)
    // - frameDisplacement - 30;
    // Color color = getPlayerColor(tower.owner.getOpponent(), true);
    // boolean canMatchHeigth = tower.diff <= getHighestSegment(
    // tower.owner.getOpponent(), false);
    // g2.setColor(canMatchHeigth ? color.darker() : grayer(color));
    // g2.setStroke(stroke40);
    // g2.drawLine(craneJibLeftX, craneJibLeftY, craneJibRightX,
    // craneJibRightY);
    // g2.drawLine(craneX, craneY, craneX, craneY - frameDisplacement - 40);
    // }
    //
    // g2.setColor(glow ? currentPlayerColor : getBrightPlayerColor(
    // tower.owner, cityTower));
    // g2.fillPolygon(tower.xs, tower.ys, 6);
    //
    // if (!glow) {
    // int oppSize = (tower.size - tower.diff) / 2;
    // if ((tower.size > 1) && (oppSize > 0) && cityTower) {
    // int[] otherXs = new int[6];
    // int[] otherYs = new int[6];
    // int opponentHeigth = oppSize * TOWER_STORIE_HEIGTH;
    // for (int i = 0; i < 6; i++) {
    // otherXs[i] = tower.xs[i];
    // otherYs[i] = tower.ys[i];
    // if (i > 2) {
    // otherYs[i] = tower.ys[js[i]] - opponentHeigth;
    // otherXs[i] = tower.xs[js[i]];
    // }
    // }
    // g2.setColor(getBrightPlayerColor(tower.owner.getOpponent(),
    // true));
    // g2.fillPolygon(otherXs, otherYs, 6);
    // }
    // }
    //
    // g2.setStroke(stroke20);
    // g2.setColor(tower.highlited ? currentPlayerColor : Color.DARK_GRAY);
    // g2.drawPolygon(tower.xs, tower.ys, 6);
    //
    // g2.setStroke(stroke10);
    // int diff = TOWER_STORIE_HEIGTH;
    // for (int i = 1; i < tower.size; i++) {
    // g2.drawLine(tower.xs[0], tower.ys[0] - diff, tower.xs[1],
    // tower.ys[1] - diff);
    // g2.drawLine(tower.xs[1], tower.ys[1] - diff, tower.xs[2],
    // tower.ys[2] - diff);
    // diff += TOWER_STORIE_HEIGTH;
    // }
    //
    // g2.setStroke(stroke20);
    // g2.drawLine(tower.innerX, tower.innerY, tower.xs[1], tower.ys[1]);
    // g2.drawLine(tower.innerX, tower.innerY, tower.xs[3], tower.ys[3]);
    // g2.drawLine(tower.innerX, tower.innerY, tower.xs[5], tower.ys[5]);
    //
    // Stroke prevStroke = g2.getStroke();
    // if (drawCrane) {
    // int[] frameXs = new int[] { tower.xs[3], tower.xs[4], tower.xs[5],
    // tower.innerX };
    // int[] frameYs = new int[] { tower.ys[3], tower.ys[4], tower.ys[5],
    // tower.innerY };
    // for (int i = 0; i < 4; i++) {
    // frameYs[i] -= frameDisplacement;
    // }
    // g2.setColor(new Color(128, 128, 128, 128));
    // g2.fillPolygon(frameXs, frameYs, 4);
    // g2.setColor(Color.YELLOW.darker());
    // g2.setStroke(stroke30);
    // g2.drawPolygon(frameXs, frameYs, 4);
    // g2.setColor(Color.DARK_GRAY);
    // g2.setStroke(stroke15);
    // for (int i = 0; i < 4; i++) {
    // g2.drawLine((tower.xs[0] + tower.xs[2]) / 2, frameYs[1] - 15,
    // frameXs[i], frameYs[i]);
    //
    // }
    // }
    // g2.setStroke(prevStroke);
    //
    // }

    public static void paintStoneOnHand(Graphics2D g2, int x, int y, Stone stone) {
        Image image = RendererUtil.getImage("resource/stones/"
                + stone.getShape().toString().toLowerCase() + "_"
                + stone.getColor().toString().toLowerCase() + ".png");
        g2.drawImage(image, x, y, GUIConstants.STONES_ON_HAND_WIDTH,
                GUIConstants.STONES_ON_HAND_HEIGHT, fmPanel);
    }
}
