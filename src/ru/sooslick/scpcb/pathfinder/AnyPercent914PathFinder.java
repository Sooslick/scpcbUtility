package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;

public class AnyPercent914PathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        XY cont = map.findRoom("room2ccont");
        XY room079 = map.findRoom("room079");

        if (cont == null || room079 == null)
            return 9999;

        XY gateB = map.findRoom("exit1");
        XY closets = map.findRoom("room2closets");
        XY clock = map.findRoom("914");
        XY start = map.findRoom("start");

        // Experimental route must meet following rules (otherwise classic route will be faster)
        // We want gasmask asap
        if (map.pathFind(start, closets) > 2)
            return 9999;

        // We want 914 nearby
        if (Math.abs(closets.x - clock.x) > 1)
            return 9999;

        // We also want 914 closer to hcz
        if (closets.y - clock.y == 0)
            return 9999;

        // 079 should be near room2ccont
        if (Math.sqrt(Math.pow(room079.x - cont.x, 2) + Math.pow(room079.y - cont.y, 2)) > 4)
            return 9999;

        return (int) Math.ceil((start.getRelative(1, 0).distance(closets) + closets.distance(clock)) * 2 +
                clock.distance(cont) +
                cont.distance(room079) * 2 +
                cont.distance(gateB));
    }

    @Override
    public String getName() {
        return "Any% (Experimental)";
    }
}
