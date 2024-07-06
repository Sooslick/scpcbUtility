package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class AnyPercentPathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY room008 = map.findRoom("008");
        XY room079 = map.findRoom("room079");
        XY cont = map.findRoom("room2ccont");
        XY gateA = map.findRoom("gateaentrance");
        XY gateB = map.findRoom("exit1");
        XY sl = map.findRoom("room2sl");

        if (cont == null)
            return 9999;
        int startLength = new CommonStartPathFinder().calcRouteLength(map);
        int slToContLength = room008 == null ?
                map.pathFind(sl, cont) :
                map.pathFind(sl, room008) + map.pathFind(room008, cont);
        return startLength + slToContLength +
                map.pathFind(cont, room079) * 2 +
                Math.min(map.pathFind(cont, gateA), map.pathFind(cont, gateB));
    }
}
