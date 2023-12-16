package ru.sooslick.scpcb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapJsonVerifier {
    private static final Pattern ROOM_PATTERN = Pattern.compile("\"name\":\"([a-z0-9_]*)\"");
    private static final Pattern X_PATTERN = Pattern.compile("\"x\":([0-9]*)");
    private static final Pattern Y_PATTERN = Pattern.compile("\"y\":([0-9]*)");
    private static final Pattern ANGLE_PATTERN = Pattern.compile("\"angle\":([0-9]*)");

    private final String pathJson;
    private final String seed;
    private final LinkedList<RoomMeta> expectedRooms = new LinkedList<>();

    public MapJsonVerifier(String pathJson, String seed) {
        this.pathJson = pathJson;
        this.seed = seed;
    }

    public boolean test() throws IOException {
        byte[] json = Files.readAllBytes(Paths.get(pathJson));

        // search "rooms" array
        int i = 19;
        //noinspection StatementWithEmptyBody
        while (json[i++] != '[') {}

        // parsing "rooms" objects
        while (json[i] != ']') {
            i = readRoom(json, i);
        }

        AtomicInteger failures = new AtomicInteger();
        PathFinder pf = SeedGenerator.scpcbCreateSeed(seed);
        expectedRooms.forEach(rm -> {
            ScpcbRoom r = pf.map[rm.x][rm.y];
            if (r == null) {
                failures.getAndIncrement();
                System.out.println("Expected room at " + rm.x + ":" + rm.y);
            } else if (!r.roomTemplate.name.equals(rm.name)) {
                failures.getAndIncrement();
                System.out.println("Expected at " + rm.x + ":" + rm.y + " room " + rm.name + ", actual " + r.roomTemplate.name);
            } else if (r.angle != rm.angle) {
                //failures.getAndIncrement();   // just report at this point // todo fix later
                System.out.println("Wrong room rotation at " + rm.x + ":" + rm.y);
            }
        });
        System.out.println("Detected " + failures + " map errors for map " + seed);
        return failures.get() == 0;
    }

    private int readRoom(byte[] json, int from) {
        int i = from;
        //noinspection StatementWithEmptyBody
        while (json[i++] != '{') {}
        int bracketsAmount = 1;
        while (bracketsAmount > 0) {
            if (json[i] == '{')
                bracketsAmount++;
            else if (json[i] == '}')
                bracketsAmount--;
            i++;
        }
        String roomObject = new String(Arrays.copyOfRange(json, from, i));
        Matcher m = testRegex(ROOM_PATTERN, roomObject);
        String name = m.group(1);
        m = testRegex(X_PATTERN, roomObject);
        int x = Integer.parseInt(m.group(1));
        m = testRegex(Y_PATTERN, roomObject);
        int y = Integer.parseInt(m.group(1));
        m = testRegex(ANGLE_PATTERN, roomObject);
        int angle = Integer.parseInt(m.group(1));
        expectedRooms.add(new RoomMeta(name, x, y, angle));
        return i;
    }

    private Matcher testRegex(Pattern pattern, String string) {
        Matcher m = pattern.matcher(string);
        if (m.find())
            return m;
        System.out.println("Regex test failure. String: " + string);
        throw new RuntimeException("testRRegex failure");
    }

    private static class RoomMeta {
        private final String name;
        private final int x;
        private final int y;
        private final int angle;

        public RoomMeta(String name, int x, int y, int angle) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
    }
}
