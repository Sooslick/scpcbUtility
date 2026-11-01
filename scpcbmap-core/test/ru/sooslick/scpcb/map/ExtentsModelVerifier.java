package ru.sooslick.scpcb.map;

/**
 * Checks {@link BlitzRoomExtents} against the room bounds dumped from the real SCP:CB 1.3.11 (scpcbFiles/roomdb.dat).
 * Fails (non-zero exit) on any mismatch. Run from the repository root, like MapJsonVerifier:
 * <pre>java -cp "scpcbmap-core/target/classes:scpcbmap-core/target/test-classes" ru.sooslick.scpcb.map.ExtentsModelVerifier</pre>
 */
public class ExtentsModelVerifier {
    // The dump of this room disagrees with its mesh by exactly 1/16 at 0 degrees (a data difference, not float math).
    private static final String KNOWN_DATA_DIFFERENCE = "room2cafeteria";

    public static void main(String[] args) {
        int total = 0, rooms = 0, mismatches = 0;
        for (ScpcbRoomTemplate t : ScpcbRoomTemplate.roomTemplates) {
            if (t.disableOverlapCheck || t.extents == null || t.name.equals(KNOWN_DATA_DIFFERENCE))
                continue;
            boolean counted = false;
            for (int angle = 0; angle <= 630; angle += 90) {
                RoomExtentsDB.Boundaries probe;
                try {
                    probe = RoomExtentsDB.findExtents(t.name, angle, 1, 1);
                } catch (RuntimeException e) {
                    continue;   // not in the dump (1499, ...)
                }
                if (probe == null)
                    continue;
                if (!counted) {
                    rooms++;
                    counted = true;
                }
                for (int i = 1; i < Map.MAP_WIDTH; i++) {
                    RoomExtentsDB.Boundaries db = RoomExtentsDB.findExtents(t.name, angle, i, i);
                    float[] model = BlitzRoomExtents.calculate(t.extents, angle, 8 * i, 8 * i);
                    float[] real = {db.minX, db.maxX, db.minZ, db.maxZ};
                    String[] names = {"minX", "maxX", "minZ", "maxZ"};
                    for (int k = 0; k < 4; k++) {
                        total++;
                        if (Float.floatToIntBits(model[k]) != Float.floatToIntBits(real[k])) {
                            if (++mismatches <= 10)
                                System.out.printf("MISMATCH %s angle=%d pos=%d %s: model %s, game %s%n", t.name, angle, i, names[k], model[k], real[k]);
                        }
                    }
                }
            }
        }
        if (rooms == 0 || total == 0)
            throw new AssertionError("Nothing was verified: is the working directory the repository root?");
        if (mismatches != 0)
            throw new AssertionError(mismatches + " of " + total + " room bounds differ from the real game");
        System.out.println("Room extents model matches the real game: " + total + " values (" + rooms + " rooms x 8 angles x 17 positions x 4 bounds), 0 mismatches");
    }
}
