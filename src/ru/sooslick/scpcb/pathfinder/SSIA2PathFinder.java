package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class SSIA2PathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        XY room008 = map.findRoom("008");
        XY room106 = map.findRoom("room106");
        XY gateA = map.findRoom("gateaentrance");

        if (cont == null || room008 == null || room106 == null || gateA == null)
            return 9999;

        XY room079 = map.findRoom("room079");
        XY sl = map.findRoom("room2sl");
        XY room914 = map.findRoom("914");
        XY room049 = map.findRoom("room049");

        return CommonStartPathFinder.instance.calcRouteLength(map) * 2 +
                map.pathFind(room914, sl, room049, room106, room008, room106, cont) +
                map.pathFind(cont, room079) * 2 +
                map.pathFind(cont, gateA);
    }

    @Override
    public String getName() {
        return "Sed Seed Intended A2";
    }
}
