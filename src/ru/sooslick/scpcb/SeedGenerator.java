package ru.sooslick.scpcb;

import java.util.LinkedHashSet;
import java.util.Set;

import static ru.sooslick.scpcb.BlitzFloatMath.maxXLesserOrEquals;
import static ru.sooslick.scpcb.BlitzFloatMath.maxZLesserOrEquals;
import static ru.sooslick.scpcb.BlitzFloatMath.minXBiggerOrEquals;
import static ru.sooslick.scpcb.BlitzFloatMath.minZBiggerOrEquals;
import static ru.sooslick.scpcb.BlitzRandom.bbRand;
import static ru.sooslick.scpcb.BlitzRandom.bbRnd;
import static ru.sooslick.scpcb.BlitzRandom.bbSeedRnd;

public class SeedGenerator {
    public static final int ROOM1 = 1;
    public static final int ROOM2 = 2;
    public static final int ROOM2C = 3;
    public static final int ROOM3 = 4;
    public static final int ROOM4 = 5;

    private static final int MAP_WIDTH = 18;
    private static final int MAP_HEIGHT = 18;
    private static final int ZONE_AMOUNT = 3;

    private static String[][] mapRoom;

    private static Set<ScpcbRoom> savedRooms;

    public static void main(String[] args) {
        // found good seeds for further examination
//        String randomSeed = "qu"; // - Any% good seed
//        String randomSeed = "QZI"; // - Any% good seed
//        String randomSeed = "7em"; // - super good start

        // seed printer block
        PathFinder pf = scpcbCreateSeed("6");
        pf.printMaze();
        pf.drawMap();
//        pf.exportJson();
//        pf.testRouteLength(PathFinder.NO_SCP914_FINDER);

        // seed bruteforcer block
//        int routeLengthThreshold = 21;
//        int[] savedState = {40, 0, 91};
//        int savedLength = 3;
//        BruteForce bf = new BruteForce(BruteForce.ASCII_HALF, 2, 15, savedLength, savedState);
//        while (!bf.isFinished()) {
//            PathFinder pf = scpcbCreateSeed(new String(bf.next()));     // severe memory leak
//            int routeLength = pf.testRouteLength(PathFinder.ANY_PERCENT_ENDGAME);
//            if (routeLength < routeLengthThreshold) {
//                pf.printMaze();
//                bf.printState();
//                break;
//            }
//        }
    }

