package ru.sooslick.scpcb;

import ru.sooslick.scpcb.map.Map;
import ru.sooslick.scpcb.map.ScpcbRoom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapJsonVerifier {
    private static final Pattern ROOM_PATTERN = Pattern.compile("\"name\":\"([a-z0-9_]*)\"");
    private static final Pattern X_PATTERN = Pattern.compile("\"x\":([0-9]*)");
    private static final Pattern Y_PATTERN = Pattern.compile("\"y\":([0-9]*)");
    private static final Pattern ANGLE_PATTERN = Pattern.compile("\"angle\":([0-9]*)");

    private final String pathJson;
    private final int seed;
    private final LinkedList<RoomMeta> expectedRooms = new LinkedList<>();

    public MapJsonVerifier(String pathJson, String seed) {
        this.pathJson = pathJson;
        this.seed = SeedGenerator.generateSeedNumber(seed);
    }

    public MapJsonVerifier(String pathJson, int seed) {
        this.pathJson = pathJson;
        this.seed = seed;
    }

    public void test() throws IOException {
        String testpath = pathJson.startsWith("/") ? pathJson : "/" + pathJson;
        InputStream is = this.getClass().getResourceAsStream(testpath);
        if (is == null)
            throw new FileNotFoundException("Resource " + testpath + " is not exist");
        byte[] json = is.readAllBytes();

        // search "rooms" array
        int i = 19;
        //noinspection StatementWithEmptyBody
        while (json[i++] != '[') {
        }

        // parsing "rooms" objects
        while (json[i] != ']') {
            i = readRoom(json, i);
        }

        int failures = 0;
        Map map = SeedGenerator.generateMap(seed);
        ScpcbRoom[][] grid = new ScpcbRoom[Map.MAP_WIDTH][Map.MAP_HEIGHT];
        for (ScpcbRoom r : map.savedRooms) {
            int x = (int) (r.x / 8);
            int y = (int) (r.z / 8);
            grid[x][y] = r;
        }

        for (RoomMeta expectedRoom : expectedRooms) {
            ScpcbRoom actualRoom = grid[expectedRoom.x][expectedRoom.y];
            if (actualRoom == null) {
                failures++;
                System.out.printf("Expected room at %d:%d%n", expectedRoom.x, expectedRoom.y);
            } else if (!actualRoom.roomTemplate.name.equals(expectedRoom.name)) {
                failures++;
                System.out.printf("Expected %s at %d:%d but got %s%n", expectedRoom.name, expectedRoom.x, expectedRoom.y, actualRoom.roomTemplate.name);
            } else if (actualRoom.angle % 360 != expectedRoom.angle % 360) {
                failures++;
                System.out.printf("Wrong room rotation at %d:%d (%s)%n", expectedRoom.x, expectedRoom.y, expectedRoom.name);
            }
            // todo add test: doors, events, map constants
        }
        if (failures == 0) {
            System.out.println("\u001B[32mTests passed for seed '" + seed + "'\u001B[37m");
        } else {
            System.out.println("\u001B[31mDetected " + failures + " map errors for seed '" + seed + "'\u001B[37m");
        }
    }

    private int readRoom(byte[] json, int from) {
        int i = from;
        //noinspection StatementWithEmptyBody
        while (json[i++] != '{') {
        }
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
        throw new RuntimeException("testRegex failure");
    }

    private record RoomMeta(String name, int x, int y, int angle) {}

    /////////////////////////

    // todo pipeline mvn test
    public static void main(String[] args) throws IOException {
        new MapJsonVerifier("dollar.json", "$").test();                 // various input characters
        new MapJsonVerifier("whitespace.json", " ").test();
        new MapJsonVerifier("6.json", "6").test();
        new MapJsonVerifier("K.json", "K").test();
        new MapJsonVerifier("446456054.json", "446456054").test();      // various well-known seeds
        new MapJsonVerifier("990066099.json", "990066099").test();
        new MapJsonVerifier("bmu23i0.json", "bmu23i0").test();
        new MapJsonVerifier("x9mc.json", "x9mc").test();
        new MapJsonVerifier("2001011999.json", "2001011999").test();
        new MapJsonVerifier("557110973.json", "557110973").test();
        new MapJsonVerifier("n790.json", "n790").test();                // room2c merge
        new MapJsonVerifier("220.json", "\\@").test();                  // room rotation above 360
        new MapJsonVerifier("558272428.json", 558272428).test();        // PD Exit tunnel swapped
        new MapJsonVerifier("1480285.json", 1480285).test();            // no HCZ room1s
        new MapJsonVerifier("1227883421.json", 1227883421).test();
        new MapJsonVerifier("5740247.json", 5740247).test();            // 079 entrance
        new MapJsonVerifier("263284380.json", 263284380).test();        // 914 hcz
    }
}
