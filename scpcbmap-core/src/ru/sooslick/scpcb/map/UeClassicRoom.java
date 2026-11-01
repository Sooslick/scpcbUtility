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
        // the horizontal bounds depend on the last bit of Blitz3D's float math, see BlitzRoomExtents
        float[] bounds = BlitzRoomExtents.calculate(roomTemplate.extents, angle, x, z);
        minX = bounds[0];
        maxX = bounds[1];
        minZ = bounds[2];
        maxZ = bounds[3];
        // UE also compares the rooms vertically; yaw doesn't change the vertical extent
        minY = (float) ((float) (roomTemplate.extents.minY * ROOM_SCALE) + 0.05f);
        maxY = (float) ((float) (roomTemplate.extents.maxY * ROOM_SCALE) - 0.05f);
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
