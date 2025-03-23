package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class SSLegacyPathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        if (cont == null)
            return 9999;

        XY room008 = map.findRoom("008");
        XY room079 = map.findRoom("room079");
        XY gateA = map.findRoom("gateaentrance");
        XY gateB = map.findRoom("exit1");
        XY sl = map.findRoom("room2sl");
        XY room914 = map.findRoom("914");

        int startLength = CommonStartPathFinder.instance.calcRouteLength(map) +
                map.pathFind(room914, sl);
        int slToContLength = room008 == null ?
                map.pathFind(sl, cont) :
                map.pathFind(sl, room008, cont);
        return startLength * 2 + slToContLength +
                map.pathFind(cont, room079) * 2 +
                Math.min(map.pathFind(cont, gateA), map.pathFind(cont, gateB));
    }

    @Override
    public String getName() {
        return "Set Seed Inbounds A1+B1 (Legacy route)";
    }
}