    public static PathFinder scpcbCreateSeed(String randomSeed) {

        // consts declared outside CreateMap function
        int[][] mapTemp = new int[MAP_WIDTH + 1][MAP_HEIGHT + 1];
        boolean[][] mapFound = new boolean[MAP_WIDTH + 1][MAP_HEIGHT + 1];

        // recreation of original CreateMap function
        System.out.println("Generating a map using the seed " + randomSeed);

        int iZoneTransition0 = 13;
        int iZoneTransition1 = 7;
        boolean iZoneHasCustomMT = false;

        int x, y, temp = 0;     // unknown initial values
        int i, x2 = 0, y2 = 0;
        int width, height;

        int zone = 0;

        bbSeedRnd(SeedTester.generateSeedNumber(randomSeed.toCharArray()));

        String[][] mapName = new String[MAP_WIDTH][MAP_HEIGHT];

        int[] mapRoomID = new int[ROOM4 + 1];

        x = MAP_WIDTH / 2;
        y = MAP_HEIGHT - 2;
        for (i = y; i <= MAP_HEIGHT - 1; i++) {
            mapTemp[x][i] = 1;
        }

        do {
            width = bbRand(10, 15);

            if (x > MAP_WIDTH * 0.6)
                width = -width;
            else if (x > MAP_WIDTH * 0.4)
                x = x - width / 2;

            // make sure the hallway doesn't go outside the array
            if (x + width > MAP_WIDTH - 3)
                width = MAP_WIDTH - 3 - x;
            else if (x + width < 2)
                width = -x + 2;

            x = Math.min(x, x + width);
            width = Math.abs(width);
            for (i = x; i <= x + width; i++)
                mapTemp[i][y] = 1;

            height = bbRand(3, 4);
            if (y - height < 1)
                height = y - 1;

            int yHallways = bbRand(4, 5);

            if (getZone(y - height) != getZone(y - height + 1))
                height--;

            for (i = 1; i <= yHallways; i++) {

                x2 = Math.max(2, Math.min(MAP_WIDTH - 2, bbRand(x, x + width - 1)));
                while (mapTemp[x2 - 1][y - 1] != 0 || mapTemp[x2][y - 1] != 0 || mapTemp[x2 + 1][y - 1] != 0)
                    x2++;

                if (x2 < x + width) {
                    int tempHeight;
                    if (i == 1) {
                        tempHeight = height;
                        if (bbRand(1, 2) == 1)
                            x2 = x;
                        else
                            x2 = x + width;
                    } else
                        tempHeight = bbRand(1, height);

                    for (y2 = y - tempHeight; y2 <= y; y2++) {
                        if (getZone(y2) != getZone(y2 + 1))
                            mapTemp[x2][y2] = 255;    //a room leading from zone to another
                        else
                            mapTemp[x2][y2] = 1;
                    }

                    if (tempHeight == height)
                        temp = x2;
                }
            }

            x = temp;
            y = y - height;
        } while (y >= 2);

        int zoneAmount = 3;     // another one?
        int[] room1Amount = new int[3];
        int[] room2Amount = new int[3];
        int[] room2cAmount = new int[3];
        int[] room3Amount = new int[3];
        int[] room4Amount = new int[3];

        // count the amount of rooms
        for (y = 1; y <= MAP_HEIGHT; y++) {
            zone = getZone(y);

            for (x = 1; x <= MAP_WIDTH - 1; x++) {
                if (mapTemp[x][y] > 0) {
                    temp = getConnections(mapTemp, x, y);
                    if (mapTemp[x][y] < 255)
                        mapTemp[x][y] = temp;
                    switch (mapTemp[x][y]) {
                        case 1:
                            room1Amount[zone]++;
                            break;
                        case 2:
                            if (getHorizontalConnections(mapTemp, x, y) == 2)
                                room2Amount[zone]++;
                            else if (getVerticalConnections(mapTemp, x, y) == 2)
                                room2Amount[zone]++;
                            else
                                room2cAmount[zone]++;
                            break;
                        case 3:
                            room3Amount[zone]++;
                            break;
                        case 4:
                            room4Amount[zone]++;
                            break;
                    }
                }
            }
        }

        // force more room1s (if needed)
        for (i = 0; i <= 2; i++) {
            // need more rooms if there are less than 5 of them
            temp = 5 - room1Amount[i];

            if (temp > 0) {

                for (y = (MAP_HEIGHT / zoneAmount) * (2 - i) + 1; y <= ((MAP_HEIGHT / zoneAmount) * ((2 - i) + 1)) - 2; y++) {

                    for (x = 2; x <= MAP_WIDTH - 2; x++) {
                        if (mapTemp[x][y] == 0) {

                            if (getConnections(mapTemp, x, y) == 1) {

                                if (mapTemp[x + 1][y] != 0) {
                                    x2 = x + 1;
                                    y2 = y;
                                } else if (mapTemp[x - 1][y] != 0) {
                                    x2 = x - 1;
                                    y2 = y;
                                } else if (mapTemp[x][y + 1] != 0) {
                                    x2 = x;
                                    y2 = y + 1;
                                } else if (mapTemp[x][y - 1] != 0) {
                                    x2 = x;
                                    y2 = y - 1;
                                }

                                boolean placed = false;
                                if (mapTemp[x2][y2] > 1 && mapTemp[x2][y2] < 4) {
                                    switch (mapTemp[x2][y2]) {
                                        case 2:
                                            if (getHorizontalConnections(mapTemp, x2, y2) == 2) {
                                                room2Amount[i]--;
                                                room3Amount[i]++;
                                                placed = true;
                                            } else if (getVerticalConnections(mapTemp, x2, y2) == 2) {
                                                room2Amount[i]--;
                                                room3Amount[i]++;
                                                placed = true;
                                            }
                                            break;
                                        case 3:
                                            room3Amount[i]--;
                                            room4Amount[i]++;
                                            placed = true;
                                    }

                                    if (placed) {
                                        mapTemp[x2][y2]++;

                                        mapTemp[x][y] = 1;
                                        room1Amount[i]++;

                                        temp--;
                                    }
                                }
                            }

                        }
                        if (temp == 0)
                            break;
                    }
                    if (temp == 0)
                        break;
                }
            }
        }

        // force more room4s and room2Cs
        int temp2 = 0;
        for (i = 0; i <= 2; i++) {

            switch (i) {
                case 2:
                    zone = 2;
                    temp2 = MAP_HEIGHT / 3;
                    break;
                case 1:
                    zone = MAP_HEIGHT / 3 + 1;
                    temp2 = (int) (MAP_HEIGHT * (2.0 / 3.0) - 1);
                    break;
                case 0:
                    zone = (int) (MAP_HEIGHT * (2.0 / 3.0) + 1);
                    temp2 = MAP_HEIGHT - 2;
            }

            if (room4Amount[i] < 1) {   // we want at least 1 ROOM4
                System.out.println("forcing a ROOM4 into zone " + i);
                temp = 0;

                for (y = zone; y <= temp2; y++) {
                    for (x = 2; x <= MAP_WIDTH - 2; x++) {
                        if (mapTemp[x][y] == 3) {   // see if adding a ROOM1 is possible
                            if (mapTemp[x + 1][y] == 0 && mapTemp[x + 1][y + 1] == 0 && mapTemp[x + 1][y - 1] == 0 && mapTemp[x + 2][y] == 0) {
                                mapTemp[x + 1][y] = 1;
                                temp = 1;
                            } else if (mapTemp[x - 1][y] == 0 && mapTemp[x - 1][y + 1] == 0 && mapTemp[x - 1][y - 1] == 0 && mapTemp[x - 2][y] == 0) {
                                mapTemp[x - 1][y] = 1;
                                temp = 1;
                            } else if (mapTemp[x][y + 1] == 0 && mapTemp[x + 1][y + 1] == 0 && mapTemp[x - 1][y + 1] == 0 && mapTemp[x][y + 2] == 0) {
                                mapTemp[x][y + 1] = 1;
                                temp = 1;
                            } else if (mapTemp[x][y - 1] == 0 && mapTemp[x + 1][y - 1] == 0 && mapTemp[x - 1][y - 1] == 0 && mapTemp[x][y - 2] == 0) {
                                mapTemp[x][y - 1] = 1;
                                temp = 1;
                            }
                            if (temp == 1) {
                                mapTemp[x][y] = 4; // turn this room into a ROOM4
                                System.out.println("ROOM4 forced into slot (" + x + ", " + y + ")");
                                room4Amount[i]++;
                                room3Amount[i]--;
                                room1Amount[i]++;
                            }
                        }
                        if (temp == 1)
                            break;
                    }
                    if (temp == 1)
                        break;
                }

                if (temp == 0)
                    System.out.println("Couldn't place ROOM4 in zone " + i);
            }

            if (room2cAmount[i] < 1) {  // we want at least 1 ROOM2C
                System.out.println("forcing a ROOM2C into zone " + i);
                temp = 0;

                zone++;
                temp2--;

                for (y = zone; y <= temp2; y++) {
                    for (x = 3; x <= MAP_WIDTH - 3; x++) {
                        if (mapTemp[x][y] == 1) {   // see if adding some rooms is possible
                            if (mapTemp[x - 1][y] > 0) {
                                if (mapTemp[x][y - 1] + mapTemp[x][y + 1] + mapTemp[x + 2][y] == 0) {
                                    if (mapTemp[x + 1][y - 2] + mapTemp[x + 2][y - 1] + mapTemp[x + 1][y - 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x + 1][y] = 2;
                                        System.out.println("ROOM2C forced into slot (" + (x + 1) + ", " + (y) + ")");
                                        mapTemp[x + 1][y - 1] = 1;
                                        temp = 1;
                                    } else if (mapTemp[x + 1][y + 2] + mapTemp[x + 2][y + 1] + mapTemp[x + 1][y + 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x + 1][y] = 2;
                                        System.out.println("ROOM2C forced into slot (" + (x + 1) + ", " + (y) + ")");
                                        mapTemp[x + 1][y + 1] = 1;
                                        temp = 1;
                                    }
                                }
                            } else if (mapTemp[x + 1][y] > 0) {
                                if (mapTemp[x][y - 1] + mapTemp[x][y + 1] + mapTemp[x - 2][y] == 0) {
                                    if (mapTemp[x - 1][y - 2] + mapTemp[x - 2][y - 1] + mapTemp[x - 1][y - 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x - 1][y] = 2;
                                        System.out.println("ROOM2C forced into slot (" + (x - 1) + ", " + (y) + ")");
                                        mapTemp[x - 1][y - 1] = 1;
                                        temp = 1;
                                    } else if (mapTemp[x - 1][y + 2] + mapTemp[x - 2][y + 1] + mapTemp[x - 1][y + 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x - 1][y] = 2;
                                        System.out.println("ROOM2C forced into slot (" + (x - 1) + ", " + (y) + ")");
                                        mapTemp[x - 1][y + 1] = 1;
                                        temp = 1;
                                    }
                                }
                            } else if (mapTemp[x][y - 1] > 0) {
                                if (mapTemp[x - 1][y] + mapTemp[x + 1][y] + mapTemp[x][y + 2] == 0) {
                                    if (mapTemp[x - 2][y + 1] + mapTemp[x - 1][y + 2] + mapTemp[x - 1][y + 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x][y + 1] = 2;
                                        System.out.println("ROOM2C forced into slot (" + x + ", " + (y + 1) + ")");
                                        mapTemp[x - 1][y + 1] = 1;
                                        temp = 1;
                                    } else if (mapTemp[x + 2][y + 1] + mapTemp[x + 1][y + 2] + mapTemp[x + 1][y + 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x][y + 1] = 2;
                                        System.out.println("ROOM2C forced into slot (" + (x) + ", " + (y + 1) + ")");
                                        mapTemp[x + 1][y + 1] = 1;
                                        temp = 1;
                                    }
                                }
                            } else if (mapTemp[x][y + 1] > 0) {
                                if (mapTemp[x - 1][y] + mapTemp[x + 1][y] + mapTemp[x][y - 2] == 0) {
                                    if (mapTemp[x - 2][y - 1] + mapTemp[x - 1][y - 2] + mapTemp[x - 1][y - 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x][y - 1] = 2;
                                        System.out.println("ROOM2C forced into slot (" + x + ", " + (y - 1) + ")");
                                        mapTemp[x - 1][y - 1] = 1;
                                        temp = 1;
                                    } else if (mapTemp[x + 2][y - 1] + mapTemp[x + 1][y - 2] + mapTemp[x + 1][y - 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x][y - 1] = 2;
                                        System.out.println("ROOM2C forced into slot (" + (x) + ", " + (y - 1) + ")");
                                        mapTemp[x + 1][y - 1] = 1;
                                        temp = 1;
                                    }
                                }
                            }
                            if (temp == 1) {
                                room2cAmount[i]++;
                                room2Amount[i]++;
                            }
                        }
                        if (temp == 1)
                            break;
                    }
                    if (temp == 1)
                        break;
                }

                if (temp == 0)
                    System.out.println("Couldn't place ROOM2C in zone " + i);
            }
        }

        int maxRooms = 55 * MAP_WIDTH / 20;
        maxRooms = Math.max(maxRooms, room1Amount[0] + room1Amount[1] + room1Amount[2]);
        maxRooms = Math.max(maxRooms, room2Amount[0] + room2Amount[1] + room2Amount[2]);
        maxRooms = Math.max(maxRooms, room2cAmount[0] + room2cAmount[1] + room2cAmount[2]);
        maxRooms = Math.max(maxRooms, room3Amount[0] + room3Amount[1] + room3Amount[2]);
        maxRooms = Math.max(maxRooms, room4Amount[0] + room4Amount[1] + room4Amount[2]);
        mapRoom = new String[ROOM4 + 1][maxRooms];

        // zone 1

        int minPos = 1;
        int maxPos = room1Amount[0] - 1;

        mapRoom[ROOM1][0] = "start";
        setRoom("roompj", ROOM1, (int) (Math.floor(0.1 * room1Amount[0])), minPos, maxPos);
        setRoom("914", ROOM1, (int) (Math.floor(0.3 * room1Amount[0])), minPos, maxPos);
        setRoom("room1archive", ROOM1, (int) (Math.floor(0.5 * room1Amount[0])), minPos, maxPos);
        setRoom("room205", ROOM1, (int) (Math.floor(0.6 * room1Amount[0])), minPos, maxPos);

        mapRoom[ROOM2C][0] = "lockroom";

        minPos = 1;
        maxPos = room2Amount[0] - 1;

        mapRoom[ROOM2][0] = "room2closets";
        setRoom("room2testroom2", ROOM2, (int) (Math.floor(0.1 * room2Amount[0])), minPos, maxPos);
        setRoom("room2scps", ROOM2, (int) (Math.floor(0.2 * room2Amount[0])), minPos, maxPos);
        setRoom("room2storage", ROOM2, (int) (Math.floor(0.3 * room2Amount[0])), minPos, maxPos);
        setRoom("room2gw_b", ROOM2, (int) (Math.floor(0.4 * room2Amount[0])), minPos, maxPos);
        setRoom("room2sl", ROOM2, (int) (Math.floor(0.5 * room2Amount[0])), minPos, maxPos);
        setRoom("room012", ROOM2, (int) (Math.floor(0.55 * room2Amount[0])), minPos, maxPos);
        setRoom("room2scps2", ROOM2, (int) (Math.floor(0.6 * room2Amount[0])), minPos, maxPos);
        setRoom("room1123", ROOM2, (int) (Math.floor(0.7 * room2Amount[0])), minPos, maxPos);
        setRoom("room2elevator", ROOM2, (int) (Math.floor(0.85 * room2Amount[0])), minPos, maxPos);

        mapRoom[ROOM3][(int) Math.floor(bbRnd(0.2f, 0.8f) * room3Amount[0])] = "room3storage";

        mapRoom[ROOM2C][(int) Math.floor(0.5 * room2cAmount[0])] = "room1162";

        mapRoom[ROOM4][(int) Math.floor(0.3 * room4Amount[0])] = "room4info";

        // zone 2

        minPos = room1Amount[0];
        maxPos = minPos + room1Amount[1] - 1;

        setRoom("room079", ROOM1, room1Amount[0] + (int) (Math.floor(0.15 * room1Amount[1])), minPos, maxPos);
        setRoom("room106", ROOM1, room1Amount[0] + (int) (Math.floor(0.3 * room1Amount[1])), minPos, maxPos);
        setRoom("008", ROOM1, room1Amount[0] + (int) (Math.floor(0.4 * room1Amount[1])), minPos, maxPos);
        setRoom("room035", ROOM1, room1Amount[0] + (int) (Math.floor(0.5 * room1Amount[1])), minPos, maxPos);
        setRoom("coffin", ROOM1, room1Amount[0] + (int) (Math.floor(0.7 * room1Amount[1])), minPos, maxPos);

        minPos = room2Amount[0];
        maxPos = minPos + room2Amount[1] - 1;

        mapRoom[ROOM2][minPos + (int) (Math.floor(0.1 * room2Amount[1]))] = "room2nuke";
        setRoom("room2tunnel", ROOM2, minPos + (int) (Math.floor(0.25 * room2Amount[1])), minPos, maxPos);
        setRoom("room049", ROOM2, minPos + (int) (Math.floor(0.4 * room2Amount[1])), minPos, maxPos);
        setRoom("room2shaft", ROOM2, minPos + (int) (Math.floor(0.6 * room2Amount[1])), minPos, maxPos);
        setRoom("testroom", ROOM2, minPos + (int) (Math.floor(0.7 * room2Amount[1])), minPos, maxPos);
        setRoom("room2servers", ROOM2, minPos + (int) (Math.floor(0.9 * room2Amount[1])), minPos, maxPos);

        mapRoom[ROOM3][room3Amount[0] + (int) Math.floor(0.3 * room3Amount[1])] = "room513";
        mapRoom[ROOM3][room3Amount[0] + (int) Math.floor(0.6 * room3Amount[1])] = "room966";

        mapRoom[ROOM2C][room2cAmount[0] + (int) Math.floor(0.5 * room2cAmount[1])] = "room2cpit";

        // zone 3

        mapRoom[ROOM1][room1Amount[0] + room1Amount[1] + room1Amount[2] - 2] = "exit1";
        mapRoom[ROOM1][room1Amount[0] + room1Amount[1] + room1Amount[2] - 1] = "gateaentrance";
        mapRoom[ROOM1][room1Amount[0] + room1Amount[1]] = "room1lifts";

        minPos = room2Amount[0] + room2Amount[1];
        maxPos = minPos + room2Amount[2] - 1;

        mapRoom[ROOM2][minPos + (int) (Math.floor(0.1 * room2Amount[2]))] = "room2poffices";
        setRoom("room2cafeteria", ROOM2, minPos + (int) (Math.floor(0.2 * room2Amount[2])), minPos, maxPos);
        setRoom("room2sroom", ROOM2, minPos + (int) (Math.floor(0.3 * room2Amount[2])), minPos, maxPos);
        setRoom("room2servers2", ROOM2, minPos + (int) (Math.floor(0.4 * room2Amount[2])), minPos, maxPos);
        setRoom("room2offices", ROOM2, minPos + (int) (Math.floor(0.45 * room2Amount[2])), minPos, maxPos);
        setRoom("room2offices4", ROOM2, minPos + (int) (Math.floor(0.5 * room2Amount[2])), minPos, maxPos);
        setRoom("room860", ROOM2, minPos + (int) (Math.floor(0.6 * room2Amount[2])), minPos, maxPos);
        setRoom("medibay", ROOM2, minPos + (int) (Math.floor(0.7 * room2Amount[2])), minPos, maxPos);
        setRoom("room2poffices2", ROOM2, minPos + (int) (Math.floor(0.8 * room2Amount[2])), minPos, maxPos);
        setRoom("room2offices2", ROOM2, minPos + (int) (Math.floor(0.9 * room2Amount[2])), minPos, maxPos);

        int r2c = room2cAmount[0] + room2cAmount[1];
        mapRoom[ROOM2C][r2c] = "room2ccont";
        mapRoom[ROOM2C][r2c + 1] = "lockroom2";

        int r3 = room3Amount[0] + room3Amount[1];
        mapRoom[ROOM3][r3 + (int) (Math.floor(0.3 * room3Amount[2]))] = "room3servers";
        mapRoom[ROOM3][r3 + (int) (Math.floor(0.7 * room3Amount[2]))] = "room3servers2";
        mapRoom[ROOM3][r3 + (int) (Math.floor(0.5 * room3Amount[2]))] = "room3offices";

        // luodaan kartta // creating a map

        savedRooms = new LinkedHashSet<>();

        temp = 0;
        ScpcbRoom r = null;
        float spacing = 8f;
        for (y = MAP_HEIGHT - 1; y >= 1; y--) {

            if (y < MAP_HEIGHT / 3 + 1)
                zone = 3;
            else if (y < MAP_HEIGHT * (2f / 3f))
                zone = 2;
            else
                zone = 1;

            for (x = 1; x <= MAP_WIDTH - 2; x++) {
                temp = getConnections(mapTemp, x, y);
                if (mapTemp[x][y] == 255) {
                    int type = temp == 2 ? ROOM2 : ROOM1;
                    if (y > MAP_HEIGHT / 2)     // zone = 2
                        r = createRoom(zone, type, x * 8, 0, y * 8, "checkpoint1");
                    else    // zone = 3
                        r = createRoom(zone, type, x * 8, 0, y * 8, "checkpoint2");
                } else if (mapTemp[x][y] > 0) {
                    switch (temp) {     // number of rooms in adjacement cells
                        case 1:
                            if (mapRoomID[ROOM1] < maxRooms && mapName[x][y] == null) {
                                if (mapRoom[ROOM1][mapRoomID[ROOM1]] != null)
                                    mapName[x][y] = mapRoom[ROOM1][mapRoomID[ROOM1]];
                            }

                            r = createRoom(zone, ROOM1, x * 8, 0, y * 8, mapName[x][y]);
                            if (mapTemp[x][y + 1] > 0)
                                r.angle = 180;
                            else if (mapTemp[x - 1][y] > 0)
                                r.angle = 270;
                            else if (mapTemp[x + 1][y] > 0)
                                r.angle = 90;
                            else
                                r.angle = 0;
                            mapRoomID[ROOM1]++;
                            break;
                        case 2:
                            if (getHorizontalConnections(mapTemp, x, y) == 2) {
                                if (mapRoomID[ROOM2] < maxRooms && mapName[x][y] == null) {
                                    if (mapRoom[ROOM2][mapRoomID[ROOM2]] != null)
                                        mapName[x][y] = mapRoom[ROOM2][mapRoomID[ROOM2]];
                                }
                                r = createRoom(zone, ROOM2, x * 8, 0, y * 8, mapName[x][y]);
                                if (bbRand(1, 2) == 1) {
                                    System.out.println(r.roomTemplate.name + " random angle: 90");
                                    r.angle = 90;
                                } else {
                                    System.out.println(r.roomTemplate.name + " random angle: 270");
                                    r.angle = 270;
                                }
                                mapRoomID[ROOM2]++;
                            } else if (getVerticalConnections(mapTemp, x, y) == 2) {
                                if (mapRoomID[ROOM2] < maxRooms && mapName[x][y] == null) {
                                    if (mapRoom[ROOM2][mapRoomID[ROOM2]] != null)
                                        mapName[x][y] = mapRoom[ROOM2][mapRoomID[ROOM2]];
                                }
                                r = createRoom(zone, ROOM2, x * 8, 0, y * 8, mapName[x][y]);
                                if (bbRand(1, 2) == 1) {
                                    System.out.println(r.roomTemplate.name + " random angle: 180");
                                    r.angle = 180;
                                } else {
                                    System.out.println(r.roomTemplate.name + " random angle: 0");
                                    r.angle = 0;
                                }
                                mapRoomID[ROOM2]++;
                            } else {
                                if (mapRoomID[ROOM2C] < maxRooms && mapName[x][y] == null) {
                                    if (mapRoom[ROOM2C][mapRoomID[ROOM2C]] != null)
                                        mapName[x][y] = mapRoom[ROOM2C][mapRoomID[ROOM2C]];
                                }

                                if (mapTemp[x - 1][y] > 0 && mapTemp[x][y + 1] > 0) {
                                    r = createRoom(zone, ROOM2C, x * 8, 0, y * 8, mapName[x][y]);
                                    r.angle = 180;
                                } else if (mapTemp[x + 1][y] > 0 && mapTemp[x][y + 1] > 0) {
                                    r = createRoom(zone, ROOM2C, x * 8, 0, y * 8, mapName[x][y]);
                                    r.angle = 90;
                                } else if (mapTemp[x - 1][y] > 0 && mapTemp[x][y - 1] > 0) {
                                    r = createRoom(zone, ROOM2C, x * 8, 0, y * 8, mapName[x][y]);
                                    r.angle = 270;
                                } else
                                    r = createRoom(zone, ROOM2C, x * 8, 0, y * 8, mapName[x][y]);
                                mapRoomID[ROOM2C]++;
                            }
                            break;
                        case 3:
                            if (mapRoomID[ROOM3] < maxRooms && mapName[x][y] == null) {
                                if (mapRoom[ROOM3][mapRoomID[ROOM3]] != null)
                                    mapName[x][y] = mapRoom[ROOM3][mapRoomID[ROOM3]];
                            }

                            r = createRoom(zone, ROOM3, x * 8, 0, y * 8, mapName[x][y]);
                            if (mapTemp[x][y - 1] == 0)
                                r.angle = 180;
                            else if (mapTemp[x - 1][y] == 0)
                                r.angle = 90;
                            else if (mapTemp[x + 1][y] == 0)
                                r.angle = 270;
                            mapRoomID[ROOM3]++;
                            break;
                        case 4:
                            if (mapRoomID[ROOM4] < maxRooms && mapName[x][y] == null) {
                                if (mapRoom[ROOM4][mapRoomID[ROOM4]] != null)
                                    mapName[x][y] = mapRoom[ROOM4][mapRoomID[ROOM4]];
                            }

                            r = createRoom(zone, ROOM4, x * 8, 0, y * 8, mapName[x][y]);
                            mapRoomID[ROOM4]++;
                            break;
                    }
                }
                if (r != null)
                    savedRooms.add(r);
            }
        }

        // gate a skipped (no rnd calls)
        mapRoomID[ROOM1]++;

        createRoom(0, ROOM1, (MAP_WIDTH - 1) * 8, 0, (MAP_HEIGHT - 1) * 8, "pocketdimension");
        mapRoomID[ROOM1]++;

        // intro skipped (although "173" room contains some Rnd calls, intro banned by speedrun rules)

        // 1499 skipped (no rnd calls)  // todo probably??????
        mapRoomID[ROOM1]++;

        savedRooms.forEach(SeedGenerator::preventRoomOverlap);

        return new PathFinder(randomSeed, savedRooms);

        // todo line 7642
    }

