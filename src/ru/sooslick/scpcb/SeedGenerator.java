package ru.sooslick.scpcb;

import jdk.internal.org.objectweb.asm.tree.MultiANewArrayInsnNode;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    public static void main(String[] args) {
        // found good seeds for further examination
//        String randomSeed = "qu"; // - Any% good seed
//        String randomSeed = "QZI"; // - Any% good seed
//        String randomSeed = "7em"; // - super good start

        // seed printer block
//        PathFinder pf = scpcbCreateSeed("Albania");
//        pf.printMaze();
//        pf.drawMap();
//        pf.testRouteLength(PathFinder.NO_SCP914_FINDER);

        // seed bruteforcer block
        int routeLengthThreshold = 21;
        int[] savedState = {80, 0, 80};
        int savedLength = 3;
        BruteForce bf = new BruteForce(BruteForce.HUMAN_READABLE, 2, 15, savedLength, savedState);
        while (!bf.isFinished()) {
            PathFinder pf = scpcbCreateSeed(new String(bf.next()));     // severe memory leak
            int routeLength = pf.testRouteLength(PathFinder.ANY_PERCENT_ENDGAME);
            if (routeLength < routeLengthThreshold) {
                pf.printMaze();
                bf.printState();
                break;
            }
        }
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
                    switch (temp) {
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

        mapRoom[ROOM2][0] = "lockroom";

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
        mapRoom[ROOM2C][r3 + (int) (Math.floor(0.3 * room3Amount[2]))] = "room3servers";
        mapRoom[ROOM2C][r3 + (int) (Math.floor(0.7 * room3Amount[2]))] = "room3servers2";
        mapRoom[ROOM2C][r3 + (int) (Math.floor(0.5 * room3Amount[2]))] = "room3offices";

        // luodaan kartta // creating a map

        Set<ScpcbRoom> savedRooms = new LinkedHashSet<>();

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
                if (mapTemp[x][y] == 255) {
                    if (y > MAP_HEIGHT / 2)     // zone = 2
                        r = createRoom(zone, ROOM2, x * 8, 0, y * 8, "checkpoint1");
                    else    // zone = 3
                        r = createRoom(zone, ROOM2, x * 8, 0, y * 8, "checkpoint2");
                } else if (mapTemp[x][y] > 0) {
                    temp = getConnections(mapTemp, x, y);
                    switch (temp) {
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
                                if (bbRand(1, 2) == 1)
                                    r.angle = 90;
                                else
                                    r.angle = 270;
                                mapRoomID[ROOM2]++;
                            } else if (getVerticalConnections(mapTemp, x, y) == 2) {
                                if (mapRoomID[ROOM2] < maxRooms && mapName[x][y] == null) {
                                    if (mapRoom[ROOM2][mapRoomID[ROOM2]] != null)
                                        mapName[x][y] = mapRoom[ROOM2][mapRoomID[ROOM2]];
                                }
                                r = createRoom(zone, ROOM2, x * 8, 0, y * 8, mapName[x][y]);
                                if (bbRand(1, 2) == 1)
                                    r.angle = 180;
                                else
                                    r.angle = 0;
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

        r = createRoom(0, ROOM1, (MAP_WIDTH - 1) * 8, 0, (MAP_HEIGHT - 1) * 8, "pocketdimension");
        mapRoomID[ROOM1]++;

        // intro skipped (although "173" room contains some Rnd calls, intro banned by speedrun rules)

        // 1499 skipped (no rnd calls)
        mapRoomID[ROOM1]++;

        // todo for each saved room
        //  preventRoomOverlap

        return new PathFinder(randomSeed, savedRooms);

        // todo line 7642

        //printMap(mapTemp);
        ///drawMap(savedRooms, randomSeed);
        //System.out.println("seed: " + randomSeed);
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

        r.zone = zone;

        r.x = x;
        r.y = y;
        r.z = z;

        if (name != null && name.length() > 0) {
            name = name.toLowerCase();
            ScpcbRoomTemplate rt = ScpcbRoomTemplate.findByName(name);
            if (rt != null) {
                r.roomTemplate = rt;
                // todo load room mesh - define extents
                r.fill();
            }
            // add light cones - seems no random here
            // todo skipped calc room extents. Need more checks
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
                        // todo load room mesh - define extents
                        r.fill();
                        // add light cones - seems no random here
                        // todo skipped calc room extents. Need more checks
                        return r;
                    }
                }
            }
        }

        return null;
    }
}
