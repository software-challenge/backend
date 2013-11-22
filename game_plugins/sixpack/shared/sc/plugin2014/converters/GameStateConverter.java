package sc.plugin2014.converters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import sc.plugin2014.GameState;
import sc.plugin2014.entities.*;
import sc.plugin2014.moves.*;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class GameStateConverter implements Converter {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return clazz.equals(GameState.class);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {

        GameState gameState = (GameState) value;

        writer.addAttribute("turn", Integer.toString(gameState.getTurn()));
        writer.addAttribute("stonesInBag",
                Integer.toString(gameState.getStoneCountInBag()));
        writer.addAttribute("start", gameState.getStartPlayerColor().toString()
                .toLowerCase());
        writer.addAttribute("current", gameState.getCurrentPlayerColor()
                .toString().toLowerCase());
 
        writer.startNode("nextStones");
        context.convertAnother(gameState.getNextStonesInBag());
        writer.endNode();

        writer.startNode("red");
        context.convertAnother(gameState.getRedPlayer());
        writer.endNode();
        writer.startNode("blue");
        context.convertAnother(gameState.getBluePlayer());
        writer.endNode();

        Board board = gameState.getBoard();
        if (board != null) {
            writer.startNode("board");
            context.convertAnother(board);
            writer.endNode();
        }

        Move move = gameState.getLastMove();
        if (move != null) {
            writer.startNode("move");
            if (move instanceof LayMove) {
                writer.addAttribute("type", MoveType.LAY.toString());
            }
            else {
                writer.addAttribute("type", MoveType.EXCHANGE.toString());
            }
            context.convertAnother(move);
            writer.endNode();
        }

        if (gameState.gameEnded()) {
            writer.startNode("condition");
            String winner = (gameState.winner() == null) ? "none" : gameState
                    .winner() + "";
            writer.addAttribute("winner", winner);
            writer.addAttribute("reason", gameState.winningReason());
            writer.endNode();
        }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        GameState gameState = new GameState(true);
        
        try {

            Field field = GameState.class.getDeclaredField("turn");
            field.setAccessible(true);
            field.set(gameState, Integer.parseInt(reader.getAttribute("turn")));
            field.setAccessible(false);

            field = GameState.class.getDeclaredField("stonesInBag");
            field.setAccessible(true);
            field.set(gameState,
                    Integer.parseInt(reader.getAttribute("stonesInBag")));
            field.setAccessible(false);

            field = GameState.class.getDeclaredField("startPlayer");
            field.setAccessible(true);
            field.set(gameState, PlayerColor.valueOf(reader.getAttribute(
                    "start").toUpperCase()));
            field.setAccessible(false);

            field = GameState.class.getDeclaredField("currentPlayer");
            field.setAccessible(true);
            field.set(gameState, PlayerColor.valueOf(reader.getAttribute(
                    "current").toUpperCase()));
            field.setAccessible(false);
            
            while (reader.hasMoreChildren()) {
            	
                reader.moveDown();

                String nodeName = reader.getNodeName();
                if (nodeName.equals("red") || nodeName.equals("blue")) {
                    Player player = (Player) context.convertAnother(gameState,
                            Player.class);
                    PlayerColor playerColor = nodeName.equals("red") ? PlayerColor.RED
                            : PlayerColor.BLUE;

                    Field playerField = GameState.class
                            .getDeclaredField(nodeName);
                    playerField.setAccessible(true);
                    playerField.set(gameState, player);
                    playerField.setAccessible(false);

                    Field colorField = Player.class.getDeclaredField("color");
                    colorField.setAccessible(true);
                    colorField.set(player, playerColor);
                    colorField.setAccessible(false);
                }
                else if (nodeName.equals("board")) {
                    Board board = (Board) context.convertAnother(gameState,
                            Board.class);
                    Field boardField = GameState.class
                            .getDeclaredField("board");
                    boardField.setAccessible(true);
                    boardField.set(gameState, board);
                    boardField.setAccessible(false);
                }
                else if (nodeName.equals("nextStones")) {
                    List<Stone> nextStones = (List<Stone>) context
                            .convertAnother(gameState, ArrayList.class);
                    StoneBag stoneBag = new StoneBag(nextStones);
                    Field nextStonesField = GameState.class
                            .getDeclaredField("nextStones");
                    nextStonesField.setAccessible(true);
                    nextStonesField.set(gameState, nextStones);
                    nextStonesField.setAccessible(false);
                    //Update the stoneBag. Otherwise no one can do a .perform on his Client
                    Field stoneBagField = GameState.class.getDeclaredField("stoneBag");
                    stoneBagField.setAccessible(true);
                    stoneBagField.set(gameState, stoneBag);
                    stoneBagField.setAccessible(false);
                }
                else if (nodeName.equals("move")) {
                    MoveType moveType = MoveType.valueOf(reader.getAttribute(
                            "type").toUpperCase());
                    Move move;

                    if (moveType == MoveType.EXCHANGE) {
                        move = (Move) context.convertAnother(gameState,
                                ExchangeMove.class);
                    }
                    else {
                        move = (Move) context.convertAnother(gameState,
                                LayMove.class);
                    }

                    Field moveField = GameState.class
                            .getDeclaredField("lastMove");
                    moveField.setAccessible(true);
                    moveField.set(gameState, move);
                    moveField.setAccessible(false);

                }
                else if (nodeName.equals("condition")) {
                    PlayerColor winner;
                    try {
                        winner = PlayerColor.valueOf(reader.getAttribute(
                                "winner").toUpperCase());
                    }
                    catch (IllegalArgumentException ex) {
                        winner = null;
                    }
                    String reason = reader.getAttribute("reason");
                    gameState.endGame(winner, reason);
                }

                reader.moveUp();

            }

        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return gameState;

    }
}