    private static int getZone(int y) {
        return (int) Math.min(ZONE_AMOUNT - 1, Math.floor((double) (MAP_WIDTH - y) / MAP_WIDTH * ZONE_AMOUNT));
    }

    ///////////////////////////////////////////

    private static int getHorizontalConnections(int[][] map, int x, int y) {
        return Math.min(1, map[x + 1][y]) + Math.min(1, map[x - 1][y]);
    }

    private static int getVerticalConnections(int[][] map, int x, int y) {
        return Math.min(1, map[x][y + 1]) + Math.min(1, map[x][y - 1]);
    }

    private static int getConnections(int[][] map, int x, int y) {
        return getVerticalConnections(map, x, y) + getHorizontalConnections(map, x, y);
    }

    ///////////////////////////////////////////

    private static boolean setRoom(String roomName, int roomType, int pos, int minPos, int maxPos) {
        if (maxPos < minPos) {
            System.out.println("Can't place " + roomName);
            return false;
        }

        System.out.println("--- SETROOM: " + roomName.toUpperCase() + " ---");
        boolean looped = false;
        boolean canPlace = true;
        while (mapRoom[roomType][pos] != null) {
            System.out.println("found " + mapRoom[roomType][pos]);
            pos++;
            if (pos > maxPos) {
                if (!looped) {
                    pos = minPos + 1;
                    looped = true;
                } else {
                    canPlace = false;
                    break;
                }
            }
        }
        System.out.println(roomName + " " + pos);
        if (canPlace) {
            System.out.println("--------------");
            mapRoom[roomType][pos] = roomName;
            return true;
        } else {
            System.out.println("couldn't place " + roomName);
            return false;
        }
    }

