package ru.sooslick.scpcb.map;

import ru.sooslick.scpcb.BlitzRandom;

import java.awt.image.BufferedImage;

/**
 * Room of SCP: Containment Breach Ultimate Edition Classic v5.5.4.1.
 * Room data and random draws of FillRoom are in {@link UeClassicRooms}.
 */
final class UeClassicRoom extends ScpcbRoom {
    private final int scp005;

    UeClassicRoom(BlitzRandom rng, ScpcbDoor.DoorFactory doorFactory, int scp005) {
        super(rng, doorFactory);
        this.scp005 = scp005;
    }

    @Override
    protected void fillContents() {
        UeClassicRooms.fill(this, rng, scp005);
        if (roomTemplate.name.startsWith("checkpoint")) shape = Map.ROOM2;
    }

    @Override
    protected void calcBounds() {
        // UE also tracks the vertical bounds, and the whole calculation is done in 32-bit floats like Blitz3D does
        minY = (float) ((float) (roomTemplate.extents.minY * ROOM_SCALE) + 0.05f);
        maxY = (float) ((float) (roomTemplate.extents.maxY * ROOM_SCALE) - 0.05f);
        minX = (float) ((float) extents.minX + 0.05f + (float) x);
        minZ = (float) ((float) extents.minZ + 0.05f + (float) z);
        maxX = (float) ((float) extents.maxX - 0.05f + (float) x);
        maxZ = (float) ((float) extents.maxZ - 0.05f + (float) z);
        // re-implementing BUG from SCP:CB, see ScpcbRoom
        if (minX > maxX) { double temp = minX; minX = maxX; maxX = temp; }
        if (minZ > maxZ) { double temp = minZ; minZ = maxZ; maxZ = temp; }
    }

    @Override
    protected boolean returnsToForwardPath(boolean deviated, int returnChance) {
        // Blitz evaluates both sides of Or; UE must consume the draw even when deviated.
        return chance(returnChance) || deviated;
    }

    @Override
    protected BufferedImage forestHeightmap(int tileType) {
        return UeClassicRooms.HEIGHTMAPS[tileType];
    }
}
