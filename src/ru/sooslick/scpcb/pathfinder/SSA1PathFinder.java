package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;
import ru.sooslick.scpcb.map.ScpcbRoom;

public class SSA1PathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        XY gateA = map.findRoom("gateaentrance");

        if (cont == null || gateA == null)
            return 9999;

        XY room008 = map.findRoom("008");
        XY room079 = map.findRoom("room079");

        int startLength = CommonStartPathFinder.instance.calcRouteLength(map);
        int hczLength = calcHcz(map, room008, cont);

        return startLength + hczLength +
                map.pathFind(cont, room079) * 2 +
                map.pathFind(cont, gateA);
    }

    private int calcHcz(MapExplorer map, XY room008, XY cont) {
        XY room106 = map.findRoom("room106");
        XY shaft = map.findRoom("shaft");
        XY tunnel = scanTunnel(map);

        if (room008 == null) {
            int route106 = 2 + map.pathFind(room106, cont);
            int routeShaft = 1 + map.pathFind(shaft, cont);
            int routeTunnel = tunnel == null ? 9999 : map.pathFind(tunnel, cont);
            return Math.min(Math.min(route106, routeShaft), routeTunnel);
        } else {
            int route106 = 2 + map.pathFind(room106, room008) + map.pathFind(room008, cont);
            int routeShaft = 1 + map.pathFind(shaft, room008) + map.pathFind(room008, cont);
            int routeTunnel = tunnel == null ? 9999 : map.pathFind(tunnel, room008) + map.pathFind(room008, cont);
            return Math.min(Math.min(route106, routeShaft), routeTunnel);
        }
    }

    private XY scanTunnel(MapExplorer map) {
        int savedY = 0;
        int savedX = 0;

        for (ScpcbRoom room : map.map.savedRooms) {
            if (!"tunnel".equals(room.roomTemplate.name))
                continue;
            int y = (int) (room.z / 8);
            if (y < savedY)
                continue;

            int x = (int) (room.x / 8);
            if (y == savedY && x > savedX)
                continue;

            savedY = y;
            savedX = x;
        }
        if (savedX == 0 && savedY == 0)
            return null;
        return new XY(savedX, savedY);
    }
}