    private static ScpcbRoom createRoom(int zone, int roomShape, int x, int y, int z, String name) {
        ScpcbRoom r = new ScpcbRoom();
        r.shape = roomShape;

        r.zone = zone;

        r.x = x;
        r.y = y;
        r.z = z;

        if (name != null && name.length() > 0) {
            name = name.toLowerCase();
            ScpcbRoomTemplate rt = ScpcbRoomTemplate.findByName(name);
            if (rt != null) {
                r.roomTemplate = rt;
                r.fill();
            }
            // add light cones - seems no random here
            r.calcExtents();
            System.out.println("Room " + r.roomTemplate.name + " at " + x + ", " + z);
            return r;
        }

        int temp = 0;
        for (ScpcbRoomTemplate rt : ScpcbRoomTemplate.roomTemplates) {

            for (int i = 0; i <= 4; i++) {
                if (rt.zone[i] == zone) {
                    if (rt.shape == roomShape) {
                        temp += rt.commonness;
                        break;
                    }
                }
            }
        }

        int randomRoom = bbRand(1, temp);
        temp = 0;
        for (ScpcbRoomTemplate rt : ScpcbRoomTemplate.roomTemplates) {
            for (int i = 0; i <= 4; i++) {
                if (rt.zone[i] == zone && rt.shape == roomShape) {
                    temp += rt.commonness;
                    if (randomRoom > temp - rt.commonness && randomRoom <= temp) {
                        r.roomTemplate = rt;
                        r.fill();
                        // add light cones - seems no random here
                        r.calcExtents();
                        System.out.println("Room " + r.roomTemplate.name + " at " + x + ", " + z);
                        return r;
                    }
                }
            }
        }

        return null;
    }

