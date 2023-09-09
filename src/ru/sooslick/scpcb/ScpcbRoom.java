package ru.sooslick.scpcb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ru.sooslick.scpcb.BlitzRandom.bbRand;
import static ru.sooslick.scpcb.BlitzRandom.bbRnd;

public class ScpcbRoom {
    private static boolean iZoneHasCustomForest = false;
    private static boolean room2gwBrokenDoor = false;
    private static int room2gw_x;
    private static int room2gw_z;

    int zone;
    int x, y, z;    // actually float, but idc
    int angle;
    ScpcbRoomTemplate roomTemplate;

    Map<String, String> rndInfo = new HashMap<>();

    public void fill() {
        switch (roomTemplate.name) {
            case "room860":
                if (!iZoneHasCustomForest)
                    genForestGrid();
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
            case "room079":
            case "room2shaft":
            case "room2tunnel":
            case "room012":
                bbRnd(0, 360);
                break;
            case "room2poffices2":
                bbRnd(0, 360);
                bbRnd(0, 360);
                bbRnd(0, 360);
                break;
            case "room3storage":
                switch (bbRand(1, 3)) {
                    case 1:
                        rndInfo.put("Hand", "Near nvgs");
                        break;
                    case 2:
                        rndInfo.put("Hand", "Middle");
                        break;
                    case 3:
                        rndInfo.put("Hand", "Behind crates");
                        break;
                }
                bbRnd(0, 360);
                break;
            case "room3servers":
            case "room2closets":
                bbRand(1, 2);
                bbRand(1, 2);
                break;
            case "room2offices2":
                bbRand(1, 2);
                bbRand(1, 4);
                break;
            case "room2offices3":
                bbRand(1, 2);
                bbRand(0, 1);
                bbRand(1, 2);
                bbRand(1, 2);
                break;
            case "start":
                bbRnd(0, 360);
                bbRnd(0, 360);
                break;
            case "room2scps":
                for (int i = 0; i <= 14; i++) {
                    bbRand(15, 16);
                    bbRand(1, 360);
                    if (i > 10)
                        bbRnd(0.2f, 0.25f);
                    else
                        bbRnd(0.1f, 0.17f);
                }
                break;
            case "coffin":
                bbRand(1, 360);
                break;
            case "173":
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
                break;
            case "room1archive":
                List<String> items = new LinkedList<>();
                for (int i = 0; i < 18; i++) {
                    String tempStr = null;
                    int chance = bbRand(-10, 100);
                    if (chance < 0)
                        continue;
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
                }
                rndInfo.put("items", items.toString());
                break;
            case "pocketdimension":
                bbRnd(0.8f, 0.8f);
                for (int i = 1; i <= 8; i++) {
                    if (i < 6)
                        bbRnd(0.5f, 0.5f);
                }
                break;
            case "room2servers2":
                bbRand(0, 245);
                break;
            case "room2gw":
            case "room2gw_b":
                //
                if (roomTemplate.name.equals("room2gw_b"))
                    bbRnd(0, 360);
                boolean bd_temp = false;
                if (room2gwBrokenDoor && room2gw_x == x && room2gw_z == z)
                    bd_temp = true;
                if ((!room2gwBrokenDoor && bbRand(1, 2) == 1) || bd_temp) {
                    room2gwBrokenDoor = true;
                    room2gw_x = x;
                    room2gw_z = z;
                }
                break;
        }
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
                    int newDir = turnIfDeviating(maxDeviationDistance, pathx, center, dir);
                    // todo VERY suspicious call here in original game - line 917
                    deviated = newDir != dir;
                    dir = newDir;
                    if (deviated)
                        grid[((gridSize - 1 - pathy) * gridSize) + pathx] = 1;
                    pathx = moveForward(dir, pathx, pathy, 0);
                    pathy = moveForward(dir, pathx, pathy, 1);
                }
            } else {
                // we are going to the side, so determine whether to keep going or go forward again
                int newDir = turnIfDeviating(maxDeviationDistance, pathx, center, dir);
                // todo VERY suspicious call here in original game - line 926
                deviated = newDir != dir;
                dir = newDir;
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
            newx = 0;
            if (chance(branchChance)) {
                branchType = -1;
                int cobbleChance = 0;   // cobble_chance NOT defined at all!
                if (chance(cobbleChance)) {
                    branchType = -2;
                }
                // create a branch at this spot
                // determine if on left or on right
                branchPos = 2 & bbRand(0, 1);
                // get leftmost or rightmost path in this row
                int leftMost = gridSize;
                int rightMost = 0;
                for (i = 0; i <= gridSize; i++) {
                    if (grid[((gridSize - 1 - newy) * gridSize) + i] == 1) {
                        if (i < leftMost)
                            leftMost = i;
                        if (i < rightMost)
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

        // todo print map to string and attach to rndInfo
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
}
