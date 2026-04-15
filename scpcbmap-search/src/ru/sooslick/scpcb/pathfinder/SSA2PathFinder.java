package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class SSA2PathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        XY gateA = map.findRoom("gateaentrance");
        XY room106 = map.findRoom("room106");

        if (cont == null || gateA == null || room106 == null)
            return 9999;

        XY room008 = map.findRoom("008");
        XY room079 = map.findRoom("room079");

        int startLength = CommonStartPathFinder.instance.calcRouteLength(map);
        int hczLength = calcBestHcz(map, room106, room008, cont);

        return startLength + 1 + hczLength +
                map.pathFind(cont, room079, room106, cont, gateA);
    }

    @Override
    public String getName() {
        return "Set Seed Inbounds A2";
    }

    private int calcBestHcz(MapExplorer map, XY room106, XY room008, XY cont) {
        XY tunnel = map.findPDExit();

        int route106 = calcHcz(map, room106, room106, room008, cont);
        int routeTunnel = calcHcz(map, tunnel, room106, room008, cont);
        return Math.min(route106, routeTunnel);
    }

    private int calcHcz(MapExplorer map, XY startPoint, XY room106, XY room008, XY cont) {
        if (startPoint == null)
            return 9999;
        if (room008 == null) {
            if (startPoint.equals(room106))
                return map.pathFind(room106, cont);
            else
                return map.pathFind(startPoint, room106, cont);
        } else {
            if (startPoint.equals(room106))
                return map.pathFind(room106, room008, cont);
            else
                return Math.min(
                        map.pathFind(startPoint, room106, room008, cont),
                        map.pathFind(startPoint, room008, room106, cont)
                );
        }
    }
}
