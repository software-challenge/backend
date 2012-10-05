package sc.player2014.logic;

import java.util.HashMap;
import java.util.Map.Entry;
import sc.player2014.Starter;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.*;
import sc.plugin2014.moves.*;
import sc.plugin2014.util.GameUtil;
import sc.shared.GameResult;

/**
 * Das Herz des Simpleclients: Eine sehr simple Logik,
 * die ihre Zuege zufaellig waehlt, aber gueltige Zuege macht.
 * Ausserdem werden zum Spielverlauf Konsolenausgaben gemacht.
 */
public class RandomLogic implements sc.plugin2014.IGameHandler {

    private final Starter client;
    private GameState     gameState;
    private Player        currentPlayer;

    /**
     * Erzeugt ein neues Strategieobjekt, das zufaellige Zuege taetigt.
     * 
     * @param client
     *            Der Zugrundeliegende Client der mit dem Spielserver
     *            kommunizieren kann.
     */
    public RandomLogic(Starter client) {
        this.client = client;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void gameEnded(GameResult data, PlayerColor color,
            String errorMessage) {

        System.out.println("*** Das Spiel ist beendet");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestAction() {
        System.out.println("*** Es wurde ein Zug angefordert");

        if (isStartLayMove()) {
            if (tryToDoStartLayMove() == false) {
                exchangeAllStones();
            }
        }
        else {
            if (tryToDoValidLayMove() == false) {
                exchangeAllStones();
            }
        }
    }

    private boolean isStartLayMove() {
        return (!gameState.getBoard().hasStones())
                && (gameState.getStartPlayer() == currentPlayer);
    }

    private boolean tryToDoStartLayMove() {
        StoneColor bestStoneColor = getBestStoneColor();

        if (bestStoneColor != null) {
            LayMove layMove = new LayMove();
            int fieldX = 6;
            int fieldY = 7;

            for (Stone stone : currentPlayer.getStones()) {
                if (stone.getColor() == bestStoneColor) {
                    layMove.layStoneOntoField(stone, gameState.getBoard()
                            .getField(fieldX, fieldY));
                    fieldX++;
                }
            }

            if (GameUtil.checkIfLayMoveIsValid(layMove, gameState.getBoard())) {
                sendAction(layMove);
                return true;
            }
        }

        return false;
    }

    private StoneColor getBestStoneColor() {
        HashMap<StoneColor, Integer> seenColors = new HashMap<StoneColor, Integer>();
        for (Stone stone : currentPlayer.getStones()) {
            if (seenColors.containsKey(stone.getColor())) {
                int colorCount = seenColors.get(stone.getColor()) + 1;
                seenColors.put(stone.getColor(), colorCount);
            }
            else {
                seenColors.put(stone.getColor(), 1);
            }
        }

        StoneColor bestStoneColor = null;
        int bestStoneColorCount = 1; // lay at least 2 stones

        for (Entry<StoneColor, Integer> seenColor : seenColors.entrySet()) {
            if (bestStoneColorCount < seenColor.getValue()) {
                bestStoneColor = seenColor.getKey();
                bestStoneColorCount = seenColor.getValue();
            }
        }
        return bestStoneColor;
    }

    private boolean tryToDoValidLayMove() {
        for (Stone stone : currentPlayer.getStones()) {
            for (Field field : gameState.getBoard().getFields()) {
                if (field.isFree()) {
                    LayMove layMove = new LayMove();
                    layMove.layStoneOntoField(stone, field);
                    if (GameUtil.checkIfLayMoveIsValid(layMove,
                            gameState.getBoard())) {
                        sendAction(layMove);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void exchangeAllStones() {
        ExchangeMove exchangeMove = new ExchangeMove(currentPlayer.getStones());
        sendAction(exchangeMove);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(Player player, Player otherPlayer) {
        currentPlayer = player;

        System.out.println("*** Spielerwechsel: " + player.getPlayerColor());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate(GameState gameState) {
        this.gameState = gameState;
        currentPlayer = gameState.getCurrentPlayer();

        System.out.print("*** Das Spiel geht vorran: Zug = "
                + gameState.getTurn());
        System.out.println(", Spieler = " + currentPlayer.getPlayerColor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendAction(Move move) {
        client.sendMove(move);
    }

}
