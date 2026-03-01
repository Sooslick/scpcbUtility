package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class CommonStartPathFinder implements PathFinder {

    public final static CommonStartPathFinder instance = new CommonStartPathFinder();

    @Override
    public int calcRouteLength(MapExplorer map) {
        XY start = map.findRoom("start");
        XY closets = map.findRoom("room2closets");
        XY room970 = map.findRoom("room2storage");
        XY testroom2 = map.findRoom("room2testroom2");
        XY room914 = map.findRoom("914");

        int defaultRoute = map.pathFind(start, closets, testroom2, room914);
        int altRoute = map.pathFind(start, room970, testroom2, closets, room914);

        return Math.min(defaultRoute, altRoute);
    }

    @Override
    public String getName() {
        return "914";
    }
}
