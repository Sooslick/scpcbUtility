package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class SSIPathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        XY room008 = map.findRoom("008");
        XY room106 = map.findRoom("room106");

        if (cont == null || room008 == null || room106 == null)
            return 9999;

        XY gateA = map.findRoom("gateaentrance");
        XY gateB = map.findRoom("exit1");
        XY room079 = map.findRoom("room079");
        XY sl = map.findRoom("room2sl");
        XY room914 = map.findRoom("914");
        XY room049 = map.findRoom("room049");

        int startLength = CommonStartPathFinder.instance.calcRouteLength(map) + map.pathFind(room914, sl);
        int routeA = map.pathFind(sl, room008, room049, room106, cont);
        int routeB = map.pathFind(sl, room049, room008, room106, cont);
        int routeC = map.pathFind(sl, room049, room106, room008, cont);

        return startLength +
                Math.min(Math.min(routeA, routeB), routeC) +
                map.pathFind(cont, room079) * 2 +
                Math.min(map.pathFind(cont, gateA), map.pathFind(cont, gateB));
    }

    @Override
    public String getName() {
        return "Set Seed Intended A1+B1";
    }
}
