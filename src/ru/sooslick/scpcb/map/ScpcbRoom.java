package ru.sooslick.scpcb.map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import static ru.sooslick.scpcb.BlitzRandom.bbRand;
import static ru.sooslick.scpcb.BlitzRandom.bbRnd;
import static ru.sooslick.scpcb.map.Map.ROOM1;
import static ru.sooslick.scpcb.map.Map.ROOM2;
import static ru.sooslick.scpcb.map.Map.ROOM2C;
import static ru.sooslick.scpcb.map.Map.ROOM3;
import static ru.sooslick.scpcb.map.Map.ROOM4;
import static ru.sooslick.scpcb.map.ScpcbDoor.createDoor;

public class ScpcbRoom {
    private static final double ROOM_SCALE = 8d / 2048;
    private static final BufferedImage[] hmap = new BufferedImage[ROOM4 + 1];

    private static boolean room2gwBrokenDoor = false;
    private static double room2gw_x;
    private static double room2gw_z;

    static {
        try {
            hmap[ROOM1] = ImageIO.read(new FileInputStream("scpcbFiles/forest1h.png"));
            hmap[ROOM2] = ImageIO.read(new FileInputStream("scpcbFiles/forest2h.png"));
            hmap[ROOM2C] = ImageIO.read(new FileInputStream("scpcbFiles/forest2Ch.png"));
            hmap[ROOM3] = ImageIO.read(new FileInputStream("scpcbFiles/forest3h.png"));
            hmap[ROOM4] = ImageIO.read(new FileInputStream("scpcbFiles/forest4h.png"));
        } catch (Exception e) {
            throw new RuntimeException("Error reading Forest textures", e);
        }
    }

    public int zone;
    public double x, z;
    public int angle;
    public int shape;

    public ScpcbRoomTemplate roomTemplate;
    public ScpcbDoor adjDoorBottom;
    public ScpcbDoor adjDoorRight;

    public double minX, minZ;
    public double maxX, maxZ;
    int extentsAngle;
    MeshExtents extents;

    public String rndInfo = null;
    public String overlaps = null;
    public ScpcbEvent linkedEventNormal;
    public ScpcbEvent linkedEventKeter;

