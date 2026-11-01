package ru.sooslick.scpcb.map;

/**
 * Room bounds as the game's CalculateRoomExtents produces them, reproducing Blitz3D's 32-bit floating point math.
 * <p>
 * The game rotates the room entity by RotateEntity(room, 0, angle, 0), transforms the room template's min and max
 * corners with TFormVector, shrinks them by 0.05 and moves them to the room position. Overlap prevention compares the
 * results with {@code <=}, and neighbouring rooms often touch exactly (a room's max meets the next room's min), so the
 * last bit decides whether the rooms overlap. Computing it in Java doubles, or even in floats on top of an exact
 * rotation, gives different answers in about 16% of the cases (measured below).
 * <p>
 * What matters, and what is reproduced here:
 * <ul>
 *     <li>the rotation matrix is built in floats from the RAW angle (it is not reduced modulo 360, rooms may have
 *     been turned to 360..630 degrees during overlap prevention) as a quaternion: {@code w = cos(a/2)},
 *     {@code y = sin(a/2)}, {@code c = 1 - 2yy}, {@code s = 2wy}. For example the matrix of 360 degrees is not the
 *     identity, it has {@code s = 1.7e-7};</li>
 *     <li>TFormVector is {@code x' = c*x + (-s)*z}, {@code z' = s*x + c*z}, rounded to float after every operation;</li>
 *     <li>the bounds are {@code (T + 0.05) + position} for min and {@code (T - 0.05) + position} for max, again
 *     rounded after every operation, left to right;</li>
 *     <li>when min ends up greater than max (rotated rooms), the values are swapped. This is a BUG of the game: it
 *     <i>extends</i> such rooms by the shrink amount instead of shrinking them.</li>
 * </ul>
 * <p>
 * Validation: {@code ExtentsModelVerifier} (test sources) runs this against the bounds dumped from the real
 * SCP:CB 1.3.11 ({@code scpcbFiles/roomdb.dat}): 8 raw angles x 90 rooms x 17 positions x 4 bounds = 48960 values,
 * 0 mismatches. For comparison the plain double math of {@link MeshExtents} mismatches 16.9% of them, and doubles
 * rotated exactly and then rounded to floats mismatch 15.8%. {@code room2cafeteria} is the only exception: its dump
 * disagrees with its mesh by exactly 1/16 even at 0 degrees, which is a data difference and not a float one.
 * <p>
 * 90 and 270 degrees are the only angles where the quaternion formula doesn't reproduce the game: {@code 1 - 2yy}
 * cancels catastrophically at a half angle of 45 degrees, and the game's matrix has {@code c = -2^-23},
 * {@code s = +-(1 + 2^-23)} there. These constants were found by fitting against the dump; they reproduce it exactly,
 * {@code s} has no other solution (its neighbours give none at all) and {@code c} is bound to a window about 3e-9 wide
 * around {@code -2^-23}. The exact sequence of engine operations behind them is not known.
 * <p>
 * Whether the same holds for another game built with Blitz3D (UE Classic) is an assumption: the matrix is the engine's,
 * the shrink-and-move part is the game's own code.
 */
final class BlitzRoomExtents {
    private static final float ROOM_SCALE = (float) ScpcbRoom.ROOM_SCALE;   // 8 / 2048, exact in float
    private static final float DEG_TO_RAD = (float) (Math.PI / 180);
    private static final float SHRINK = 0.05f;
    private static final float ULP_ONE = 0x1p-23f;

    private BlitzRoomExtents() {
    }

    /**
     * Rotation of the room entity about Y, the entries of {@code [c 0 s; 0 1 0; -s 0 c]} as the game's engine has them.
     *
     * @param angle raw (not reduced) angle in degrees
     * @return {c, s}
     */
    static float[] yaw(int angle) {
        // fitted against the real game, see the class description
        if (angle == 90)
            return new float[]{-ULP_ONE, 1 + ULP_ONE};
        if (angle == 270)
            return new float[]{-ULP_ONE, -(1 + ULP_ONE)};

        float half = (angle * DEG_TO_RAD) / 2f;
        float w = (float) Math.cos(half);
        float y = (float) Math.sin(half);
        return new float[]{1 - 2 * (y * y), 2 * (w * y)};
    }

    /**
     * CalculateRoomExtents for the horizontal axes.
     *
     * @param template extents of the room template mesh (unscaled)
     * @param angle    raw room angle in degrees
     * @param x        position of the room
     * @param z        position of the room
     * @return {minX, maxX, minZ, maxZ}
     */
    static float[] calculate(MeshExtents template, int angle, double x, double z) {
        float[] yaw = yaw(angle);
        float c = yaw[0], s = yaw[1];

        float minXs = (float) (template.minX * ROOM_SCALE), minZs = (float) (template.minZ * ROOM_SCALE);
        float maxXs = (float) (template.maxX * ROOM_SCALE), maxZs = (float) (template.maxZ * ROOM_SCALE);

        // TFormVector
        float tMinX = c * minXs + (-s) * minZs;
        float tMinZ = s * minXs + c * minZs;
        float tMaxX = c * maxXs + (-s) * maxZs;
        float tMaxZ = s * maxXs + c * maxZs;

        float minX = (tMinX + SHRINK) + (float) x;
        float minZ = (tMinZ + SHRINK) + (float) z;
        float maxX = (tMaxX - SHRINK) + (float) x;
        float maxZ = (tMaxZ - SHRINK) + (float) z;

        // re-implementing BUG from SCP:CB
        if (minX > maxX) {
            float a = minX;
            minX = maxX;
            maxX = a;
        }
        if (minZ > maxZ) {
            float a = minZ;
            minZ = maxZ;
            maxZ = a;
        }
        return new float[]{minX, maxX, minZ, maxZ};
    }
}
