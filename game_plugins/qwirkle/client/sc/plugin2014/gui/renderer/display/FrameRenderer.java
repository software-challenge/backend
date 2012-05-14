/**
 * 
 */
package sc.plugin2014.gui.renderer.display;

import static sc.plugin2014.gui.renderer.game_configuration.RenderConfiguration.*;
import static sc.plugin2014.util.Constants.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.PlayerColor;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.gui.renderer.RenderFacade;
import sc.plugin2014.gui.renderer.RendererUtil;
import sc.plugin2014.gui.renderer.display.listener.GameKeyAdapter;
import sc.plugin2014.gui.renderer.display.listener.LayMoveAdapter;
import sc.plugin2014.gui.renderer.game_configuration.RenderConfiguration;
import sc.plugin2014.moves.LayMove;
import sc.plugin2014.moves.Move;

/**
 * @author tkra, ffi
 */
public class FrameRenderer extends JComponent {
    private static final long       serialVersionUID  = -7852533731353419771L;

    public static final Object      LOCK              = new Object();

    // current (game) state
    private PlayerColor             currentPlayer;
    private GameState               gameState;

    // image components
    private BufferedImage           buffer;
    private boolean                 updateBuffer;
    private final Image             bgImage;
    private Image                   scaledBgImage;
    private final Image             progressIcon;

    // steine
    private final List<Stone>       redStones;
    private final List<Stone>       blueStones;
    private List<Stone>             sensetiveStones;
    private Stone                   selectedStone;
    private int                     dx, dy;
    private int                     ox, oy;

    // sonstiges
    private int                     turnToAnswer      = -1;
    private boolean                 gameEnded;

    private final MouseAdapter      layMouseAdapter   = new LayMoveAdapter(this);

    private final ComponentListener componentListener = new ComponentAdapter() {

                                                          @Override
                                                          public void componentResized(
                                                                  ComponentEvent e) {
                                                              resizeBoard();
                                                              repaint();
                                                          }

                                                      };

    public FrameRenderer() {
        updateBuffer = true;
        bgImage = RendererUtil.getImage("resource/game/bg.png");
        progressIcon = RendererUtil.getImage("resource/game/progress.png");
        redStones = new LinkedList<Stone>();
        blueStones = new LinkedList<Stone>();
        sensetiveStones = new LinkedList<Stone>();

        setMinimumSize(new Dimension(
                (2 * STONES_PER_PLAYER * (GUIConstants.STONES_ON_HAND_WIDTH + GUIConstants.STUFF_GAP))
                        + (2 * 1 * (GUIConstants.TOWER_TOTAL_WIDTH + GUIConstants.STUFF_GAP)),
                600));

        setDoubleBuffered(true);
        addComponentListener(componentListener);
        addKeyListener(new GameKeyAdapter(this));
        setFocusable(true);
        requestFocusInWindow();

        RenderConfiguration.loadSettings();

        resizeBoard();
        repaint();

    }

    public void updateGameState(GameState gameState) {

        if ((this.gameState != null)) {
            int turnDiff = gameState.getTurn() - this.gameState.getTurn();

            Move move = gameState.getLastMove();
            if ((move != null) && (turnDiff == 1)) {
                moveSegment(gameState);
            }
        }

        // aktuellen spielstand sichern
        this.gameState = gameState;
        currentPlayer = gameState.getCurrentPlayer().getPlayerColor();
        updateBuffer = true;

        selectedStone = null;

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

    private synchronized void moveSegment(final GameState gameState) {

        final int FPS = 30;

        setEnabled(false);
        final LayMove move = (LayMove) gameState.getLastMove();

        /*
         * final Point p = new Point(selectedStone.x, selectedStone.y);
         * final Point q = new Point(targetTower.innerX - TOWER_LEFT_WIDTH,
         * targetTower.innerY);
         * 
         * if (OPTIONS[MOVEMENT]) {
         * 
         * double pixelPerFrame = getWidth() / (1.5 * FPS);
         * double dist = Math.sqrt(Math.pow(p.x - q.x, 2)
         * + Math.pow(p.y - q.y, 2));
         * 
         * final int frames = (int) Math.ceil(dist / pixelPerFrame);
         * final Point o = new Point(p.x, p.y);
         * final Point dP = new Point(q.x - p.x, q.y - p.y);
         * 
         * long start = System.currentTimeMillis();
         * int h = (selectedStone.size * TOWER_STORIE_HEIGTH)
         * + TOWER_LEFT_HEIGTH + TOWER_RIGHT_HEIGTH + 10;
         * for (int frame = 0; frame < frames; frame++) {
         * 
         * int oldx = selectedStone.xs[0] - 5;
         * int oldy = selectedStone.ys[4] - 5;
         * 
         * p.x = o.x + (int) ((double) (frame * dP.x) / (double) frames);
         * p.y = o.y + (int) ((double) (frame * dP.y) / (double) frames);
         * selectedStone.moveTo(p.x, p.y);
         * 
         * // invalidate();
         * // getParent().repaint();
         * 
         * repaint(oldx, oldy, TOWER_TOTAL_WIDTH + 10, h);
         * repaint(selectedStone.xs[0] - 5, selectedStone.ys[4] - 5,
         * TOWER_TOTAL_WIDTH + 10, h);
         * 
         * synchronized (LOCK) {
         * LOCK.notify();
         * }
         * 
         * try {
         * long duration = (start + ((frame + 1) * (1000 / FPS)))
         * - System.currentTimeMillis();
         * Thread.sleep(duration > 0 ? duration : 0);
         * }
         * catch (InterruptedException e) {
         * e.printStackTrace();
         * }
         * 
         * }
         * }
         * 
         * zum schluss richtig positionieren
         * selectedStone.moveTo(q.x, q.y);
         */
        setEnabled(true);

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

    private synchronized void sendMove(final Move move) {

        removeMouseListener(layMouseAdapter);
        removeMouseMotionListener(layMouseAdapter);

        if (myTurn() && !gameEnded) {
            RenderFacade.getInstance().sendMove(move);
            turnToAnswer = -1;
        }
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
                e.printStackTrace();
            }
        }

        DrawAdditional.recreatePlayerSegments(getWidth(), getHeight(),
                redStones, blueStones);

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
            Painter.paintDynamicComponents(g2);
        }

        if (gameEnded) {
            Painter.paintEndMessage(g2, gameState, getWidth(), getHeight());
        }

        // bmFrames++;
        // // repainted = true;
        synchronized (LOCK) {
            LOCK.notify();
        }

    }

    private void fillBuffer() {

        int imageType = OPTIONS[TRANSPARANCY] ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_BGR;
        buffer = new BufferedImage(getWidth(), getHeight(), imageType);
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                OPTIONS[ANTIALIASING] ? RenderingHints.VALUE_ANTIALIAS_ON
                        : RenderingHints.VALUE_ANTIALIAS_OFF);

        Painter.paintStaticComponents(g2, getWidth(), getHeight(), this,
                scaledBgImage, gameState);
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
}
