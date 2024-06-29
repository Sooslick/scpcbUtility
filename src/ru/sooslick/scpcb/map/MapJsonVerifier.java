package ru.sooslick.scpcb.map;

import ru.sooslick.scpcb.PathFinder;
import ru.sooslick.scpcb.SeedGenerator;

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
        while (json[i++] != '[') {
        }

        // parsing "rooms" objects
        while (json[i] != ']') {
            i = readRoom(json, i);
        }

        AtomicInteger failures = new AtomicInteger();
        PathFinder pf = SeedGenerator.scpcbCreateSeed(seed);
        expectedRooms.forEach(expectedRoom -> {
            ScpcbRoom actualRoom = pf.map[expectedRoom.x][expectedRoom.y];
            if (actualRoom == null) {
                failures.getAndIncrement();
                System.out.printf("Expected room at %d:%d%n", expectedRoom.x, expectedRoom.y);
            } else if (!actualRoom.roomTemplate.name.equals(expectedRoom.name)) {
                failures.getAndIncrement();
                System.out.printf("Expected %s at %d:%d but got %s%n", expectedRoom.name, expectedRoom.x, expectedRoom.y, actualRoom.roomTemplate.name);
            } else if (actualRoom.angle % 360 != expectedRoom.angle % 360) {
                failures.getAndIncrement();
                System.out.printf("Wrong room rotation at %d:%d (%s)%n", expectedRoom.x, expectedRoom.y, expectedRoom.name);
            }
        });
        if (failures.get() == 0) {
            System.out.println("\u001B[32mTests passed for seed '" + seed + "'\u001B[37m");
            return true;
        } else {
            System.out.println("\u001B[31mDetected " + failures + " map errors for seed '" + seed + "'\u001B[37m");
            return false;
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


    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("custom test launch");
            new MapJsonVerifier(args[0], args[1]).test();
            return;
        }

        new MapJsonVerifier("tests/dollar.json", "$").test();
        new MapJsonVerifier("tests/whitespace.json", " ").test();
        new MapJsonVerifier("tests/6.json", "6").test();
        new MapJsonVerifier("tests/K.json", "K").test();
        new MapJsonVerifier("tests/446456054.json", "446456054").test();
        new MapJsonVerifier("tests/990066099.json", "990066099").test();
        new MapJsonVerifier("tests/bmu23i0.json", "bmu23i0").test();
        new MapJsonVerifier("tests/x9mc.json", "x9mc").test();
        new MapJsonVerifier("tests/2001011999.json", "2001011999").test();
        new MapJsonVerifier("tests/557110973.json", "557110973").test();
    }
}
