package ru.sooslick.scpcb.map;

public class MeshExtents {
    public double minX, minY, minZ;
    public double maxX, maxY, maxZ;

    public MeshExtents(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public MeshExtents rotate(int angle) {
        angle%= 360;
        double rads = Math.toRadians(angle);

        double xr = minX * Math.cos(rads) - minZ * Math.sin(rads);
        double zr = minX * Math.sin(rads) + minZ * Math.cos(rads);
        minX = xr;
        minZ = zr;

        xr = maxX * Math.cos(rads) - maxZ * Math.sin(rads);
        zr = maxX * Math.sin(rads) + maxZ * Math.cos(rads);
        maxX = xr;
        maxZ = zr;

        // fix inverted Min/Max values
//        if (minX > maxX) {
//            double a = minX;
//            minX = maxX;
//            maxX = a;
//        }
//        if (minZ > maxZ) {
//            double a = minZ;
//            minZ = maxZ;
//            maxZ = a;
//        }

        return this;
    }

    public MeshExtents scale(double scale) {
        minX*= scale;
        minY*= scale;
        minZ*= scale;
        maxX*= scale;
        maxY*= scale;
        maxZ*= scale;

        return this;
    }

    public MeshExtents copyTransform(double scale, int angle) {
        return new MeshExtents(minX, minY, minZ, maxX, maxY, maxZ)
                .scale(scale)
                .rotate(angle);
    }

    public String toString() {
        return String.format("%s, %s, %s / %s, %s, %s",
                minX, minY, minZ,
                maxX, maxY, maxZ);
    }
}
