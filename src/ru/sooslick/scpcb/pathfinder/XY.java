package ru.sooslick.scpcb.pathfinder;

public class XY {
    public int x;
    public int y;
    public int steps;

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
}
