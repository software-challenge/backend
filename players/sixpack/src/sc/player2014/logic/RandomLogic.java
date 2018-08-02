package sc.player2014.logic;

import java.util.List;
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
     * Wenn ein Zug von uns angefordert wurde, dann prüfen wir erst, ob es der
     * aller erste Zug ist. Wenn dies nicht der Fall ist, wird ein zufälliger
     * Legezug gemacht. Wenn keine regelkonformen Züge gefunden wurden, dann
     * werden alle Steine von der Hand ausgetauscht.
     */
    @Override
    public void onRequestAction() {
        System.out.println("*** Es wurde ein Zug angefordert");

        if (isOurStartLayMove()) {
            if (tryToDoStartLayMove() == false) {
                exchangeStones(currentPlayer.getStones());
            }
        }
        else {
            if (tryToDoValidLayMove() == false) {
                exchangeStones(currentPlayer.getStones());
            }
        }
    }

    /**
     * Gibt zurück, ob wir den aller ersten Zug machen müssen
     * 
     * @return <code>true</code>: Wir müssen den ersten Zug machen <br>
     *         <code>false</code>: Wir müssen nicht den ersten Zug machen
     */
    private boolean isOurStartLayMove() {
        return (!gameState.getBoard().hasStones())
                && (gameState.getStartPlayer() == currentPlayer);
    }

    /**
     * Versucht einen Startzug zu finden, indem die meiste Farbe der Steine auf
     * der Hand ausgewählt wird und dann versucht wird, ob das legen dieser
     * Reihe möglich ist.
     * 
     * @return <code>true</code>: Wir müssen den ersten Zug machen <br>
     *         <code>false</code>: Wir müssen nicht den ersten Zug machen
     */
    private boolean tryToDoStartLayMove() {
        StoneColor bestStoneColor = GameUtil.getBestStoneColor(currentPlayer
                .getStones());

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
            	layMove.addHint("Sending Start LayMove");
                sendAction(layMove);
                return true;
            }
        }

        return false;
    }

    /**
     * Versucht einen Zug zu finden, indem für alle Steine auf der Hand alle
     * freien Felder des Spielbrettes durchgegangen werden und dann versucht
     * wird, ob das legen des Steines auf das freie Feld möglich ist.
     * 
     * @return <code>true</code>: Wir müssen den ersten Zug machen <br>
     *         <code>false</code>: Wir müssen nicht den ersten Zug machen
     */
    private boolean tryToDoValidLayMove() {
        for (Stone stone : currentPlayer.getStones()) {
            for (Field field : gameState.getBoard().getFields()) {
                if (field.isFree()) {
                    LayMove layMove = new LayMove();
                    layMove.layStoneOntoField(stone, field);

                    if (GameUtil.checkIfLayMoveIsValid(layMove,
                            gameState.getBoard())) {
                    	layMove.addHint("Sending LayMove");
                        sendAction(layMove);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Wechselt die übergebenen Steine aus und beendet damit die Runde.
     */
    private void exchangeStones(List<Stone> stones) {
        ExchangeMove exchangeMove = new ExchangeMove(stones);
        exchangeMove.addHint("Sending Exchange Move");
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

        System.out.print("*** Das Spiel geht voran: Zug = "
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void gameEnded(GameResult data, PlayerColor color,
            String errorMessage) {

        System.out.println("*** Das Spiel ist beendet");
    }
}
