package ru.sooslick.scpcb.map;

import java.util.List;
import java.util.Set;

/**
 * SCP: Containment Breach Ultimate Edition Classic v5.5.4.1 map (default 18x18 size, intro skipped).
 */
public class MapUeClassic extends Map {

    /**
     * Location of SCP-005, which UE chooses before the map seed is applied (it is not derived from the seed):
     * 1 - SCP-005 chamber, 2 - Maynard's office, 3 - SCP-409 chamber.
     */
    public final int scp005;

    public MapUeClassic(int seed, int scp005) {
        super(seed);
        if (scp005 < 1 || scp005 > 3)
            throw new IllegalArgumentException("SCP-005 location must be 1, 2 or 3");
        this.scp005 = scp005;
        generate();
    }

    @Override
    protected List<ScpcbRoomTemplate> roomTemplates() {
        return UeClassicRooms.TEMPLATES;
    }

    @Override
    protected ScpcbRoom newRoom() {
        return new UeClassicRoom(rng, doors, scp005);
    }

    @Override
    protected int mapRoomCapacity(int roomSlots) {
        return Math.max(55, roomSlots);
    }

    @Override
    protected List<ScpcbRoom> createFixedRooms() {
        // unlike 1.3.11, UE places both rooms inside the map bounds and checks them for overlaps
        ScpcbRoom gateA = createRoom(0, Map.ROOM1, (Map.MAP_WIDTH - 1) * 8, 8, "gatea");
        ScpcbRoom pd = createRoom(0, Map.ROOM1, (Map.MAP_WIDTH - 1) * 8, (Map.MAP_HEIGHT - 1) * 8, "pocketdimension");
        return List.of(gateA, pd);
    }

    @Override
    protected int doorZone(int y) {
        // the zone boundaries are shifted one row up compared to 1.3.11
        if (y < ZONE_TRANSITION_1 - 1)
            return 3;
        else if (y < ZONE_TRANSITION_0 - 1)
            return 2;
        else
            return 1;
    }

    @Override
    protected boolean isOverlapCheckExempt(ScpcbRoomTemplate template) {
        return super.isOverlapCheckExempt(template) || template.name.equals("room173");
    }

    @Override
    protected boolean checkRoomOverlap(ScpcbRoom r1, ScpcbRoom r2) {
        // UE also compares rooms vertically
        if (r1.maxY <= r2.minY || r1.minY >= r2.maxY)
            return false;
        return super.checkRoomOverlap(r1, r2);
    }

    @Override
    protected void initGameState() {
        // shortcut: UE events/initial NPC state are outside this room preview; port InitNewGame and Event_System to expose them.
        savedEventsNormal = Set.of();
        savedEventsKeter = Set.of();
    }

