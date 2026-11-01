package ru.sooslick.scpcb.map;

import java.util.List;

/**
 * SCP: Containment Breach v1.3.11 map.
 */
public class Map1311 extends Map {

    public Map1311(int seed) {
        super(seed);
        generate();
    }

    @Override
    protected List<ScpcbRoomTemplate> roomTemplates() {
        return ScpcbRoomTemplate.roomTemplates;
    }

    @Override
    protected ScpcbRoom newRoom() {
        return new ScpcbRoom(rng, doors);
    }

    @Override
    protected int mapRoomCapacity(int roomSlots) {
        return roomSlots;
    }

    @Override
    protected List<ScpcbRoom> createFixedRooms() {
        // gate A and the pocket dimension aren't shown on the map and aren't checked for overlaps,
        // but creating them consumes random numbers
        createRoom(0, Map.ROOM1, 500, 500, "gatea");
        createRoom(0, Map.ROOM1, (Map.MAP_WIDTH - 1) * 8, (Map.MAP_HEIGHT - 1) * 8, "pocketdimension");
        return List.of();
    }

    @Override
    protected int doorZone(int y) {
        if (y < ZONE_TRANSITION_1 - 1)
            return 3;
        else if (y >= ZONE_TRANSITION_1 && y < ZONE_TRANSITION_0)
            return 2;
        else
            return 1;
    }

    @Override
    protected void initGameState() {
        state106 = rng.bbRand(12, 17);
        createDecals();
        playerAngle = rng.bbRand(160, 200);

        int savedState = rng.getRndState();
        Events events = new Events(savedRooms, false, rng);
        savedEventsNormal = events.createEvents();

        rng.bbSeedRnd(savedState);
        events = new Events(savedRooms, true, rng);
        savedEventsKeter = events.createEvents();

        savedEventsNormal.forEach(e -> e.room.linkedEventNormal = e);
        savedEventsKeter.forEach(e -> e.room.linkedEventKeter = e);

        createTunnels();
        defineLoadingScreen();

        findPDExitTunnel();
    }

