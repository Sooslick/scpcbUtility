package ru.sooslick.scpcb.map;

import static ru.sooslick.scpcb.BlitzRandom.bbRand;

public class ScpcbDoor {

    public final boolean open;
    private final boolean autoClose;

    public static ScpcbDoor createDoor(boolean open, int big) {
        return new ScpcbDoor(open, big);
    }

    public ScpcbDoor(boolean open, int big) {
        this.open = open;
        int rndState = bbRand(1, 8);
        this.autoClose = (open && big == 0 && rndState == 1);
    }

    public int getJsonValue() {
        if (autoClose)
            return 2;
        else return open ? 1 : 0;
    }
}
