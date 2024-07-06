package ru.sooslick.scpcb.map;

import java.util.LinkedHashSet;
import java.util.Set;

import static ru.sooslick.scpcb.BlitzRandom.bbRand;
import static ru.sooslick.scpcb.BlitzRandom.bbRnd;
import static ru.sooslick.scpcb.BlitzRandom.bbSeedRnd;
import static ru.sooslick.scpcb.SeedGenerator.GENERATOR_DEPTH_MAZE;
import static ru.sooslick.scpcb.SeedGenerator.GENERATOR_DEPTH_ROOMS;

public class Map {

    public static final int ROOM1 = 1;
    public static final int ROOM2 = 2;
    public static final int ROOM2C = 3;
    public static final int ROOM3 = 4;
    public static final int ROOM4 = 5;

    public static final int MAP_WIDTH = 18;
    public static final int MAP_HEIGHT = 18;
    public static final int ZONE_AMOUNT = 3;

    // custom field - depth of generator
    private final int depth;

    // maze
    private final int[][] mapTemp = new int[Map.MAP_WIDTH + 1][Map.MAP_HEIGHT + 1];

    // room count
    private final int[] room1Amount = new int[3];
    private final int[] room2Amount = new int[3];
    private final int[] room2cAmount = new int[3];
    private final int[] room3Amount = new int[3];
    private final int[] room4Amount = new int[3];

    // set of generated rooms
    public Set<ScpcbRoom> savedRooms;

    // maze fill queue
    private String[][] mapRoom;

    public Map(int seed, int depth) {
        this.depth = depth;
        createMap(seed);
    }

    private void createMap(int seed) {
        bbSeedRnd(seed);
        generateMaze();
        countRooms();
        // make sure we have at least 5 room1 in each zone
        enrichRoom1s();
        // make sure we have room2c and room4 in each zone
        enrichRoom2c4s();
        if (depth == GENERATOR_DEPTH_MAZE)
            return;

        mapRoom = new String[Map.ROOM4 + 1][room2Amount[0] + room2Amount[1] + room2Amount[2] + 3];
        defineRooms();
        createRooms();
        createDoors();
        if (depth == GENERATOR_DEPTH_ROOMS)
            return;

        initWayPoints();
    }

    private void initWayPoints() {
        // todo Main.bb line 8400
        // I've checked waypoints and seen no rnd calls there, so I can delete this part prob
    }

    private int getZone(int y) {
        return (int) Math.min(Map.ZONE_AMOUNT - 1, Math.floor((double) (Map.MAP_WIDTH - y) / Map.MAP_WIDTH * Map.ZONE_AMOUNT));
    }

    ///////////////////////////////////////////

    private int getHorizontalConnections(int[][] map, int x, int y) {
        return Math.min(1, map[x + 1][y]) + Math.min(1, map[x - 1][y]);
    }

    private int getVerticalConnections(int[][] map, int x, int y) {
        return Math.min(1, map[x][y + 1]) + Math.min(1, map[x][y - 1]);
    }

    private int getConnections(int[][] map, int x, int y) {
        return getVerticalConnections(map, x, y) + getHorizontalConnections(map, x, y);
    }

    ///////////////////////////////////////////

