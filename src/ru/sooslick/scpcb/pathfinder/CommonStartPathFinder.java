package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class CommonStartPathFinder implements PathFinder {

    public final static CommonStartPathFinder instance = new CommonStartPathFinder();

    @Override
    public int calcRouteLength(MapExplorer map) {
        XY start = map.findRoom("start");
        XY closets = map.findRoom("room2closets");
        XY storage = map.findRoom("room2storage");
        XY skull = map.findRoom("room1123");
        XY testroom2 = map.findRoom("room2testroom2");
        XY room914 = map.findRoom("914");

        int defaultRoute = map.pathFind(start, closets) +
                map.pathFind(closets, testroom2) +
                map.pathFind(testroom2, room914);

        int altRoute1 = map.pathFind(start, storage) +
                map.pathFind(storage, testroom2) +
                map.pathFind(testroom2, skull) +
                map.pathFind(skull, room914);

        int altRoute2 = map.pathFind(start, storage) +
                map.pathFind(storage, skull) +
                map.pathFind(skull, testroom2) +
                map.pathFind(testroom2, room914);

        int altRoute3 = map.pathFind(start, skull) +
                map.pathFind(skull, storage) +
                map.pathFind(storage, testroom2) +
                map.pathFind(testroom2, room914);

        return Math.min(Math.min(defaultRoute, altRoute1), Math.min(altRoute2, altRoute3));
    }

    @Override
    public String getName() {
        return "914";
    }
}
