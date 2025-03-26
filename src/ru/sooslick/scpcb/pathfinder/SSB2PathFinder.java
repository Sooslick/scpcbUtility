package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;
import ru.sooslick.scpcb.map.ScpcbRoom;

public class SSB2PathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        XY gateB = map.findRoom("exit1");
        XY nuke = map.findRoom("room2nuke");

        if (cont == null)
            return 9999;

        XY room008 = map.findRoom("008");
        XY room079 = map.findRoom("room079");

        int startLength = CommonStartPathFinder.instance.calcRouteLength(map);

        XY room106 = map.findRoom("room106");
        XY shaft = map.findRoom("shaft");
        XY tunnel = scanTunnel(map);

        int route106 = 2 + calcRoute(map, room106, nuke, room008, cont, room079);
        int routeShaft = 2 + calcRoute(map, shaft, nuke, room008, cont, room079);
        int routeTunnel = 1 + calcRoute(map, tunnel, nuke, room008, cont, room079);

        return startLength * 2 + Math.min(Math.min(route106, routeShaft), routeTunnel) + map.pathFind(cont, gateB);
    }

    @Override
    public String getName() {
        return "Sed Seed Inbounds B2";
    }

    private int calcRoute(MapExplorer map, XY startPoint, XY nuke, XY room008, XY cont, XY room079) {
        if (startPoint == null)
            return 9999;
        if (room008 == null) {
            int routeA = map.pathFind(startPoint, nuke, cont) + map.pathFind(cont, room079) * 2;
            int routeB = map.pathFind(startPoint, cont, nuke, room079, cont);
            return Math.min(routeA, routeB);
        } else {
            int cont079 = map.pathFind(cont, room079) * 2;
            int routeA = map.pathFind(startPoint, room008, nuke, cont) + cont079;
            int routeB = map.pathFind(startPoint, nuke, room008, cont) + cont079;

            int routeC = map.pathFind(startPoint, room008, cont, nuke, room079, cont);
            return Math.min(Math.min(routeA, routeB), routeC);
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
