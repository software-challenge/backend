package sc.plugin2014.gui.renderer.display;

import static sc.plugin2014.gui.renderer.configuration.GUIConstants.*;
import static sc.plugin2014.gui.renderer.configuration.RenderConfiguration.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;
import java.util.List;
import javax.swing.JComponent;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.*;
import sc.plugin2014.gui.renderer.RenderFacade;
import sc.plugin2014.gui.renderer.components.*;
import sc.plugin2014.gui.renderer.components.Button;
import sc.plugin2014.gui.renderer.configuration.GUIConstants;
import sc.plugin2014.gui.renderer.configuration.RenderConfiguration;
import sc.plugin2014.gui.renderer.listener.GameKeyAdapter;
import sc.plugin2014.gui.renderer.listener.LayMoveAdapter;
import sc.plugin2014.gui.renderer.util.RendererUtil;
import sc.plugin2014.moves.*;
import sc.plugin2014.util.GameUtil;

public class GameRenderer extends JComponent {
    private static final long       serialVersionUID     = -7852533731353419771L;

    private PlayerColor             currentPlayer;
    private GameState               gameState;

    private BufferedImage           buffer;
    private boolean                 updateBuffer;
    private final Image             bgImage;
    private Image                   scaledBgImage;
    private final Image             progressIcon;

    private static final Object     LOCK                 = new Object();

    public static final String      LAY_STONE_LABEL      = "Steine legen";

    public static final String      EXCHANGE_STONE_LABEL = "Steine tauschen";

    public EMoveMode                moveMode             = EMoveMode.NONE;

    private final List<GUIStone>    redStones;
    private final List<GUIStone>    blueStones;
    public List<GUIStone>           sensetiveStones;
    public GUIStone                 selectedStone;
    public int                      dx, dy;
    public int                      ox, oy;

    private int                     turnToAnswer         = -1;
    private boolean                 gameEnded;

    private final MouseAdapter      layMouseAdapter      = new LayMoveAdapter(
                                                                 this);

    private final ComponentListener componentListener    = new ComponentAdapter() {

                                                             @Override
                                                             public void componentResized(
                                                                     ComponentEvent e) {
                                                                 resizeBoard();
                                                                 repaint();
                                                             }

                                                         };

    public List<GUIStone>           toLayStones;

    public final Button             actionButton;
    private final Button            takeBackButton;

    private final List<GUIStone>    animatedStones       = new ArrayList<GUIStone>();

    private final int               FPS                  = 60;

