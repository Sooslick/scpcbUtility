package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;
import ru.sooslick.scpcb.map.ScpcbRoom;

public class SSPathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        XY gateA = map.findRoom("gateaentrance");
        XY gateB = map.findRoom("exit1");

        if (cont == null)
            return 9999;

        XY room008 = map.findRoom("008");
        XY room079 = map.findRoom("room079");

        int startLength = CommonStartPathFinder.instance.calcRouteLength(map);
        int hczLength = calcBestHcz(map, room008, cont);

        return startLength * 2 + hczLength +
                map.pathFind(cont, room079) * 2 +
                Math.min(map.pathFind(cont, gateA), map.pathFind(cont, gateB));
    }

    @Override
    public String getName() {
        return "Set Seed Inbounds A1+B1";
    }

    private int calcBestHcz(MapExplorer map, XY room008, XY cont) {
        XY room106 = map.findRoom("room106");
        XY shaft = map.findRoom("shaft");
        XY tunnel = scanTunnel(map);

        int route106 = 2 + calcHcz(map, room106, room008, cont);
        int routeShaft = 2 + calcHcz(map, shaft, room008, cont);
        int routeTunnel = 1 + calcHcz(map, tunnel, room008, cont);
        return Math.min(Math.min(route106, routeShaft), routeTunnel);
    }

    private int calcHcz(MapExplorer map, XY startPoint, XY room008, XY cont) {
        if (startPoint == null)
            return 9999;
        if (room008 == null) {
            return map.pathFind(startPoint, cont);
        } else {
            return map.pathFind(startPoint, room008, cont);
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
