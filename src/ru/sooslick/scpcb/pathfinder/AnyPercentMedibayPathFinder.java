package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class AnyPercentMedibayPathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        XY room079 = map.findRoom("room079");
        XY medibay = map.findRoom("medibay");

        if (cont == null || room079 == null || medibay == null)
            return 9999;

        XY gateB = map.findRoom("exit1");
        XY start = map.findRoom("start");

        return (int) Math.ceil(start.getRelative(1, 0).distance(cont) +
                cont.distance(room079) * 2 +
                cont.distance(medibay) +
                medibay.distance(gateB));
    }

    @Override
    public String getName() {
        return "Any% (Old route)";
    }
}