    public GameRenderer() {
        updateBuffer = true;
        bgImage = RendererUtil.getImage("resource/game/bg.png");
        toLayStones = new ArrayList<GUIStone>();
        progressIcon = RendererUtil.getImage("resource/game/progress.png");
        redStones = new LinkedList<GUIStone>();
        blueStones = new LinkedList<GUIStone>();
        sensetiveStones = new LinkedList<GUIStone>();

        setDoubleBuffered(true);
        addComponentListener(componentListener);
        GameKeyAdapter gameKeyAdapter = new GameKeyAdapter(this);
        addKeyListener(gameKeyAdapter);
        setFocusable(true);
        requestFocusInWindow();

        RenderConfiguration.loadSettings();

        setLayout(null);

        actionButton = new Button(LAY_STONE_LABEL);
        this.add(actionButton);

        actionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (actionButton.isEnabled()) {
                        sendMove();
                    }
                }
            }
        });

        actionButton.addKeyListener(gameKeyAdapter);

        takeBackButton = new Button("Steine zurücknehmen");
        this.add(takeBackButton);

        takeBackButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (takeBackButton.isEnabled()) {
                        if (moveMode == EMoveMode.LAY) {
                            for (GUIStone stone : toLayStones) {
                                stone.setHighlighted(false);
                                addStone(stone);
                            }
                            selectedStone = null;
                            toLayStones.clear();
                        }
                        else {
                            for (GUIStone guistone : sensetiveStones) {
                                guistone.setHighlighted(false);
                            }
                        }

                        moveMode = EMoveMode.NONE;

                        actionButton.setEnabled(false);
                        takeBackButton.setEnabled(false);

                        updateView();
                    }
                }
            }
        });

        takeBackButton.addKeyListener(gameKeyAdapter);

        resizeBoard();
        repaint();

        updateView();
    }

    public void updateGameState(GameState gameState) {

        if ((this.gameState != null)) {
            int turnDiff = gameState.getTurn() - this.gameState.getTurn();

            Move move = gameState.getLastMove();
            if (!myTurn()) {
                if ((move != null) && (turnDiff == 1)) {
                    if (move instanceof LayMove) {
                        moveStonesToBoard((LayMove) move,
                                gameState.getOtherPlayerColor());
                    }
                    else if (move instanceof ExchangeMove) {
                        moveStonesToBag((ExchangeMove) move,
                                gameState.getOtherPlayerColor());
                    }
                }
            }
        }

        actionButton.setEnabled(false);
        takeBackButton.setEnabled(false);

        this.gameState = gameState;
        currentPlayer = gameState.getCurrentPlayer().getPlayerColor();
        updateBuffer = true;

        toLayStones.clear();
        selectedStone = null;

        redStones.clear();
        for (int i = 0; i < gameState.getRedPlayer().getStones().size(); i++) {
            redStones.add(new GUIStone(gameState.getRedPlayer().getStones()
                    .get(i), i));
        }

        blueStones.clear();
        for (int i = 0; i < gameState.getBluePlayer().getStones().size(); i++) {
            blueStones.add(new GUIStone(gameState.getBluePlayer().getStones()
                    .get(i), i));
        }

        gameEnded = gameState.gameEnded();

        if (currentPlayer == PlayerColor.RED) {
            sensetiveStones = redStones;
        }
        else {
            sensetiveStones = blueStones;
        }

        if (gameState.gameEnded()) {
            gameEnded = true;
            currentPlayer = gameState.winner();
        }

        repaint();

    }

    private synchronized void moveStonesToBag(final ExchangeMove move,
            final PlayerColor playerColor) {
        setEnabled(false);

        for (Stone stoneToMove : move.getStonesToExchange()) {
            GUIStone animatedStone = new GUIStone(stoneToMove, -1);

            animatedStones.add(animatedStone);

            if (playerColor == PlayerColor.RED) {
                int x = BORDER_SIZE + STUFF_GAP;
                int y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH
                        - STUFF_GAP - STONE_HEIGHT;

                GUIStone stoneOnHand = null;
                for (int i = 0; i < redStones.size(); i++) {
                    GUIStone stone = redStones.get(i);
                    if ((stone != null) && stoneToMove.equals(stone.getStone())) {
                        stoneOnHand = stone;
                    }
                }

                animatedStone
                        .setX((stoneOnHand.getOriginalPositionOnHand() * (STONE_WIDTH + STUFF_GAP))
                                + x);
                animatedStone.setY(y);

                removeRedStoneFromHand(stoneToMove);
            }
            else {
                int x = getWidth() - BORDER_SIZE - STUFF_GAP - STONE_WIDTH;
                int y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH
                        - STUFF_GAP - STONE_HEIGHT;

                GUIStone stoneOnHand = null;
                for (int i = 0; i < blueStones.size(); i++) {
                    GUIStone stone = blueStones.get(i);
                    if ((stone != null) && stoneToMove.equals(stone.getStone())) {
                        stoneOnHand = stone;
                    }
                }

                animatedStone
                        .setX((-1 * stoneOnHand.getOriginalPositionOnHand() * (STONE_WIDTH + STUFF_GAP))
                                + x);
                animatedStone.setY(y);

                removeBlueStoneFromHand(stoneToMove);
            }

            final Point start = new Point(animatedStone.getX(),
                    animatedStone.getY());

            final Point target = new Point(20, 40);

            doMovement(animatedStone, start, target);

            animatedStones.remove(animatedStone);
        }

        animatedStones.clear();

        setEnabled(true);
    }

    private synchronized void moveStonesToBoard(final LayMove move,
            final PlayerColor playerColor) {

        setEnabled(false);

        for (Entry<Stone, Field> stoneToField : move.getStoneToFieldMapping()
                .entrySet()) {

            Stone originalStone = stoneToField.getKey();
            Field targetField = stoneToField.getValue();
            GUIStone animatedStone = new GUIStone(stoneToField.getKey(), -1);

            animatedStones.add(animatedStone);

            if (playerColor == PlayerColor.RED) {
                int x = BORDER_SIZE + STUFF_GAP;
                int y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH
                        - STUFF_GAP - STONE_HEIGHT;

                GUIStone stoneOnHand = null;
                for (int i = 0; i < redStones.size(); i++) {
                    GUIStone stone = redStones.get(i);
                    if ((stone != null)
                            && originalStone.equals(stone.getStone())) {
                        stoneOnHand = stone;
                    }
                }

                animatedStone
                        .setX((stoneOnHand.getOriginalPositionOnHand() * (STONE_WIDTH + STUFF_GAP))
                                + x);
                animatedStone.setY(y);

                removeRedStoneFromHand(originalStone);
            }
            else {
                int x = getWidth() - BORDER_SIZE - STUFF_GAP - STONE_WIDTH;
                int y = getHeight() - BORDER_SIZE - PROGRESS_BAR_HEIGTH
                        - STUFF_GAP - STONE_HEIGHT;

                GUIStone stoneOnHand = null;
                for (int i = 0; i < blueStones.size(); i++) {
                    GUIStone stone = blueStones.get(i);
                    if ((stone != null)
                            && originalStone.equals(stone.getStone())) {
                        stoneOnHand = stone;
                    }
                }

                animatedStone
                        .setX((-1 * stoneOnHand.getOriginalPositionOnHand() * (STONE_WIDTH + STUFF_GAP))
                                + x);
                animatedStone.setY(y);

                removeBlueStoneFromHand(originalStone);
            }

            final Point start = new Point(animatedStone.getX(),
                    animatedStone.getY());

            int boardOffsetX = GUIBoard.calculateOffsetX(
                    GUIConstants.BORDER_SIZE, getWidth()
                            - GUIConstants.BORDER_SIZE
                            - GUIConstants.SIDE_BAR_WIDTH);

            int boardOffsetY = GUIBoard.calculateOffsetY(
                    GUIConstants.BORDER_SIZE, getHeight() - STATUS_HEIGTH);

            final Point target = new Point(boardOffsetX
                    + (targetField.getPosX() * STONE_WIDTH), boardOffsetY
                    + (targetField.getPosY() * STONE_HEIGHT));

            doMovement(animatedStone, start, target);
        }
        animatedStones.clear();

        setEnabled(true);
    }

    private void removeRedStoneFromHand(Stone originalStone) {
        GUIStone toRemove = null;
        for (int i = 0; i < redStones.size(); i++) {
            GUIStone stone = redStones.get(i);
            if ((stone != null) && originalStone.equals(stone.getStone())) {
                toRemove = stone;
            }
        }
        redStones.remove(toRemove);
    }

    private void removeBlueStoneFromHand(Stone originalStone) {
        GUIStone toRemove = null;
        for (int i = 0; i < blueStones.size(); i++) {
            GUIStone stone = blueStones.get(i);
            if ((stone != null) && originalStone.equals(stone.getStone())) {
                toRemove = stone;
            }
        }
        blueStones.remove(toRemove);
    }

    private void doMovement(GUIStone animatedStone, final Point p, final Point q) {
        if (OPTIONS[MOVEMENT]) {

            double pixelPerFrame = getWidth() / (1.5 * FPS);
            double dist = Math.sqrt(Math.pow(p.x - q.x, 2)
                    + Math.pow(p.y - q.y, 2));

            final int frames = (int) Math.floor(dist / pixelPerFrame);
            final Point o = new Point(p.x, p.y);
            final Point dP = new Point(q.x - p.x, q.y - p.y);

            long start = System.currentTimeMillis();
            for (int frame = 0; frame < frames; frame++) {
                p.x = o.x + (int) ((double) (frame * dP.x) / frames);
                p.y = o.y + (int) ((double) (frame * dP.y) / frames);

                if ((frame + 1) == frames) {
                    p.x = q.x;
                    p.y = q.y;
                }
                animatedStone.moveTo(p.x, p.y);

                updateView();

                synchronized (LOCK) {
                    LOCK.notify();
                }

                try {
                    long duration = (long) ((start + ((frame + 1) * (1000.0 / FPS))) - System
                            .currentTimeMillis());
                    Thread.sleep(duration > 0 ? duration : 0);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            animatedStone.setHighlighted(true);
        }
    }

    public synchronized void updateView() {
        updateBuffer = true;
        repaint();
    }

    public synchronized void requestMove(final int turn) {
        turnToAnswer = turn;

        addMouseListener(layMouseAdapter);
        addMouseMotionListener(layMouseAdapter);
    }

    private boolean myTurn() {
        return turnToAnswer == gameState.getTurn();
    }

    private synchronized void sendMove() {
        removeMouseListener(layMouseAdapter);
        removeMouseMotionListener(layMouseAdapter);

        if (moveMode == EMoveMode.LAY) {
            LayMove layMove = new LayMove();
            for (GUIStone guistone : toLayStones) {
                layMove.layStoneOntoField(guistone.getStone(),
                        guistone.getField());
            }

            if (myTurn() && !gameEnded) {
                RenderFacade.getInstance().sendMove(layMove);
            }

            toLayStones.clear();
        }
        else if (moveMode == EMoveMode.EXCHANGE) {
            ArrayList<Stone> stonesToExchange = new ArrayList<Stone>();

            for (GUIStone guiStone : sensetiveStones) {
                if (guiStone.isHighlighted()) {
                    stonesToExchange.add(guiStone.getStone());
                }
            }

            if (myTurn() && !gameEnded) {
                ExchangeMove exchangeMove = new ExchangeMove(stonesToExchange);
                RenderFacade.getInstance().sendMove(exchangeMove);
            }
        }

        moveMode = EMoveMode.NONE;
    }

    private void resizeBoard() {

        int width = getWidth() - (2 * GUIConstants.BORDER_SIZE);
        int heigth = getHeight() - (2 * GUIConstants.BORDER_SIZE)
                - GUIConstants.PROGRESS_BAR_HEIGTH;

        if ((width > 0) && (heigth > 0)) {
            MediaTracker tracker = new MediaTracker(this);

            scaledBgImage = new BufferedImage(width, heigth,
                    BufferedImage.TYPE_3BYTE_BGR);
            scaledBgImage.getGraphics().drawImage(bgImage, 0, 0, width, heigth,
                    this);
            tracker.addImage(scaledBgImage, 0);
            try {
                tracker.waitForID(0);
            }
            catch (InterruptedException e) {
                // e.printStackTrace();
            }
        }

        System.gc();
        updateBuffer = true;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON
                        : RenderingHints.VALUE_ANTIALIAS_OFF);

        if (updateBuffer) {
            fillBuffer();
        }

        g2.drawImage(buffer, 0, 0, getWidth(), getHeight(), this);

        if (gameState != null) {
            boolean dragging = (selectedStone != null)
                    && (moveMode != EMoveMode.EXCHANGE);

            Painter.paintDynamicComponents(g2, selectedStone, getWidth(),
                    getHeight(), gameState, redStones, blueStones, this,
                    dragging);

            for (GUIStone animatedStone : animatedStones) {
                animatedStone.draw(g2, currentPlayer);
            }
        }

        if (gameEnded) {
            Painter.paintEndMessage(g2, gameState, getWidth(), getHeight());
        }

        actionButton.setBounds((getWidth() / 2) - 100, getHeight() - 110, 200,
                30);

        actionButton.paint(g);

        takeBackButton.setBounds((getWidth() / 2) - 100, getHeight() - 70, 200,
                30);
        takeBackButton.paint(g);
    }

    private void fillBuffer() {

        int imageType = OPTIONS[TRANSPARANCY] ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_BGR;
        buffer = new BufferedImage(getWidth(), getHeight(), imageType);
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON
                        : RenderingHints.VALUE_ANTIALIAS_OFF);

        boolean dragging = (selectedStone != null)
                && (moveMode != EMoveMode.EXCHANGE);

        Painter.paintStaticComponents(g2, getWidth(), getHeight(), this,
                scaledBgImage, gameState, toLayStones, this, dragging,
                selectedStone, sensetiveStones);
        if (gameState != null) {
            // printGameStatus(g2);
            Painter.paintSemiStaticComponents(g2, getWidth(), getHeight(),
                    gameState, progressIcon, this);
        }
        updateBuffer = false;
    }

    public Image getImage() {
        BufferedImage img;
        img = new BufferedImage(getWidth(), getHeight(),
                BufferedImage.TYPE_INT_RGB);
        paint(img.getGraphics());
        return img;
    }

    public void layStone(GUIStone stone) {
        if (stone != null) {
            Field belongingField = GUIBoard.getBelongingField(
                    gameState.getBoard(), GUIConstants.BORDER_SIZE,
                    GUIConstants.BORDER_SIZE, getWidth()
                            - GUIConstants.BORDER_SIZE
                            - GUIConstants.SIDE_BAR_WIDTH, getHeight()
                            - GUIConstants.STATUS_HEIGTH, stone);
            if (isLayMovePossible(belongingField, stone.getStone())) {
                stone.setField(belongingField);
                removeStone(stone);
                actionButton.setLabel(LAY_STONE_LABEL);
                moveMode = EMoveMode.LAY;
                stone.setHighlighted(true);
                toLayStones.add(stone);
                actionButton.setEnabled(true);
                takeBackButton.setEnabled(true);
            }
            else {
                stone.setHighlighted(false);
                addStone(stone);
                if (toLayStones.size() == 0) {
                    moveMode = EMoveMode.NONE;
                    actionButton.setEnabled(false);
                    takeBackButton.setEnabled(false);
                }
            }
            updateBuffer = true;
            repaint();
        }
    }

    private boolean isLayMovePossible(Field belongingField, Stone stone) {
        if ((belongingField == null) || !belongingField.isFree()) {
            return false;
        }

        if (!gameState.getBoard().hasStones() && (toLayStones.isEmpty())) {
            return true;
        }

        LayMove layMove = new LayMove();

        for (GUIStone guistone : toLayStones) {
            layMove.layStoneOntoField(guistone.getStone(), guistone.getField());
        }
        layMove.layStoneOntoField(stone, belongingField);

        return GameUtil.checkIfLayMoveIsValid(layMove, gameState.getBoard());
    }

    public void removeStone(GUIStone stone) {
        if (currentPlayer == PlayerColor.RED) {
            redStones.remove(stone);
        }
        else {
            blueStones.remove(stone);
        }

        toLayStones.remove(stone);
    }

    public void addStone(GUIStone stone) {
        if (currentPlayer == PlayerColor.RED) {
            if (redStones.size() > stone.getOriginalPositionOnHand()) {
                redStones.add(stone.getOriginalPositionOnHand(), stone);
            }
            else {
                redStones.add(stone);
            }
        }
        else {
            if (blueStones.size() > stone.getOriginalPositionOnHand()) {
                blueStones.add(stone.getOriginalPositionOnHand(), stone);
            }
            else {
                blueStones.add(stone);
            }
        }
    }

    public void toogleExchangeStone(GUIStone stone) {
        stone.setHighlighted(!stone.isHighlighted());

        for (GUIStone guiStone : sensetiveStones) {
            if (guiStone.isHighlighted()) {
                takeBackButton.setEnabled(true);
                actionButton.setEnabled(true);
                return;
            }
        }

        takeBackButton.setEnabled(false);
        actionButton.setEnabled(false);
        moveMode = EMoveMode.NONE;
    }
}
