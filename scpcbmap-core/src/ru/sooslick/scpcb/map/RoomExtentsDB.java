package ru.sooslick.scpcb.map;

import java.util.HashMap;
import java.util.Map;

import static ru.sooslick.scpcb.map.Map.MAP_WIDTH;

public class RoomExtentsDB {

    private static final HashMap<String, RoomExtentsDB> DB;

    private final Map<Integer, Boundaries> boundaries = new HashMap<>();

    static {
        RMeshReader r = new RMeshReader("scpcbFiles/roomdb.dat");
        DB = new HashMap<>();

        while (!r.isEof()) {
            String name = r.readString();
            for (int a = 0; a <= 630; a+= 90) {
                int angle = r.readInt();
                RoomExtentsDB roomDb = new RoomExtentsDB();
                for (int i = 1; i < MAP_WIDTH; i++) {
                    Boundaries b = new Boundaries();
                    b.minX = r.readFloat();
                    b.maxX = r.readFloat();
                    b.minZ = r.readFloat();
                    b.maxZ = r.readFloat();
                    roomDb.boundaries.put(i, b);
                }
                DB.put(name + angle, roomDb);
            }
        }
    }

    public static Boundaries findExtents(String name, int angle, int x, int z) {
        Boundaries b = new Boundaries();
        RoomExtentsDB db = DB.get(name + angle);
        b.minX = db.boundaries.get(x).minX;
        b.maxX = db.boundaries.get(x).maxX;
        b.minZ = db.boundaries.get(z).minZ;
        b.maxZ = db.boundaries.get(z).maxZ;
        return b;
    }

    static class Boundaries {
        float minX, maxX, minZ, maxZ;
    }
}