    private void setRoom(String roomName, int roomType, int pos, int minPos, int maxPos) {
//        System.out.println("--- SETROOM: " + roomName.toUpperCase() + " ---");
        boolean looped = false;
        boolean canPlace = true;
        while (mapRoom[roomType][pos] != null) {
//            System.out.println("found " + mapRoom[roomType][pos]);
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
//        System.out.println(roomName + " " + pos);
        if (canPlace) {
//            System.out.println("--------------");
            mapRoom[roomType][pos] = roomName;
        } //else
//            System.out.println("couldn't place " + roomName);
    }

    private ScpcbRoom createRoom(int zone, int roomShape, int x, int z, String name) {
        ScpcbRoom r = new ScpcbRoom();
        r.shape = roomShape;

        r.zone = zone;

        r.x = x;
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
//            System.out.println("Room " + r.roomTemplate.name + " at " + x + ", " + z);
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
//                        System.out.println("Room " + r.roomTemplate.name + " at " + x + ", " + z);
                        return r;
                    }
                }
            }
        }

        return null;
    }

    private void preventRoomOverlap(ScpcbRoom r) {
        if (r.roomTemplate.disableOverlapCheck)
            return;

        boolean isIntersecting = false;

        // Just skip it when it would try to check for the checkpoints
        if (r.roomTemplate.name.contains("checkpoint") || r.roomTemplate.name.equalsIgnoreCase("start"))
            return;

        //System.out.println("////////////////////");
        //System.out.println("PreventRoomOverlap: " + r.roomTemplate.name);

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
        if (r.roomTemplate.shape == Map.ROOM2) {
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
//            System.out.println("ROOM2 turning succesful! " + r.roomTemplate.name);
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
                    r.calcExtents();

                    r2.x = x * 8;
                    r2.z = y * 8;
                    r2.angle = rot;
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

                        isIntersecting = false; // this assignment does nothing but give misleading debug message
                    }
                    // my personal stuff to work around SCP:CB bug
//                    else {
//                        System.out.println("ACTUAL successful room replacement");
//                    }
                }
            }
        }

        // room was able to the placed in a different spot
//        if (!isIntersecting) {
//            System.out.println("Room re-placing successful! " + r.roomTemplate.name);
//            return;
//        }