    // CreateMap's mandatory-room schedule in UE Classic v5.5.4.1 (source order matters).
    @Override
    protected void defineRooms() {
        int minPos = 1, maxPos = room1Amount[0] - 1;
        mapRoom[ROOM1][0] = "room173";
        setRoom("room372", ROOM1, (int) Math.floor(0.1 * room1Amount[0]), minPos, maxPos);
        setRoom("room005", ROOM1, (int) Math.floor(0.3 * room1Amount[0]), minPos, maxPos);
        setRoom("room914", ROOM1, (int) Math.floor(0.35 * room1Amount[0]), minPos, maxPos);
        setRoom("room205", ROOM1, (int) Math.floor(0.5 * room1Amount[0]), minPos, maxPos);
        setRoom("room1archive", ROOM1, (int) Math.floor(0.6 * room1Amount[0]), minPos, maxPos);
        mapRoom[ROOM2C][0] = "lockroom";
        maxPos = room2Amount[0] - 1;
        mapRoom[ROOM2][0] = "room2closets";
        setRoom("room2testroom2", ROOM2, (int) Math.floor(0.1 * room2Amount[0]), minPos, maxPos);
        setRoom("room2posters", ROOM2, (int) Math.floor(0.15 * room2Amount[0]), minPos, maxPos);
        setRoom("room2scps3", ROOM2, (int) Math.floor(0.2 * room2Amount[0]), minPos, maxPos);
        setRoom("room447", ROOM2, (int) Math.floor(0.25 * room2Amount[0]), minPos, maxPos);
        setRoom("room2scps", ROOM2, (int) Math.floor(0.3 * room2Amount[0]), minPos, maxPos);
        setRoom("room2storage", ROOM2, (int) Math.floor(0.35 * room2Amount[0]), minPos, maxPos);
        setRoom("room2gw_b", ROOM2, (int) Math.floor(0.4 * room2Amount[0]), minPos, maxPos);
        setRoom("room2sl", ROOM2, (int) Math.floor(0.5 * room2Amount[0]), minPos, maxPos);
        setRoom("room012", ROOM2, (int) Math.floor(0.55 * room2Amount[0]), minPos, maxPos);
        setRoom("room2scps2", ROOM2, (int) Math.floor(0.6 * room2Amount[0]), minPos, maxPos);
        setRoom("medibay2", ROOM2, (int) Math.floor(0.7 * room2Amount[0]), minPos, maxPos);
        setRoom("room1123", ROOM2, (int) Math.floor(0.75 * room2Amount[0]), minPos, maxPos);
        setRoom("room2elevator", ROOM2, (int) Math.floor(0.85 * room2Amount[0]), minPos, maxPos);
        mapRoom[ROOM3][(int) Math.floor(rng.bbRnd(0.2f, 0.8f) * room3Amount[0])] = "room3storage";
        mapRoom[ROOM3][(int) Math.floor(0.9 * room3Amount[0])] = "room3scps";
        mapRoom[ROOM2C][(int) Math.floor(0.2 * room2cAmount[0])] = "room066";
        mapRoom[ROOM2C][(int) Math.floor(0.5 * room2cAmount[0])] = "room1162";
        mapRoom[ROOM4][(int) Math.floor(0.3 * room4Amount[0])] = "room4info";

        minPos = room1Amount[0]; maxPos = minPos + room1Amount[1] - 1;
        setRoom("room079", ROOM1, minPos + (int) Math.floor(0.15 * room1Amount[1]), minPos, maxPos);
        setRoom("room106", ROOM1, minPos + (int) Math.floor(0.2 * room1Amount[1]), minPos, maxPos);
        setRoom("room096", ROOM1, minPos + (int) Math.floor(0.3 * room1Amount[1]), minPos, maxPos);
        setRoom("room035", ROOM1, minPos + (int) Math.floor(0.5 * room1Amount[1]), minPos, maxPos);
        setRoom("room895", ROOM1, minPos + (int) Math.floor(0.6 * room1Amount[1]), minPos, maxPos);
        minPos = room2Amount[0]; maxPos = minPos + room2Amount[1] - 1;
        mapRoom[ROOM2][minPos + (int) Math.floor(0.15 * room2Amount[1])] = "room409";
        mapRoom[ROOM2][minPos + (int) Math.floor(0.3 * room2Amount[1])] = "room2nuke";
        setRoom("room2tunnel", ROOM2, minPos + (int) Math.floor(0.4 * room2Amount[1]), minPos, maxPos);
        setRoom("room049", ROOM2, minPos + (int) Math.floor(0.45 * room2Amount[1]), minPos, maxPos);
        setRoom("room008", ROOM2, minPos + (int) Math.floor(0.5 * room2Amount[1]), minPos, maxPos);
        setRoom("room2shaft", ROOM2, minPos + (int) Math.floor(0.6 * room2Amount[1]), minPos, maxPos);
        setRoom("room2testroom", ROOM2, minPos + (int) Math.floor(0.7 * room2Amount[1]), minPos, maxPos);
        setRoom("room2servers", ROOM2, minPos + (int) Math.floor(0.9 * room2Amount[1]), minPos, maxPos);
        mapRoom[ROOM2][minPos + (int) Math.floor(0.8 * room2Amount[1])] = "room457";
        mapRoom[ROOM3][room3Amount[0] + (int) Math.floor(0.3 * room3Amount[1])] = "room513";
        mapRoom[ROOM3][room3Amount[0] + (int) Math.floor(0.4 * room3Amount[1])] = "room009";
        mapRoom[ROOM3][room3Amount[0] + (int) Math.floor(0.6 * room3Amount[1])] = "room966";
        mapRoom[ROOM2C][room2cAmount[0] + (int) Math.floor(0.2 * room2cAmount[1])] = "room650";
        mapRoom[ROOM2C][room2cAmount[0] + (int) Math.floor(0.64 * room2cAmount[1])] = "room2cpit";

        int totalRoom1 = room1Amount[0] + room1Amount[1] + room1Amount[2];
        mapRoom[ROOM1][totalRoom1 - 3] = "gateb";
        mapRoom[ROOM1][totalRoom1 - 2] = "gateaentrance";
        mapRoom[ROOM1][totalRoom1 - 1] = "room1office";
        mapRoom[ROOM1][room1Amount[0] + room1Amount[1]] = "room1lifts";
        minPos = room2Amount[0] + room2Amount[1]; maxPos = minPos + room2Amount[2] - 1;
        mapRoom[ROOM2][minPos + (int) Math.floor(0.1 * room2Amount[2])] = "room2poffices";
        setRoom("room2cafeteria", ROOM2, minPos + (int) Math.floor(0.2 * room2Amount[2]), minPos, maxPos);
        setRoom("room2sroom", ROOM2, minPos + (int) Math.floor(0.35 * room2Amount[2]), minPos, maxPos);
        mapRoom[ROOM2][minPos + (int) Math.floor(0.35 * room2Amount[2])] = "room2bio";
        setRoom("room2servers2", ROOM2, minPos + (int) Math.floor(0.4 * room2Amount[2]), minPos, maxPos);
        setRoom("room2offices", ROOM2, minPos + (int) Math.floor(0.45 * room2Amount[2]), minPos, maxPos);
        setRoom("room2offices4", ROOM2, minPos + (int) Math.floor(0.5 * room2Amount[2]), minPos, maxPos);
        mapRoom[ROOM2][minPos + (int) Math.floor(0.55 * room2Amount[2])] = "room2offices5";
        setRoom("room860", ROOM2, minPos + (int) Math.floor(0.6 * room2Amount[2]), minPos, maxPos);
        setRoom("medibay", ROOM2, minPos + (int) Math.floor(0.7 * room2Amount[2]), minPos, maxPos);
        setRoom("room2poffices2", ROOM2, minPos + (int) Math.floor(0.8 * room2Amount[2]), minPos, maxPos);
        setRoom("room2offices2", ROOM2, minPos + (int) Math.floor(0.9 * room2Amount[2]), minPos, maxPos);
        mapRoom[ROOM2C][room2cAmount[0] + room2cAmount[1]] = "room2ccont";
        mapRoom[ROOM2C][room2cAmount[0] + room2cAmount[1] + 1] = "lockroom2";
        int base = room3Amount[0] + room3Amount[1];
        mapRoom[ROOM3][base + (int) Math.floor(0.3 * room3Amount[2])] = "room3servers";
        mapRoom[ROOM3][base + (int) Math.floor(0.5 * room3Amount[2])] = "room3servers2";
        mapRoom[ROOM3][base + (int) Math.floor(0.7 * room3Amount[2])] = "room3offices";
        mapRoom[ROOM3][base + (int) Math.floor(0.9 * room3Amount[2])] = "room3offices2";
    }
}
