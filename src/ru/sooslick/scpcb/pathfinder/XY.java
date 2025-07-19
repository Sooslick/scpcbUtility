package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.map.ScpcbRoom;

public class XY {
    public int x;
    public int y;
    public int steps;

    public static XY of(ScpcbRoom r) {
        return new XY((int) (r.x / 8), (int) (r.z / 8));
    }

    public XY(int x, int y) {
        this(x, y, 0);
    }

    private XY(int x, int y, int steps) {
        this.x = x;
        this.y = y;
        this.steps = steps;
    }

    public boolean equals(XY other) {
        return x == other.x && y == other.y;
    }

    public XY getRelative(int addX, int addY) {
        return new XY(x + addX, y + addY, steps + 1);
    }

    public double distance(XY other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
}
