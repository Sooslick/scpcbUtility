package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class SSIB2PathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        XY room008 = map.findRoom("008");
        XY room106 = map.findRoom("room106");
        XY gateB = map.findRoom("exit1");

        if (cont == null || room008 == null || room106 == null)
            return 9999;

        XY room079 = map.findRoom("room079");
        XY sl = map.findRoom("room2sl");
        XY room914 = map.findRoom("914");
        XY room049 = map.findRoom("room049");
        XY nuke = map.findRoom("room2nuke");

        int start = CommonStartPathFinder.instance.calcRouteLength(map) + map.pathFind(room914, sl);

        // SCENARIO 1: NUKE BEFORE ENTRANCE ZONE
        int routeA = map.pathFind(sl, room008, room049, room106, nuke, cont);
        int routeB = map.pathFind(sl, room049, room008, room106, nuke, cont);
        int routeC = map.pathFind(sl, room049, room106, room008, nuke, cont);
        int routeD = map.pathFind(sl, room049, room106, nuke, room008, cont);

        int fullRouteA = start + Math.min(Math.min(routeA, routeB), Math.min(routeC, routeD)) +
                map.pathFind(cont, room079) * 2 +
                map.pathFind(cont, gateB);

        // SCENARIO 2: NUKE AFTER ENTRANCE ZONE
        int routeZ = map.pathFind(sl, room008, room049, room106, cont);
        int routeY = map.pathFind(sl, room049, room008, room106, cont);
        int routeX = map.pathFind(sl, room049, room106, room008, cont);

        int fullRouteB = start + Math.min(Math.min(routeZ, routeY), routeX) +
                map.pathFind(cont, nuke, room079, cont, gateB);

        return Math.min(fullRouteA, fullRouteB);
    }

    @Override
    public String getName() {
        return "Set Seed Intended B2";
    }
}
