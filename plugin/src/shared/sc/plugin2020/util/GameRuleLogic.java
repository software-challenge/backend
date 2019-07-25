package sc.plugin2020.util;

import sc.plugin2020.Board;
import sc.plugin2020.Field;
import sc.plugin2020.GameState;
import sc.shared.PlayerColor;

import java.util.ArrayList;

public class GameRuleLogic {
    Field getNeighbourInDirection(Board b, Coord c, Direction d){
        return b.getField(new Coord(c.x + d.shift().x,c.y + d.shift().y,c.z + d.shift().z));
    }

    public PlayerColor getCurrentPlayerColor(GameState gs){
        return ((gs.getTurn() % 2 == 0) ? PlayerColor.RED : PlayerColor.BLUE);
    }
}
