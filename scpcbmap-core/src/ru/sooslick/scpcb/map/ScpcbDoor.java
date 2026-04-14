package ru.sooslick.scpcb.map;

import ru.sooslick.scpcb.BlitzRandom;

public class ScpcbDoor {

    public final boolean open;
    private final boolean autoClose;

    public ScpcbDoor(boolean open, int big, BlitzRandom rng) {
        this.open = open;
        int rndState = rng.bbRand(1, 8);
        this.autoClose = (open && big == 0 && rndState == 1);
    }

    public int getJsonValue() {
        if (autoClose)
            return 2;
        else return open ? 1 : 0;
    }

    public record DoorFactory(BlitzRandom rng) {
        public ScpcbDoor createDoor(boolean open, int big) {
            return new ScpcbDoor(open, big, rng);
        }
    }
}
