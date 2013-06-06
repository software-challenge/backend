package sc.plugin2014.entities;

import java.util.ArrayList;
import java.util.List;
import sc.framework.plugins.SimplePlayer;
import com.thoughtworks.xstream.annotations.*;

@XStreamAlias(value = "player")
public class Player extends SimplePlayer implements Cloneable {

    @XStreamOmitField
    private PlayerColor       color;

    @XStreamAsAttribute
    private int               points;

    @XStreamImplicit(itemFieldName = "stone")
    private final List<Stone> stones;

    public Player() {
        stones = null;
    }

    public Player(final PlayerColor color) {
        stones = new ArrayList<Stone>();
        this.color = color;
        points = 0;
    }

    public PlayerColor getPlayerColor() {
        return color;
    }

    public List<Stone> getStones() {
        return stones;
    }

    public int getPoints() {
        return points;
    }

    public void addStone(Stone stone) {
        stones.add(stone);
    }

    public void addStone(Stone stone, int position) {
        if (position > stones.size()) {
            stones.add(stone);
        }
        else {
            stones.set(position, stone);
        }
    }

    public void removeStone(Stone stone) {
        int position = getStonePosition(stone);
        stones.set(position, null);
    }

    public int getStonePosition(Stone stone) {
        for (int i = 0; i < stones.size(); i++) {
            Stone s = stones.get(i);
            if (stone.equals(s)) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasStone(Stone stone) {
        for (Stone s : stones) {
            if (stone.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Player) && (((Player) obj).color == color);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Player clone = new Player(color);
        clone.points = points;
        if (stones != null) {
            for (Stone s : stones) {
                clone.addStone((Stone) s.clone());
            }
        }
        return clone;
    }
}