    // todo - floating point math differences
    private static void preventRoomOverlap(ScpcbRoom r) {
        if (r.roomTemplate.disableOverlapCheck)
            return;

        boolean isIntersecting = false;

        // Just skip it when it would try to check for the checkpoints
        if (r.roomTemplate.name.contains("checkpoint") || r.roomTemplate.name.equalsIgnoreCase("start"))
            return;

        System.out.println("////////////////////");
        System.out.println("PreventRoomOverlap: " + r.roomTemplate.name);

        // First, check if the room is actually intersecting at all
        for (ScpcbRoom r2 : savedRooms) {
            if (r2 != r && !r2.roomTemplate.disableOverlapCheck) {
                if (checkRoomOverlap(r, r2)) {
                    isIntersecting = true;
                    break;
                }
            }
        }

        // If not, then simply return it as True
        if (!isIntersecting)
            return;

        // Room is intersecting: First, check if the given room is a ROOM2, so we could potentially just turn it by 180 degrees
        isIntersecting = false;
        if (r.roomTemplate.shape == ROOM2) {
            // Room is a ROOM2, let's check if turning it 180 degrees fixes the overlapping issue
            r.angle += 180;
            r.calcExtents();

            for (ScpcbRoom r2 : savedRooms) {
                if (r2 != r && !r2.roomTemplate.disableOverlapCheck) {
                    if (checkRoomOverlap(r, r2)) {
                        // didn't work -> rotate the room back and move to the next step
                        isIntersecting = true;
                        r.angle -= 180;
                        r.calcExtents();
                        break;
                    }
                }
            }
        } else
            isIntersecting = true;

        // room is ROOM2 and was able to be turned by 180 degrees
        if (!isIntersecting) {
            System.out.println("ROOM2 turning succesful! " + r.roomTemplate.name);
            return;
        }

        // Room is either not a ROOM2 or the ROOM2 is still intersecting, now trying to swap the room with another of the same type
        for (ScpcbRoom r2 : savedRooms) {
            if (r2 != r && !r2.roomTemplate.disableOverlapCheck) {
                if (r.roomTemplate.shape == r2.roomTemplate.shape &&
                        r.zone == r2.zone &&
                        !r2.roomTemplate.name.contains("checkpoint") &&
                        !r2.roomTemplate.name.equals("start")) {
                    double x = r.x / 8;
                    double y = r.z / 8;
                    int rot = r.angle;

                    double x2 = r2.x / 8;
                    double y2 = r2.z / 8;
                    int rot2 = r2.angle;

                    isIntersecting = false;

                    r.x = x2 * 8;
                    r.z = y2 * 8;
                    r.angle = rot2;
                    r.swapped = true;
                    r.calcExtents();

                    r2.x = x * 8;
                    r2.z = y * 8;
                    r2.angle = rot;
                    r2.swapped = true;
                    r2.calcExtents();

                    // make sure neither room overlaps with anything after the swap
                    for (ScpcbRoom r3 : savedRooms) {
                        if (!r3.roomTemplate.disableOverlapCheck) {
                            if (r3 != r) {
                                if (checkRoomOverlap(r, r3)) {
                                    isIntersecting = true;
                                    break;
                                }
                            }
                            if (r3 != r2) {
                                if (checkRoomOverlap(r2, r3)) {
                                    isIntersecting = true;
                                    break;
                                }
                            }
                        }
                    }

                    // Either the original room or the "reposition" room is intersecting, reset the position of each room to their original one
                    if (isIntersecting) {
                        r.x = x * 8;
                        r.z = y * 8;
                        r.angle = rot;
                        r.calcExtents();

                        r2.x = x2 * 8;
                        r2.z = y2 * 8;
                        r2.angle = rot2;
                        r2.calcExtents();

                        //isIntersecting = false; // this assignment does nothing but give misleading debug message
                    }
                }
            }
        }

        // room was able to the placed in a different spot
        if (!isIntersecting) {
            System.out.println("Room re-placing successful! " + r.roomTemplate.name);
            return;
        }

        System.out.println("Couldn't fix overlap issue for room " + r.roomTemplate.name);
    }

    private static boolean checkRoomOverlap(ScpcbRoom r1, ScpcbRoom r2) {
//        if (r1.roomTemplate.name.equals("room2testroom2") && r2.roomTemplate.name.equals("roompj"))
//            System.out.println("breakpoint");

        if (maxXLesserOrEquals(r1, r2) || maxZLesserOrEquals(r1, r2))
            return false;

        if (minXBiggerOrEquals(r1, r2) || minZBiggerOrEquals(r1, r2))
            return false;
        System.out.println("CheckRoomOverlap: " + r1.roomTemplate.name + " / " + r2.roomTemplate.name);
        return true;
    }
}