//        System.out.println("Couldn't fix overlap issue for room " + r.roomTemplate.name);
    }

    private boolean checkRoomOverlap(ScpcbRoom r1, ScpcbRoom r2) {
        if (r1.maxX <= r2.minX || r1.maxZ <= r2.minZ)
            return false;

        if (r1.minX >= r2.maxX || r1.minZ >= r2.maxZ)
            return false;

        //System.out.println("CheckRoomOverlap: " + r1.roomTemplate.name + " / " + r2.roomTemplate.name + "\n...");
        return true;
    }

    private ScpcbRoom findRoom(int x, int z) {
        return savedRooms.stream()
                .filter(r -> (int) r.x / 8 == x)
                .filter(r -> (int) r.z / 8 == z)
                .findFirst()
                .orElse(null);
    }

    private void generateMaze() {

        int x = Map.MAP_WIDTH / 2;
        int y = Map.MAP_HEIGHT - 2;

        // define start room
        mapTemp[x][Map.MAP_HEIGHT - 1] = 1;

        int width, height, temp = 0;
        do {
            // generate horizontal line
            width = bbRand(10, 15);

            if (x > Map.MAP_WIDTH * 0.6)
                width = -width;
            else if (x > Map.MAP_WIDTH * 0.4)
                x = x - width / 2;

            // make sure the hallway doesn't go outside the array
            if (x + width > Map.MAP_WIDTH - 3)
                width = Map.MAP_WIDTH - 3 - x;
            else if (x + width < 2)
                width = -x + 2;

            x = Math.min(x, x + width);
            width = Math.abs(width);
            for (int i = x; i <= x + width; i++)
                mapTemp[i][y] = 1;

            // generate vertical connections
            height = bbRand(3, 4);
            if (y - height < 1)
                height = y - 1;

            int yHallways = bbRand(4, 5);

            if (getZone(y - height) != getZone(y - height + 1))
                height--;

            for (int i = 1; i <= yHallways; i++) {

                int x2 = Math.max(2, Math.min(Map.MAP_WIDTH - 2, bbRand(x, x + width - 1)));
                while (mapTemp[x2 - 1][y - 1] != 0 || mapTemp[x2][y - 1] != 0 || mapTemp[x2 + 1][y - 1] != 0)
                    x2++;

                if (x2 < x + width) {
                    int tempHeight;
                    if (i == 1) {
                        // make sure that we generate at least one connection to next horizontal line
                        tempHeight = height;
                        if (bbRand(1, 2) == 1)
                            x2 = x;
                        else
                            x2 = x + width;
                    } else
                        tempHeight = bbRand(1, height);

                    for (int y2 = y - tempHeight; y2 <= y; y2++) {
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
    }

    private void countRooms() {
        int zone;
        // count the amount of rooms
        for (int y = 1; y <= Map.MAP_HEIGHT; y++) {
            zone = getZone(y);

            for (int x = 1; x <= Map.MAP_WIDTH - 1; x++) {
                if (mapTemp[x][y] > 0) {
                    if (mapTemp[x][y] < 255)
                        mapTemp[x][y] = getConnections(mapTemp, x, y);
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
    }

    private void enrichRoom1s() {
        int x2 = 0, y2 = 0, roomsLeft;

        // force more room1s (if needed) for each zone
        for (int i = 0; i <= 2; i++) {
            // need more rooms if there are less than 5 of them
            roomsLeft = 5 - room1Amount[i];
            if (roomsLeft > 0) {
                for (int y = (Map.MAP_HEIGHT / ZONE_AMOUNT) * (2 - i) + 1; y <= ((Map.MAP_HEIGHT / ZONE_AMOUNT) * ((2 - i) + 1)) - 2; y++) {
                    for (int x = 2; x <= Map.MAP_WIDTH - 2; x++) {
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

                                        roomsLeft--;
                                    }
                                }
                            }
                        }
                        if (roomsLeft == 0)
                            break;
                    }
                    if (roomsLeft == 0)
                        break;
                }
            }
        }
    }

    private void enrichRoom2c4s() {
        // force more room4s and room2Cs
        int zoneStart = 0, zoneEnd = 0;
        boolean placed;
        for (int i = 0; i <= 2; i++) {

            switch (i) {
                case 2:
                    zoneStart = 2;
                    zoneEnd = Map.MAP_HEIGHT / 3;
                    break;
                case 1:
                    zoneStart = Map.MAP_HEIGHT / 3 + 1;
                    zoneEnd = (int) (Map.MAP_HEIGHT * (2.0 / 3.0) - 1);
                    break;
                case 0:
                    zoneStart = (int) (Map.MAP_HEIGHT * (2.0 / 3.0) + 1);
                    zoneEnd = Map.MAP_HEIGHT - 2;
            }

            if (room4Amount[i] < 1) {   // we want at least 1 ROOM4
                //System.out.println("forcing a ROOM4 into zone " + i);
                placed = false;

                for (int y = zoneStart; y <= zoneEnd; y++) {
                    for (int x = 2; x <= Map.MAP_WIDTH - 2; x++) {
                        if (mapTemp[x][y] == 3) {   // see if adding a ROOM1 is possible
                            if (mapTemp[x + 1][y] == 0 && mapTemp[x + 1][y + 1] == 0 && mapTemp[x + 1][y - 1] == 0 && mapTemp[x + 2][y] == 0) {
                                mapTemp[x + 1][y] = 1;
                                placed = true;
                            } else if (mapTemp[x - 1][y] == 0 && mapTemp[x - 1][y + 1] == 0 && mapTemp[x - 1][y - 1] == 0 && mapTemp[x - 2][y] == 0) {
                                mapTemp[x - 1][y] = 1;
                                placed = true;
                            } else if (mapTemp[x][y + 1] == 0 && mapTemp[x + 1][y + 1] == 0 && mapTemp[x - 1][y + 1] == 0 && mapTemp[x][y + 2] == 0) {
                                mapTemp[x][y + 1] = 1;
                                placed = true;
                            } else if (mapTemp[x][y - 1] == 0 && mapTemp[x + 1][y - 1] == 0 && mapTemp[x - 1][y - 1] == 0 && mapTemp[x][y - 2] == 0) {
                                mapTemp[x][y - 1] = 1;
                                placed = true;
                            }
                            if (placed) {
                                mapTemp[x][y] = 4; // turn this room into a ROOM4
                                //System.out.println("ROOM4 forced into slot (" + x + ", " + y + ")");
                                room4Amount[i]++;
                                room3Amount[i]--;
                                room1Amount[i]++;
                            }
                        }
                        if (placed)
                            break;
                    }
                    if (placed)
                        break;
                }
            }

            if (room2cAmount[i] < 1) {  // we want at least 1 ROOM2C
                //System.out.println("forcing a ROOM2C into zone " + i);
                placed = false;

                zoneStart++;
                zoneEnd--;

                for (int y = zoneStart; y <= zoneEnd; y++) {
                    for (int x = 3; x <= Map.MAP_WIDTH - 3; x++) {
                        if (mapTemp[x][y] == 1) {   // see if adding some rooms is possible
                            if (mapTemp[x - 1][y] > 0) {
                                if (mapTemp[x][y - 1] + mapTemp[x][y + 1] + mapTemp[x + 2][y] == 0) {
                                    if (mapTemp[x + 1][y - 2] + mapTemp[x + 2][y - 1] + mapTemp[x + 1][y - 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x + 1][y] = 2;
                                        //System.out.println("ROOM2C forced into slot (" + (x + 1) + ", " + (y) + ")");
                                        mapTemp[x + 1][y - 1] = 1;
                                        placed = true;
                                    } else if (mapTemp[x + 1][y + 2] + mapTemp[x + 2][y + 1] + mapTemp[x + 1][y + 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x + 1][y] = 2;
                                        //System.out.println("ROOM2C forced into slot (" + (x + 1) + ", " + (y) + ")");
                                        mapTemp[x + 1][y + 1] = 1;
                                        placed = true;
                                    }
                                }
                            } else if (mapTemp[x + 1][y] > 0) {
                                if (mapTemp[x][y - 1] + mapTemp[x][y + 1] + mapTemp[x - 2][y] == 0) {
                                    if (mapTemp[x - 1][y - 2] + mapTemp[x - 2][y - 1] + mapTemp[x - 1][y - 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x - 1][y] = 2;
                                        //System.out.println("ROOM2C forced into slot (" + (x - 1) + ", " + (y) + ")");
                                        mapTemp[x - 1][y - 1] = 1;
                                        placed = true;
                                    } else if (mapTemp[x - 1][y + 2] + mapTemp[x - 2][y + 1] + mapTemp[x - 1][y + 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x - 1][y] = 2;
                                        //System.out.println("ROOM2C forced into slot (" + (x - 1) + ", " + (y) + ")");
                                        mapTemp[x - 1][y + 1] = 1;
                                        placed = true;
                                    }
                                }
                            } else if (mapTemp[x][y - 1] > 0) {
                                if (mapTemp[x - 1][y] + mapTemp[x + 1][y] + mapTemp[x][y + 2] == 0) {
                                    if (mapTemp[x - 2][y + 1] + mapTemp[x - 1][y + 2] + mapTemp[x - 1][y + 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x][y + 1] = 2;
                                        //System.out.println("ROOM2C forced into slot (" + x + ", " + (y + 1) + ")");
                                        mapTemp[x - 1][y + 1] = 1;
                                        placed = true;
                                    } else if (mapTemp[x + 2][y + 1] + mapTemp[x + 1][y + 2] + mapTemp[x + 1][y + 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x][y + 1] = 2;
                                        //System.out.println("ROOM2C forced into slot (" + (x) + ", " + (y + 1) + ")");
                                        mapTemp[x + 1][y + 1] = 1;
                                        placed = true;
                                    }
                                }
                            } else if (mapTemp[x][y + 1] > 0) {
                                if (mapTemp[x - 1][y] + mapTemp[x + 1][y] + mapTemp[x][y - 2] == 0) {
                                    if (mapTemp[x - 2][y - 1] + mapTemp[x - 1][y - 2] + mapTemp[x - 1][y - 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x][y - 1] = 2;
                                        //System.out.println("ROOM2C forced into slot (" + x + ", " + (y - 1) + ")");
                                        mapTemp[x - 1][y - 1] = 1;
                                        placed = true;
                                    } else if (mapTemp[x + 2][y - 1] + mapTemp[x + 1][y - 2] + mapTemp[x + 1][y - 1] == 0) {
                                        mapTemp[x][y] = 2;
                                        mapTemp[x][y - 1] = 2;
                                        //System.out.println("ROOM2C forced into slot (" + (x) + ", " + (y - 1) + ")");
                                        mapTemp[x + 1][y - 1] = 1;
                                        placed = true;
                                    }
                                }
                            }
                            if (placed) {
                                room2cAmount[i]++;
                                room2Amount[i]++;
                            }
                        }
                        if (placed)
                            break;
                    }
                    if (placed)
                        break;
                }
            }
        }
    }

    private void defineRooms() {
        // zone 1
        int minPos = 1;
        int maxPos = room1Amount[0] - 1;

        mapRoom[Map.ROOM1][0] = "start";
        setRoom("roompj", Map.ROOM1, (int) (Math.floor(0.1 * room1Amount[0])), minPos, maxPos);
        setRoom("914", Map.ROOM1, (int) (Math.floor(0.3 * room1Amount[0])), minPos, maxPos);
        setRoom("room1archive", Map.ROOM1, (int) (Math.floor(0.5 * room1Amount[0])), minPos, maxPos);
        setRoom("room205", Map.ROOM1, (int) (Math.floor(0.6 * room1Amount[0])), minPos, maxPos);

        mapRoom[Map.ROOM2C][0] = "lockroom";

        maxPos = room2Amount[0] - 1;

        mapRoom[Map.ROOM2][0] = "room2closets";
        setRoom("room2testroom2", Map.ROOM2, (int) (Math.floor(0.1 * room2Amount[0])), minPos, maxPos);
        setRoom("room2scps", Map.ROOM2, (int) (Math.floor(0.2 * room2Amount[0])), minPos, maxPos);
        setRoom("room2storage", Map.ROOM2, (int) (Math.floor(0.3 * room2Amount[0])), minPos, maxPos);
        setRoom("room2gw_b", Map.ROOM2, (int) (Math.floor(0.4 * room2Amount[0])), minPos, maxPos);
        setRoom("room2sl", Map.ROOM2, (int) (Math.floor(0.5 * room2Amount[0])), minPos, maxPos);
        setRoom("room012", Map.ROOM2, (int) (Math.floor(0.55 * room2Amount[0])), minPos, maxPos);
        setRoom("room2scps2", Map.ROOM2, (int) (Math.floor(0.6 * room2Amount[0])), minPos, maxPos);
        setRoom("room1123", Map.ROOM2, (int) (Math.floor(0.7 * room2Amount[0])), minPos, maxPos);
        setRoom("room2elevator", Map.ROOM2, (int) (Math.floor(0.85 * room2Amount[0])), minPos, maxPos);

        mapRoom[Map.ROOM3][(int) Math.floor(bbRnd(0.2f, 0.8f) * room3Amount[0])] = "room3storage";

        mapRoom[Map.ROOM2C][(int) Math.floor(0.5 * room2cAmount[0])] = "room1162";

        mapRoom[Map.ROOM4][(int) Math.floor(0.3 * room4Amount[0])] = "room4info";

        // zone 2
        minPos = room1Amount[0];
        maxPos = minPos + room1Amount[1] - 1;

        setRoom("room079", Map.ROOM1, room1Amount[0] + (int) (Math.floor(0.15 * room1Amount[1])), minPos, maxPos);
        setRoom("room106", Map.ROOM1, room1Amount[0] + (int) (Math.floor(0.3 * room1Amount[1])), minPos, maxPos);
        setRoom("008", Map.ROOM1, room1Amount[0] + (int) (Math.floor(0.4 * room1Amount[1])), minPos, maxPos);
        setRoom("room035", Map.ROOM1, room1Amount[0] + (int) (Math.floor(0.5 * room1Amount[1])), minPos, maxPos);
        setRoom("coffin", Map.ROOM1, room1Amount[0] + (int) (Math.floor(0.7 * room1Amount[1])), minPos, maxPos);

        minPos = room2Amount[0];
        maxPos = minPos + room2Amount[1] - 1;

        mapRoom[Map.ROOM2][minPos + (int) (Math.floor(0.1 * room2Amount[1]))] = "room2nuke";
        setRoom("room2tunnel", Map.ROOM2, minPos + (int) (Math.floor(0.25 * room2Amount[1])), minPos, maxPos);
        setRoom("room049", Map.ROOM2, minPos + (int) (Math.floor(0.4 * room2Amount[1])), minPos, maxPos);
        setRoom("room2shaft", Map.ROOM2, minPos + (int) (Math.floor(0.6 * room2Amount[1])), minPos, maxPos);
        setRoom("testroom", Map.ROOM2, minPos + (int) (Math.floor(0.7 * room2Amount[1])), minPos, maxPos);
        setRoom("room2servers", Map.ROOM2, minPos + (int) (Math.floor(0.9 * room2Amount[1])), minPos, maxPos);

        mapRoom[Map.ROOM3][room3Amount[0] + (int) Math.floor(0.3 * room3Amount[1])] = "room513";
        mapRoom[Map.ROOM3][room3Amount[0] + (int) Math.floor(0.6 * room3Amount[1])] = "room966";

        mapRoom[Map.ROOM2C][room2cAmount[0] + (int) Math.floor(0.5 * room2cAmount[1])] = "room2cpit";

        // zone 3
        mapRoom[Map.ROOM1][room1Amount[0] + room1Amount[1] + room1Amount[2] - 2] = "exit1";
        mapRoom[Map.ROOM1][room1Amount[0] + room1Amount[1] + room1Amount[2] - 1] = "gateaentrance";
        mapRoom[Map.ROOM1][room1Amount[0] + room1Amount[1]] = "room1lifts";

        minPos = room2Amount[0] + room2Amount[1];
        maxPos = minPos + room2Amount[2] - 1;

        mapRoom[Map.ROOM2][minPos + (int) (Math.floor(0.1 * room2Amount[2]))] = "room2poffices";
        setRoom("room2cafeteria", Map.ROOM2, minPos + (int) (Math.floor(0.2 * room2Amount[2])), minPos, maxPos);
        setRoom("room2sroom", Map.ROOM2, minPos + (int) (Math.floor(0.3 * room2Amount[2])), minPos, maxPos);
        setRoom("room2servers2", Map.ROOM2, minPos + (int) (Math.floor(0.4 * room2Amount[2])), minPos, maxPos);
        setRoom("room2offices", Map.ROOM2, minPos + (int) (Math.floor(0.45 * room2Amount[2])), minPos, maxPos);
        setRoom("room2offices4", Map.ROOM2, minPos + (int) (Math.floor(0.5 * room2Amount[2])), minPos, maxPos);
        setRoom("room860", Map.ROOM2, minPos + (int) (Math.floor(0.6 * room2Amount[2])), minPos, maxPos);
        setRoom("medibay", Map.ROOM2, minPos + (int) (Math.floor(0.7 * room2Amount[2])), minPos, maxPos);
        setRoom("room2poffices2", Map.ROOM2, minPos + (int) (Math.floor(0.8 * room2Amount[2])), minPos, maxPos);
        setRoom("room2offices2", Map.ROOM2, minPos + (int) (Math.floor(0.9 * room2Amount[2])), minPos, maxPos);

        int r2c = room2cAmount[0] + room2cAmount[1];
        mapRoom[Map.ROOM2C][r2c] = "room2ccont";
        mapRoom[Map.ROOM2C][r2c + 1] = "lockroom2";

        int r3 = room3Amount[0] + room3Amount[1];
        mapRoom[Map.ROOM3][r3 + (int) (Math.floor(0.3 * room3Amount[2]))] = "room3servers";
        mapRoom[Map.ROOM3][r3 + (int) (Math.floor(0.7 * room3Amount[2]))] = "room3servers2";
        mapRoom[Map.ROOM3][r3 + (int) (Math.floor(0.5 * room3Amount[2]))] = "room3offices";
    }

    private void createRooms() {
        int zone, connections;
        int[] mapRoomID = new int[Map.ROOM4 + 1];
        savedRooms = new LinkedHashSet<>();
        ScpcbRoom r = null;
        for (int y = Map.MAP_HEIGHT - 1; y >= 1; y--) {

            if (y < Map.MAP_HEIGHT / 3 + 1)
                zone = 3;
            else if (y < Map.MAP_HEIGHT * (2f / 3f))
                zone = 2;
            else
                zone = 1;

            for (int x = 1; x <= Map.MAP_WIDTH - 2; x++) {
                connections = getConnections(mapTemp, x, y);
                if (mapTemp[x][y] == 255) {
                    int type = connections == 2 ? Map.ROOM2 : Map.ROOM1;
                    if (y > Map.MAP_HEIGHT / 2)     // zone = 2
                        r = createRoom(zone, type, x * 8, y * 8, "checkpoint1");
                    else    // zone = 3
                        r = createRoom(zone, type, x * 8, y * 8, "checkpoint2");
                } else if (mapTemp[x][y] > 0) {
                    String mapName = null;
                    switch (connections) {     // number of rooms in adjacement cells
                        case 1:
                            if (mapRoom[Map.ROOM1][mapRoomID[Map.ROOM1]] != null)
                                mapName = mapRoom[Map.ROOM1][mapRoomID[Map.ROOM1]];

                            r = createRoom(zone, Map.ROOM1, x * 8, y * 8, mapName);
                            if (mapTemp[x][y + 1] > 0)
                                r.angle = 180;
                            else if (mapTemp[x - 1][y] > 0)
                                r.angle = 270;
                            else if (mapTemp[x + 1][y] > 0)
                                r.angle = 90;
                            else
                                r.angle = 0;
                            mapRoomID[Map.ROOM1]++;
                            break;
                        case 2:
                            if (getHorizontalConnections(mapTemp, x, y) == 2) {
                                if (mapRoom[Map.ROOM2][mapRoomID[Map.ROOM2]] != null)
                                    mapName = mapRoom[Map.ROOM2][mapRoomID[Map.ROOM2]];
                                r = createRoom(zone, Map.ROOM2, x * 8, y * 8, mapName);
                                if (bbRand(1, 2) == 1) {
                                    //System.out.println(r.roomTemplate.name + " random angle: 90");
                                    r.angle = 90;
                                } else {
                                    //System.out.println(r.roomTemplate.name + " random angle: 270");
                                    r.angle = 270;
                                }
                                mapRoomID[Map.ROOM2]++;
                            } else if (getVerticalConnections(mapTemp, x, y) == 2) {
                                if (mapRoom[Map.ROOM2][mapRoomID[Map.ROOM2]] != null)
                                    mapName = mapRoom[Map.ROOM2][mapRoomID[Map.ROOM2]];
                                r = createRoom(zone, Map.ROOM2, x * 8, y * 8, mapName);
                                if (bbRand(1, 2) == 1) {
                                    //System.out.println(r.roomTemplate.name + " random angle: 180");
                                    r.angle = 180;
                                } else {
                                    //System.out.println(r.roomTemplate.name + " random angle: 0");
                                    r.angle = 0;
                                }
                                mapRoomID[Map.ROOM2]++;
                            } else {
                                if (mapRoom[Map.ROOM2C][mapRoomID[Map.ROOM2C]] != null)
                                    mapName = mapRoom[Map.ROOM2C][mapRoomID[Map.ROOM2C]];

                                if (mapTemp[x - 1][y] > 0 && mapTemp[x][y + 1] > 0) {
                                    r = createRoom(zone, Map.ROOM2C, x * 8, y * 8, mapName);
                                    r.angle = 180;
                                } else if (mapTemp[x + 1][y] > 0 && mapTemp[x][y + 1] > 0) {
                                    r = createRoom(zone, Map.ROOM2C, x * 8, y * 8, mapName);
                                    r.angle = 90;
                                } else if (mapTemp[x - 1][y] > 0 && mapTemp[x][y - 1] > 0) {
                                    r = createRoom(zone, Map.ROOM2C, x * 8, y * 8, mapName);
                                    r.angle = 270;
                                } else
                                    r = createRoom(zone, Map.ROOM2C, x * 8, y * 8, mapName);
                                mapRoomID[Map.ROOM2C]++;
                            }
                            break;
                        case 3:
                            if (mapRoom[Map.ROOM3][mapRoomID[Map.ROOM3]] != null)
                                mapName = mapRoom[Map.ROOM3][mapRoomID[Map.ROOM3]];

                            r = createRoom(zone, Map.ROOM3, x * 8, y * 8, mapName);
                            if (mapTemp[x][y - 1] == 0)
                                r.angle = 180;
                            else if (mapTemp[x - 1][y] == 0)
                                r.angle = 90;
                            else if (mapTemp[x + 1][y] == 0)
                                r.angle = 270;
                            mapRoomID[Map.ROOM3]++;
                            break;
                        case 4:
                            if (mapRoom[Map.ROOM4][mapRoomID[Map.ROOM4]] != null)
                                mapName = mapRoom[Map.ROOM4][mapRoomID[Map.ROOM4]];

                            r = createRoom(zone, Map.ROOM4, x * 8, y * 8, mapName);
                            mapRoomID[Map.ROOM4]++;
                            break;
                    }
                }
                if (r != null)
                    savedRooms.add(r);
            }
        }

        // gate a skipped (no rnd calls)
        mapRoomID[Map.ROOM1]++;

        createRoom(0, Map.ROOM1, (Map.MAP_WIDTH - 1) * 8, (Map.MAP_HEIGHT - 1) * 8, "pocketdimension");
        mapRoomID[Map.ROOM1]++;

        // intro skipped (although "173" room contains some Rnd calls, intro banned by speedrun rules)

        // todo check if room height messes up overlapping check
        r = createRoom(0, Map.ROOM1, 8, 0, "dimension1499");
        savedRooms.add(r);  // add 1499 to overlap check
        mapRoomID[Map.ROOM1]++;

        savedRooms.forEach(this::preventRoomOverlap);
        savedRooms.remove(r);   /// remove 1499 from the map after overlap check
    }

    private void createDoors() {
        int iZoneTransition0 = 13;
        int iZoneTransition1 = 7;

        int zone, type = 0;
        ScpcbDoor d;
        boolean shouldSpawnDoor;
        for (int y = Map.MAP_HEIGHT; y >= 0; y--) {
            if (y < iZoneTransition1 - 1)
                zone = 3;
            else if (y >= iZoneTransition1 && y < iZoneTransition0)
                zone = 2;
            else
                zone = 1;

            for (int x = Map.MAP_WIDTH; x >= 0; x--) {
                if (mapTemp[x][y] > 0) {
                    if (zone == 2)
                        type = 2;
                    else
                        type = 0;
                }

                ScpcbRoom r = findRoom(x, y);
                if (r != null) {
                    r.angle = r.angle % 360;
                    shouldSpawnDoor = false;
                    switch (r.shape) {
                        case Map.ROOM1:
                            if (r.angle == 90)
                                shouldSpawnDoor = true;
                            break;
                        case Map.ROOM2:
                            if (r.angle == 90 || r.angle == 270)
                                shouldSpawnDoor = true;
                            break;
                        case Map.ROOM2C:
                            if (r.angle == 0 || r.angle == 90)
                                shouldSpawnDoor = true;
                            break;
                        case Map.ROOM3:
                            if (r.angle == 0 || r.angle == 90 || r.angle == 180)
                                shouldSpawnDoor = true;
                            break;
                        default:
                            shouldSpawnDoor = true;
                    }
                    if (shouldSpawnDoor) {
                        if (x < Map.MAP_WIDTH) {
                            if (mapTemp[x + 1][y] > 0) {
                                d = new ScpcbDoor(r, Math.max(bbRand(-3, 1), 0) > 0, type);
                                r.adjDoorRight = d;
                            }
                        }
                    }

                    shouldSpawnDoor = false;
                    switch (r.shape) {
                        case Map.ROOM1:
                            if (r.angle == 180)
                                shouldSpawnDoor = true;
                            break;
                        case Map.ROOM2:
                            if (r.angle == 0 || r.angle == 180)
                                shouldSpawnDoor = true;
                            break;
                        case Map.ROOM2C:
                            if (r.angle == 90 || r.angle == 180)
                                shouldSpawnDoor = true;
                            break;
                        case Map.ROOM3:
                            if (r.angle == 90 || r.angle == 180 || r.angle == 270)
                                shouldSpawnDoor = true;
                            break;
                        default:
                            shouldSpawnDoor = true;
                    }
                    if (shouldSpawnDoor) {
                        if (x < Map.MAP_HEIGHT) {
                            if (mapTemp[x][y + 1] > 0) {
                                d = new ScpcbDoor(r, Math.max(bbRand(-3, 1), 0) > 0, type);
                                r.adjDoorBottom = d;
                            }
                        }
                    }
                }
            }
        }
    }
}