    @Override
    protected void defineRooms() {
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

        mapRoom[Map.ROOM3][(int) Math.floor(rng.bbRnd(0.2f, 0.8f) * room3Amount[0])] = "room3storage";

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

    private void createDecals() {
        for (ScpcbRoom r : savedRooms) {
            if (!r.roomTemplate.disableDecals) {
                randomDecals();
            }
            if (r.roomTemplate.name.equals("start")) {
                r.createItem();
                r.createItem();
            }
        }
        //I'd removed these rooms from list, so I have to create decals outside rooms cycle
        randomDecals(); // "gatea"
        randomDecals(); // "pocketdimension"
    }

    private void randomDecals() {
        if (rng.bbRand(1, 4) == 1) {
            rng.bbRand(2, 3);
            rng.bbRnd(-2, 2);
            rng.bbRnd(-2, 2);
            rng.bbRand(0, 360);
            rng.bbRnd(0.1f, 0.4f);
            rng.bbRnd(0.85f, 0.95f);
        }
        if (rng.bbRand(1, 4) == 1) {
            rng.bbRnd(-2, 2);
            rng.bbRnd(-2, 2);
            rng.bbRand(0, 360);
            rng.bbRnd(0.5f, 0.7f);
            rng.bbRnd(0.7f, 0.85f);
        }
    }

    private void createTunnels() {
        int[] grid = new int[MT_SIZE * MT_SIZE];
        rng.bbSeedRnd(seed);

        // 0 = right
        // 1 = up
        // 2 = left
        // 3 = down
        int dir = rng.bbRand(0, 1) << 1;
        int ix = MT_SIZE / 2 + rng.bbRand(-2, 2);
        int iy = MT_SIZE / 2 + rng.bbRand(-2, 2);
        grid[ix + (iy * MT_SIZE)] = 1;
        if (dir == 2) {
            grid[(ix + 1) + (iy * MT_SIZE)] = 1;
        } else {
            grid[(ix - 1) + (iy * MT_SIZE)] = 1;
        }

        int count = 2;
        int turns = 0;
        while (count < 100 && turns++ < 100) {
            int tempInt = rng.bbRand(1, 5) << rng.bbRand(1, 2);
            for (int i = 1; i <= tempInt; i++) {

                boolean tempInt2 = true;    // Regalis??? hello

                switch (dir) {
                    case 0:
                        if (ix < MT_SIZE - 2 - (i % 2))
                            ix = ix + 1;
                        else
                            tempInt2 = false;
                        break;
                    case 1:
                        if (iy < MT_SIZE - 2 - (i % 2))
                            iy = iy + 1;
                        else
                            tempInt2 = false;
                        break;
                    case 2:
                        if (ix > 1 + (i % 2))
                            ix = ix - 1;
                        else
                            tempInt2 = false;
                        break;
                    case 3:
                        if (iy > 1 + (i % 2))
                            iy = iy - 1;
                        else
                            tempInt2 = false;
                        break;
                }

                if (tempInt2) {
                    if (grid[ix + (iy * MT_SIZE)] == 0) {
                        grid[ix + (iy * MT_SIZE)] = 1;
                        count = count + 1;
                    }
                } else
                    break;
            }

            dir += (rng.bbRand(0, 1) << 1) - 1;
            if (dir < 0)
                dir += 4;
            else if (dir > 3)
                dir -= 4;
        }

        for (int x = 0; x < MT_SIZE; x++) {
            for (int y = 0; y < MT_SIZE; y++) {
                if (grid[x + ((y) * MT_SIZE)] > 0)
                    grid[x + ((y) * MT_SIZE)] = Math.min(grid[x + ((y + 1) * MT_SIZE)], 1) +
                            Math.min(grid[x + ((y - 1) * MT_SIZE)], 1) +
                            Math.min(grid[(x + 1) + (y * MT_SIZE)], 1) +
                            Math.min(grid[(x - 1) + (y * MT_SIZE)], 1);
            }
        }

        int maxX = MT_SIZE - 1;
        boolean canRetry = false;
        int retries = 0;

        for (ix = 0; ix <= maxX; ix++) {
            for (iy = 0; iy <= MT_SIZE - 1; iy++) {
                if (grid[ix + 1 + (iy * MT_SIZE)] > 0) {
                    maxX = ix;
                    if (grid[ix + 1 + ((iy + 1) * MT_SIZE)] < 3 && grid[ix + 1 + ((iy - 1) * MT_SIZE)] < 3) {
                        canRetry = true;
                        if (rng.bbRand(0, 1) == 1) {
                            grid[ix + 1 + ((iy) * MT_SIZE)] = grid[ix + 1 + ((iy) * MT_SIZE)] + 1;
                            grid[ix + ((iy) * MT_SIZE)] = 7;
                            canRetry = false;
                            break;
                        }
                    }
                }
            }
            if (canRetry && retries++ < 20)
                ix--;
        }

        // Regalis??? hello what's wrong with your vars initialization
        int firstX = -1;
        int lastX = 0;
        int firstY = 0;
        int lastY = -1;

        for (iy = 0; iy < MT_SIZE; iy++) {
            for (ix = 0; ix < MT_SIZE; ix++) {
                if (grid[ix + (iy * MT_SIZE)] == 2) {
                    if (grid[(ix + 1) + ((iy) * MT_SIZE)] > 0 && grid[(ix - 1) + ((iy) * MT_SIZE)] > 0) {
                        // horizontal
                        if (firstX == -1 || firstY == -1) {
                            if (grid[ix - 1 + (iy * MT_SIZE)] < 3 && grid[ix + 1 + (iy * MT_SIZE)] < 3 && grid[ix + ((iy - 1) * MT_SIZE)] < 3 && grid[ix + ((iy + 1) * MT_SIZE)] < 3) {
                                if (grid[ix - 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix + 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix + 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix - 1 + ((iy + 1) * MT_SIZE)] < 1) {
                                    firstX = ix;
                                    firstY = iy;
                                }
                            }
                        }
                        if (grid[ix - 1 + (iy * MT_SIZE)] < 3 && grid[ix + 1 + (iy * MT_SIZE)] < 3 && grid[ix + ((iy - 1) * MT_SIZE)] < 3 && grid[ix + ((iy + 1) * MT_SIZE)] < 3) {
                            if (grid[ix - 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix + 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix + 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix - 1 + ((iy + 1) * MT_SIZE)] < 1) {
                                lastX = ix;
                                lastY = iy;
                            }
                        }
                    } else if (grid[(ix) + ((iy + 1) * MT_SIZE)] > 0 && grid[(ix) + ((iy - 1) * MT_SIZE)] > 0) {
                        // vertical
                        if (firstX == -1 || firstY == -1) {
                            if (grid[ix - 1 + (iy * MT_SIZE)] < 3 && grid[ix + 1 + (iy * MT_SIZE)] < 3 && grid[ix + ((iy - 1) * MT_SIZE)] < 3 && grid[ix + ((iy + 1) * MT_SIZE)] < 3) {
                                if (grid[ix - 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix + 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix + 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix - 1 + ((iy + 1) * MT_SIZE)] < 1) {
                                    firstX = ix;
                                    firstY = iy;
                                }
                            }
                        }
                        if (grid[ix - 1 + (iy * MT_SIZE)] < 3 && grid[ix + 1 + (iy * MT_SIZE)] < 3 && grid[ix + ((iy - 1) * MT_SIZE)] < 3 && grid[ix + ((iy + 1) * MT_SIZE)] < 3) {
                            if (grid[ix - 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix + 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix + 1 + ((iy - 1) * MT_SIZE)] < 1 && grid[ix - 1 + ((iy + 1) * MT_SIZE)] < 1) {
                                lastX = ix;
                                lastY = iy;
                            }
                        }
                    }
                }
            }
        }

        if (firstX >= 0 && firstY >= 0)
            grid[firstX + (firstY * MT_SIZE)] = 5;
        if (lastX >= 0 && lastY >= 0)
            grid[lastX + (lastY * MT_SIZE)] = 6;

        // I assume the following code does not generate any valuable info
        // UpdateEvents.bb #3236

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MT_SIZE; i++) {
            for (int j = 0; j < MT_SIZE; j++) {
                if (grid[(i * MT_SIZE) + j] >= 7)
                    sb.append("H");
                else if (grid[(i * MT_SIZE) + j] >= 5)
                    sb.append("E");
                else if (grid[(i * MT_SIZE) + j] > 0)
                    sb.append("X");
                else
                    sb.append(".");
            }
            sb.append("|");
        }
        savedRooms.stream()
                .filter(r -> r.roomTemplate.name.contains("room2tunnel"))
                .findFirst()
                .ifPresent(r -> r.rndInfo = "tunnels=" + sb);
    }

    private void defineLoadingScreen() {
        rng.bbSeedRnd(seed);
        loadingScreen = LoadingScreens.find(rng.bbRand(1, 32));
    }

    private void findPDExitTunnel() {
        for (ScpcbRoom r : savedRooms) {
            if ("tunnel".equals(r.roomTemplate.name)) {
                r.rndInfo = "Pocket Dimension exit";
                return;
            }
        }
    }
}
