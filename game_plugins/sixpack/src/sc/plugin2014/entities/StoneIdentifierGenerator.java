package sc.plugin2014.entities;

public class StoneIdentifierGenerator {
    private static int nextId = 0;

    public static void reset() {
        nextId = 0;
    }

    public static int getNextId() {
        nextId++;
        return nextId;
    }
}
