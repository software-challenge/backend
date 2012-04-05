package sc.plugin2014.entities;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import sc.plugin2014.util.Constants;

public class StoneBag {

    private final List<Stone> stones;

    public StoneBag() {
        stones = new ArrayList<Stone>(Constants.STONES_COLOR_COUNT
                * Constants.STONES_SHAPE_COUNT
                * Constants.STONES_SAME_KIND_COUNT);
        for (int i = 0; i < Constants.STONES_COLOR_COUNT; i++) {
            for (int j = 0; j < Constants.STONES_SHAPE_COUNT; j++) {
                for (int k = 0; k < Constants.STONES_SAME_KIND_COUNT; k++) {
                    stones.add(new Stone(StoneColor.getColorFromIndex(i),
                            StoneShape.getShapeFromIndex(j)));
                }
            }
        }
        randomizeStones();
    }

    private void randomizeStones() {
        SecureRandom sr = null;
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");

            byte[] bytes = new byte[1024 / 8];
            sr.nextBytes(bytes);

            int seedByteCount = 10;
            byte[] seed = sr.generateSeed(seedByteCount);

            sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(seed);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Collections.shuffle(stones, sr);
    }

    public int getStoneCountInBag() {
        return stones.size();
    }

    public Stone drawStone() {
        if (stones.size() > 0) {
            return stones.remove(0);
        }

        throw new IllegalAccessError("Der Beutel ist leer");
    }

    public void putBackStone(Stone stone) {
        stones.add(stone);
        randomizeStones();
    }

    @Override
    public Object clone() {
        return null; // TODO
    }
}