    public void fill() {
        switch (roomTemplate.name) {
            case "room860":
                createDoor(false, 0);
                createDoor(true, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                genForestGrid();
                createItem();
                createItem();
                break;
            case "lockroom":
            case "room205":
            case "room2doors":
            case "lockroom3":
                createDoor(true, 0);
                createDoor(true, 0);
                break;
            case "lockroom2":
                for (int i = 0; i <= 5; i++) {
                    // random decals
                    bbRand(2, 3);
                    bbRnd(-392, 520);
                    bbRnd(0, 0.001f);
                    bbRnd(-392, 520);
                    bbRnd(0, 360);
                    bbRnd(0.3f, 0.6f);

                    bbRand(15, 16);
                    bbRnd(-392, 520);
                    bbRnd(0, 0.001f);
                    bbRnd(-392, 520);
                    bbRnd(0, 360);
                    bbRnd(0.1f, 0.6f);

                    bbRand(15, 16);
                    bbRnd(-0.5f, 0.5f);
                    bbRnd(0, 0.001f);
                    bbRnd(-0.5f, 0.5f);
                    bbRnd(0, 360);
                    bbRnd(0.1f, 0.6f);
                }
                break;
            case "gatea":
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                // this door probably may cause some issues with maps without gate A, but everything is fine rn
                createDoor(false, 3);
                break;
            case "gateaentrance":
                createDoor(true, 3);
                createDoor(false, 1);
                break;
            case "exit1":
                createDoor(false, 1);
                createDoor(true, 3);
                createDoor(false, 3);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                break;
            case "roompj":
                createItem();
                createItem();
                createDoor(true, 1);
                break;
            case "room079":
                createDoor(false, 1);
                createDoor(false, 1);
                createDoor(false, 0);
                bbRnd(0, 360);
                break;
            case "checkpoint1":
            case "checkpoint2":
                createDoor(false, 0);
                createDoor(false, 0);
                if (shape == ROOM1) {
                    createDoor(false, 0);
                    // My workaround for checkpoint rooms
                    // SCP:CB works with checkpoint as if they are ROOM2s, but with some extra code
                    // I'd simplified a lot of scp:cb code by making dead end checkpoints ROOM1,
                    // but I have to set it back to ROOM2 to generate doors correctly
                    shape = ROOM2;
                }
                break;
            case "room2testroom2":
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                createItem();
                break;
            case "room2storage":
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                createItem();
                createItem();
                createItem();
                break;
            case "room2sroom":
                createDoor(false, 0);
                createItem();
                createItem();
                createItem();
                createItem();
                createItem();
                break;
            case "room3offices":
                createDoor(false, 0);
                break;
            case "room2shaft":
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                createItem();
                createItem();
                createItem();
                createItem();
                bbRnd(0, 360);
                break;
            case "room2poffices":
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                createItem();
                createItem();
                createItem();
                createItem();
                break;
            case "room2sl":
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                break;
            case "room2poffices2":
                createDoor(false, 0);
                createDoor(false, 0);
                bbRnd(0, 360);
                bbRnd(0, 360);
                bbRnd(0, 360);
                createItem();
                createItem();
                createItem();
                break;
            case "room2elevator":
                createDoor(false, 3);
                break;
            case "room2cafeteria":
                createItem();
                createItem();
                createItem();
                createItem();
                createItem();
                break;
            case "room2nuke":
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(true, 3);
                createDoor(false, 3);
                createItem();
                createItem();
                break;
            case "room2tunnel":
                createDoor(true, 3);
                createDoor(true, 3);
                createDoor(false, 1);
                bbRnd(0, 360);
                createItem();
                break;
            case "008":
                createDoor(true, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                createItem();
                break;
            case "room035":
                createDoor(true, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                createItem();
                createItem();
                createItem();
                createItem();
                break;
            case "room513":
                createDoor(false, 0);
                createItem();
                createItem();
                createItem();
                break;
            case "room966":
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                break;
            case "room3storage":
                createDoor(true, 3);
                createDoor(false, 3);
                createDoor(true, 3);
                createDoor(false, 3);
                switch (bbRand(1, 3)) {
                    case 1:
                        rndInfo = "Hand near nvgs";
                        break;
                    case 2:
                        rndInfo = "Hand at the middle";
                        break;
                    case 3:
                        rndInfo = "Hand behind crates";
                        break;
                }
                createItem();
                createItem();
                bbRnd(0, 360);
                createDoor(false, 2);
                createDoor(false, 2);
                createDoor(false, 2);
                createDoor(false, 2);
                break;
            case "room049":
                createDoor(true, 3);
                createDoor(false, 3);
                createDoor(true, 3);
                createDoor(false, 3);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 2);
                createItem();
                createItem();
                createItem();
                createDoor(true, 1);
                createDoor(false, 2);
                createDoor(false, 2);
                break;
            case "room012":
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                createItem();
                bbRnd(0, 360);
                break;
            case "room2servers":
                createDoor(false, 2);
                createDoor(true, 0);
                createDoor(true, 0);
                createDoor(false, 0);
                break;
            case "room3servers":
                createItem();
                if (bbRand(1, 2) == 1)
                    createItem();
                if (bbRand(1, 2) == 1)
                    createItem();
                createItem();
                break;
            case "room3servers2":
                createItem();
                createItem();
                break;
            case "testroom":
                createDoor(false, 2);
                createDoor(true, 0);
                createItem();
                break;
            case "room2closets":
                createItem();
                createItem();
                createItem();
                if (bbRand(1, 2) == 1)
                    createItem();
                if (bbRand(1, 2) == 1)
                    createItem();
                createItem();
                createItem();
                createItem();
                createDoor(false, 0);
                break;
            case "room2offices":
                createItem();
                createItem();
                createItem();
                createItem();
                break;
            case "room2offices2":
                createItem();
                createItem();
                bbRand(1, 2);
                createItem();   // if / else document
                createItem();
                bbRand(1, 4);
                break;
            case "room2offices3":
                bbRand(1, 2);
                createItem();   // if / else document
                createItem();
                createItem();
                createItem();
                for (int i = 0; i <= bbRand(0, 1); i++)
                    createItem();
                createItem();
                if (bbRand(1, 2) == 1)
                    createItem();
                if (bbRand(1, 2) == 1)
                    createItem();
                createDoor(true, 0);
                break;
            case "start":
                createDoor(true, 1);
                createDoor(false, 0);
                createDoor(true, 0);
                createDoor(false, 0);
                createDoor(true, 0);
                createDoor(false, 0);
                bbRnd(0, 360);
                bbRnd(0, 360);
                break;
            case "room2scps":
                createDoor(true, 0);
                createDoor(true, 0);
                createDoor(true, 0);
                createDoor(true, 0);
                createDoor(true, 0);
                createDoor(true, 0);
                createItem();
                createItem();
                createItem();
                createItem();
                createItem();
                for (int i = 0; i <= 14; i++) {
                    bbRand(15, 16);
                    bbRand(1, 360);
                    if (i > 10)
                        bbRnd(0.2f, 0.25f);
                    else
                        bbRnd(0.1f, 0.17f);
                }
                break;
            case "endroom":
                createDoor(false, 1);
                break;
            case "endroomc":
                createDoor(false, 2);
                break;
            case "coffin":
                createDoor(false, 1);
                createItem();
                createItem();
                createItem();
                break;
            case "914":
                createDoor(false, 1);
                createDoor(true, 0);
                createDoor(true, 0);
                createItem();
                createItem();
                createItem();
                break;
            case "173":
                createDoor(false, 1);
                bbRand(4, 5);
                bbRnd(0, 360);
                for (int x = 0; x <= 1; x++)
                    for (int z = 0; z <= 1; z++) {
                        bbRand(4, 6);
                        bbRnd(-0.5f, 0.5f);
                        bbRnd(0.001f, 0.0018f);
                        bbRnd(-0.5f, 0.5f);
                        bbRnd(0, 360);
                        bbRnd(0.5f, 0.8f);
                        bbRnd(0.8f, 1.0f);
                    }
                createDoor(true, 0);
                createDoor(true, 0);
                createDoor(true, 0);
                createDoor(true, 0);
                createDoor(false, 0);
                createDoor(true, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                for (int z = 0; z <= 1; z++) {
                    createDoor(false, 0);
                    createDoor(false, 0);
                    for (int x = 0; x <= 2; x++)
                        createDoor(false, 0);
                    for (int x = 0; x <= 4; x++)
                        createDoor(false, 0);
                }
                createItem();
                break;
            case "room2ccont":
            case "room1162":
            case "room2offices4":
                createDoor(false, 0);
                createItem();
                break;
            case "room106":
                createItem();
                createItem();
                createItem();
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                break;
            case "room1archive":
                List<String> items = new LinkedList<>();
                for (int i = 0; i <= 1; i++) {
                    for (int j = 0; j <= 2; j++) {
                        for (int k = 0; k <= 2; k++) {
                            String tempStr = "bat";
                            int chance = bbRand(-10, 100);
                            if (chance < 0)
                                break;
                            else if (chance < 40) {
                                tempStr = "doc";
                                switch (bbRand(1, 6)) {
                                    case 1:
                                        tempStr += "1123";
                                        break;
                                    case 2:
                                        tempStr += "1048";
                                        break;
                                    case 3:
                                        tempStr += "939";
                                        break;
                                    case 4:
                                        tempStr += "682";
                                        break;
                                    case 5:
                                        tempStr += "079";
                                        break;
                                    case 6:
                                        tempStr += "966";
                                        break;
                                }
                            } else if (chance < 45) {
                                tempStr = "K" + bbRand(1, 2);
                            } else if (chance < 50)
                                tempStr = "med";
                            else if (chance < 60)
                                tempStr = "bat";
                            else if (chance < 70)
                                tempStr = "nav";
                            else if (chance < 85)
                                tempStr = "radio";
                            else if (chance < 95)
                                tempStr = "clipboard";
                            else {
                                if (bbRand(1, 3) < 3)
                                    tempStr = "K0";
                            }
                            if (tempStr != null)
                                items.add(tempStr);
                            bbRnd(-96, 96);
                            createItem();
                        }
                    }
                }
                rndInfo = "Items: " + String.join(", ", items);
                createDoor(false, 0);
                break;
            case "room2test1074":
                // wtf is this room?
                createDoor(false, 3);
                createDoor(true, 3);
                createDoor(true, 3);
                createDoor(false, 3);
                createItem();
                break;
            case "room1123":
                createItem();
                createItem();
                createItem();
                createItem();
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                break;
            case "pocketdimension":
                createItem();
                createDoor(false, 0);
                createDoor(false, 0);
                bbRnd(0.8f, 0.8f);
                for (int i = 1; i <= 8; i++) {
                    if (i < 6)
                        bbRnd(0.5f, 0.5f);
                }
                break;
            case "room2servers2":
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                bbRand(0, 245);
                break;
            case "room2gw":
            case "room2gw_b":
                // ENDSHN, your code for these rooms is unreadable mess (i'm sorry)
                if (roomTemplate.name.equals("room2gw_b"))
                    bbRnd(0, 360);
                createDoor(false, 0);
                createDoor(false, 0);
                if (roomTemplate.name.equals("room2gw")) {
                    boolean bd_temp = (room2gwBrokenDoor && room2gw_x == x && room2gw_z == z);
                    if ((bbRand(1, 2) == 1 && !room2gwBrokenDoor) || bd_temp) {
                        room2gwBrokenDoor = true;
                        room2gw_x = x;
                        room2gw_z = z;
                    }
                }
                break;
            case "room3gw":
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                break;
            case "room2scps2":
                createDoor(false, 0);
                createDoor(false, 0);
                createDoor(false, 0);
                createItem();
                createItem();
                createItem();
                createItem();
                break;
            case "medibay":
                createItem();
                createItem();
                createItem();
                createDoor(false, 0);
                break;
            case "room2cpit":
                createDoor(false, 2);
                createItem();
                break;
        }

        // Const MaxRoomLights% = 32
        for (int i = 0; i < Math.min(32, roomTemplate.lightsAmount); i++) {
            bbRand(1, 360);
            bbRand(1, 10);
        }

        // no rnd calls at CreateScreen, CreateWaypoint
    }

    public void calcExtents() {
        if (roomTemplate.disableOverlapCheck)
            return;

        // while generating map SCP:CB can rotate room without changing extents
        // so I have to store this variable to prevent blatant lie in console output
        extentsAngle = angle;
        extents = roomTemplate.extents.copyTransform(ROOM_SCALE, angle);
        // System.out.println("Room template extents: " + roomTemplate.extents);

        // adjustments by bounds db dumped from vanilla game
        // this is custom block of code which overwrites vanilla behaviour
        // but this block is critically important because of differences in java vs blitz3d floating point math
        if (x > 0 && z > 0) {
            // System.out.printf("request db search: %s (%f:%f %d°)%n", roomTemplate.name, x, z, angle);
            RoomExtentsDB.Boundaries b = RoomExtentsDB.findExtents(roomTemplate.name, angle, (int) x / 8, (int) z / 8);
            minX = b.minX;
            maxX = b.maxX;
            minZ = b.minZ;
            maxZ = b.maxZ;
        }

        // vanilla math block
        // I left this branch because of unusual 1499 placement (db doesn't store dimension1499)
        else {
            // shrink the extents slightly - we don't care if the overlap is smaller than the thickness of the walls
            minX = extents.minX + 0.05 + x;
            minZ = extents.minZ + 0.05 + z;
            maxX = extents.maxX - 0.05 + x;
            maxZ = extents.maxZ - 0.05 + z;

            // re-implementing BUG from SCP:CB
            if (minX > maxX) {
                double a = minX;
                minX = maxX;
                maxX = a;
            }
            if (minZ > maxZ) {
                double a = minZ;
                minZ = maxZ;
                maxZ = a;
            }

//        System.out.printf("Room %s extents : %s, %s, %s / %s, %s, %s / %s°%n", roomTemplate.name,
//                minX, minY, minZ,
//                maxX, maxY, maxZ,
//                angle);
        }
    }

    // I assume I haven't any need in vanilla extents after generating map,
    // so I mess up vanilla extents to find out real overlaps
    public void fixExtents() {
        if (extents == null)
            return;

        double extMinX = Math.min(extents.minX, extents.maxX);
        double extMaxX = Math.max(extents.minX, extents.maxX);
        double extMinZ = Math.min(extents.minZ, extents.maxZ);
        double extMaxZ = Math.max(extents.minZ, extents.maxZ);

        // shrink the extents slightly - we don't care if the overlap is smaller than the thickness of the walls
        minX = extMinX + 0.05 + x;
        minZ = extMinZ + 0.05 + z;
        maxX = extMaxX - 0.05 + x;
        maxZ = extMaxZ - 0.05 + z;
    }

    public void addOverlap(ScpcbRoom other) {
        String add = (int) (other.x / 8) + "-" + (int) (other.z / 8);
        if (overlaps == null)
            overlaps = add;
        else
            overlaps+= "," + add;
    }

    private void genForestGrid() {
        int gridSize = 10;
        int deviationChance = 40;
        int returnChance = 27;
        int branchChance = 65;
        int maxDeviationDistance = 3;
        int center = 5;
        int branchMaxLife = 4;
        int branchDieChance = 18;

        int door1pos, door2pos;
        int i, j;
        door1pos = bbRand(3, 7);
        door2pos = bbRand(3, 7);

        // weird 2dimension array declaration from original game
        int[] grid = new int[gridSize * gridSize + 1];

        // set the position of the concrete and doors
        grid[door1pos] = 3;
        grid[(gridSize - 1) * gridSize + door2pos] = 3;

        // generate the path
        int pathx = door2pos;
        int pathy = 1;
        int dir = 1;
        grid[((gridSize - 1 - pathy) * gridSize) + pathx] = 1;

        boolean deviated;

        while (pathy < gridSize - 4) {
            if (dir == 1) {
                // determine whether to go forward or to the side
                if (chance(deviationChance)) {
                    // pick a branch direction
                    dir = 2 * bbRand(0, 1);
                    // make sure you have not passed max side distance
                    dir = turnIfDeviating(maxDeviationDistance, pathx, center, dir);
                    // VERY suspicious call here in original game - line 917
                    deviated = turnIfDeviatingBool(maxDeviationDistance, pathx, center, dir);
                    if (deviated)
                        grid[((gridSize - 1 - pathy) * gridSize) + pathx] = 1;
                    pathx = moveForward(dir, pathx, pathy, 0);
                    pathy = moveForward(dir, pathx, pathy, 1);
                }
            } else {
                // we are going to the side, so determine whether to keep going or go forward again
                dir = turnIfDeviating(maxDeviationDistance, pathx, center, dir);
                // VERY suspicious call here in original game - line 926
                deviated = turnIfDeviatingBool(maxDeviationDistance, pathx, center, dir);
                if (deviated || chance(returnChance))
                    dir = 1;

                pathx = moveForward(dir, pathx, pathy, 0);
                pathy = moveForward(dir, pathx, pathy, 1);
                // if we just started going forward go twice so as to avoid creating a potential 2x2 line
                if (dir == 1) {
                    grid[((gridSize - 1 - pathy) * gridSize) + pathx] = 1;
                    pathx = moveForward(dir, pathx, pathy, 0);
                    pathy = moveForward(dir, pathx, pathy, 1);
                }
            }

            // add our position to the grid
            grid[((gridSize - 1 - pathy) * gridSize) + pathx] = 1;
        }
        // finally, bring the path back to the door now that we have reached the end
        dir = 1;
        while (pathy < gridSize - 2) {
            pathx = moveForward(dir, pathx, pathy, 0);
            pathy = moveForward(dir, pathx, pathy, 1);
            grid[((gridSize - 1 - pathy) * gridSize) + pathx] = 1;
        }

        if (pathx != door1pos) {
            dir = 0;
            if (door1pos > pathx)
                dir = 2;
            while (pathx != door1pos) {
                pathx = moveForward(dir, pathx, pathy, 0);
                pathy = moveForward(dir, pathx, pathy, 1);
                grid[((gridSize - 1 - pathy) * gridSize) + pathx] = 1;
            }
        }

        //attempt to create new branches
        int newy, tempy, newx;
        int branchType, branchPos;
        newy = -3;  // used for counting off; branches will only be considered once every 4 units so as to avoid potentially too many branches
        while (newy < gridSize - 6) {
            newy += 4;
            tempy = newy;
            if (chance(branchChance)) {
                branchType = -1;
                int cobbleChance = 0;   // cobble_chance NOT defined at all!
                                        // btw, chance(0) hit true on seed 2001011999, I need to doublecheck this
                if (chance(cobbleChance)) {
                    branchType = -2;
                }
                // create a branch at this spot
                // determine if on left or on right
                branchPos = 2 * bbRand(0, 1);
                // get leftmost or rightmost path in this row
                int leftMost = gridSize;
                int rightMost = 0;
                for (i = 0; i <= gridSize; i++) {
                    if (grid[((gridSize - 1 - newy) * gridSize) + i] == 1) {
                        if (i < leftMost)
                            leftMost = i;
                        if (i > rightMost)
                            rightMost = i;
                    }
                }
                if (branchPos == 0)
                    newx = leftMost - 1;
                else
                    newx = rightMost + 1;
                // before creating a branch make sure there are no 1's above or below
                if ((tempy != 0 && grid[((gridSize - 1 - tempy + 1) * gridSize) + newx] == 1) || (grid[((gridSize - 1 - tempy - 1) * gridSize) + newx] == 1)) {
                    break; // break simply to stop creating the branch
                }
                grid[((gridSize - 1 - tempy) * gridSize) + newx] = branchType;  // make 4s so you don't confuse your branch for a path; will be changed later
                if (branchPos == 0)
                    newx = leftMost - 2;
                else
                    newx = rightMost + 2;
                grid[((gridSize - 1 - tempy) * gridSize) + newx] = branchType;    // branch out twice to avoid creating an unwanted 2x2 path with the real path
                i = 2;
                while (i < branchMaxLife) {
                    i++;
                    if (chance(branchDieChance))
                        break;
                    if (bbRand(0, 3) == 0) {    // have a higher chance to go up to confuse the player
                        if (branchPos == 0)
                            newx--;
                        else
                            newx++;
                    } else
                        tempy++;

                    // before creating a branch make sure there are no 1's above or below
                    int n = ((gridSize - 1 - tempy + 1) * gridSize) + newx;
                    if (n < gridSize - 1) {
                        if (tempy != 0 && grid[n] == 1)
                            break;
                    }
                    n = ((gridSize - 1 - tempy - 1) * gridSize) + newx;
                    if (n > 0) {
                        if (grid[n] == 1)
                            break;
                    }

                    grid[((gridSize - 1 - tempy) * gridSize) + newx] = branchType; // make 4s so you don't confuse your branch for a path; will be changed later
                    if (tempy >= gridSize - 2)
                        break;
                }
            }
        }

        // change branches from 4s to 1s (they were 4s so that they didn't accidently
        for (i = 0; i <= gridSize - 1; i++) {
            for (j = 0; j <= gridSize - 1; j++) {
                if (grid[(i * gridSize) + j] == -1)
                    grid[(i * gridSize) + j] = 1;
                else if (grid[(i * gridSize) + j] == -2)
                    grid[(i * gridSize) + j] = 1;
            }
        }

        // Function PlaceForest
        boolean[] itemPlaced = new boolean[4];
        for (int tx = 1; tx <= gridSize - 1; tx++) {
            for (int ty = 1; ty <= gridSize - 1; ty++) {
                if (grid[ty * gridSize + tx] == 1) {
                    int tileType = 0;
                    boolean hasHorConnection = false;
                    boolean hasVertConnection = false;
                    if (tx + 1 < gridSize) {
                        tileType = grid[(ty * gridSize) + tx + 1] > 0 ? 1 : 0;
                        hasHorConnection = grid[(ty * gridSize) + tx + 1] > 0;
                    }
                    if (tx - 1 >= 0) {
                        tileType += grid[(ty * gridSize) + tx - 1] > 0 ? 1 : 0;
                        hasHorConnection = hasHorConnection || grid[(ty * gridSize) + tx - 1] > 0;
                    }
                    if (ty + 1 < gridSize) {
                        tileType += grid[((ty + 1) * gridSize) + tx] > 0 ? 1 : 0;
                        hasVertConnection = grid[((ty + 1) * gridSize) + tx] > 0;
                    }
                    if (ty - 1 >= 0) {
                        tileType += grid[((ty - 1) * gridSize) + tx] > 0 ? 1 : 0;
                        hasVertConnection = hasVertConnection || grid[((ty - 1) * gridSize) + tx] > 0;
                    }

                    if (tileType > 2)
                        tileType++;
                    else if (tileType == 2 && hasHorConnection && hasVertConnection)
                        tileType = ROOM2C;

                    if (tileType > 0) {

                        // 2, 5, 8
                        if (ty % 3 == 2 && !itemPlaced[ty / 3]) {
                            itemPlaced[ty / 3] = true;
                            createItem();

                            grid[ty * gridSize + tx] = 255; // custom Sooslick stuff for painting webmap cells
                        }

                        // place trees and other details
                        // only placed on spots where the value of the heightmap is above 100
                        BufferedImage img = hmap[tileType];
                        int width = img.getWidth();
                        for (int lx = 3; lx <= width - 2; lx++) {
                            for (int ly = 3; ly <= width - 2; ly++) {
                                int rgb = img.getRGB(lx, width - ly);
                                int red = (rgb & 0x00ff0000) >> 16;

                                if (red > bbRand(100, 260)) {
                                    int roll = bbRand(0, 7);
                                    // create a tree
                                    if (roll < 7) {
                                        bbRnd(0.25f, 0.4f);

                                        for (i = 0; i <= 3; i++) {
                                            bbRnd(-20, 20);
                                        }

                                        bbRnd(3.0f, 3.2f);
                                        bbRnd(-5, 5);
                                        bbRnd(0, 360.0f);
                                    }
                                    // add a rock
                                    if (roll == 7) {
                                        //bbRnd(0.01f,0.012f);
                                        bbRnd(1, 2);
                                        bbRnd(0, 360.0f);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (i = 0; i < gridSize; i++) {
            for (j = gridSize - 1; j >= 0; j--) {
                if (grid[(i * gridSize) + j] == 255)
                    sb.append("H");
                else if (grid[(i * gridSize) + j] != 0)
                    sb.append("X");
                else
                    sb.append(".");
            }
            sb.append("|");
        }
        rndInfo = "forest=" + sb;
    }

    private boolean chance(int prob) {
        return bbRand(0, 100) <= prob;
    }

    private int turnIfDeviating(int maxDeviationDistance, int pathx, int center, int dir) {
        // check if deviating and return the answer. if deviating, turn around
        int currDeviation = center - pathx;
        if ((dir == 0 && currDeviation >= maxDeviationDistance) || (dir == 2 && currDeviation <= -maxDeviationDistance)) {
            dir = (dir + 2) % 4;
        }
        return dir;
    }

    private boolean turnIfDeviatingBool(int maxDeviationDistance, int pathx, int center, int dir) {
        // check if deviating and return the answer. if deviating, turn around
        int currDeviation = center - pathx;
        return (dir == 0 && currDeviation >= maxDeviationDistance) || (dir == 2 && currDeviation <= -maxDeviationDistance);
    }

    private int moveForward(int dir, int pathx, int pathy, int rv) {
        // move 1 unit along the grid in the designated direction
        if (dir == 1) {
            if (rv == 0)
                return pathx;
            else
                return pathy + 1;
        }
        if (rv == 0)
            return pathx - 1 + dir;
        else
            return pathy;
    }

    protected void createItem() {
        bbRand(1, 360); // just rotation ._.
    }

    public String toString() {
        return roomTemplate.name + " at " + x + "," + z;
    }
}
