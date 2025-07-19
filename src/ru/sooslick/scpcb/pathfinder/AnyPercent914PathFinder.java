package ru.sooslick.scpcb.pathfinder;

import ru.sooslick.scpcb.MapExplorer;
import ru.sooslick.scpcb.map.ScpcbRoom;

public class AnyPercent914PathFinder implements PathFinder {
    @Override
    public int calcRouteLength(MapExplorer map) {
        ScpcbRoom contRoom = map.getRoom("room2ccont");
        ScpcbRoom compRoom = map.getRoom("room079");

        if (contRoom == null || compRoom == null)
            return 9999;

        XY cont = XY.of(contRoom);
        XY room079 = XY.of(compRoom);

        XY gateB = map.findRoom("exit1");
        XY closets = map.findRoom("room2closets");
        XY clock = map.findRoom("914");
        XY start = map.findRoom("start");

        // 079 should be on the ~same column with electrical
        if (Math.abs(room079.x - cont.x) > 1)
            return 9999;
        // Expect specific rotation for quick cont/079 travel
        if (compRoom.angle != 270)
            return 9999;
        if (contRoom.angle != 270)
            return 9999;

        return (int) Math.ceil((start.getRelative(1, 0).distance(closets) + closets.distance(clock)) * 2 +
                clock.distance(cont) +
                cont.distance(room079) * 2 +
                cont.distance(gateB));
    }

    @Override
    public String getName() {
        return "Any%";
    }
}
