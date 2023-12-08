package ru.sooslick.scpcb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// todo delete temp class for initial extents verification
public class ExtentsVerifier {
    private static String scpLogPath = "genlog";
    private static String newLogPath = "testme.txt";

    public static void main(String[] args) throws IOException {
        Map<String, MeshExtents> scpExts = new HashMap<>();
        Map<String, MeshExtents> newExts = new HashMap<>();

        Iterator<String> scpLines = Files.lines(Paths.get(scpLogPath))
                .iterator();
        boolean checkRoomName = false;
        MeshExtents exts = null;
        while (scpLines.hasNext()) {
            String line = scpLines.next();
            if (line.contains("roomtemplateextents:")) {
                checkRoomName = true;
                String[] parts = line.replaceAll(",", "").split(" ");
                exts = new MeshExtents(
                        Double.parseDouble(parts[1]),
                        0,
                        Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]),
                        0,
                        Double.parseDouble(parts[6]));
            } else if (checkRoomName) {
                checkRoomName = false;
                if (line.contains(" extents:")) {
                    String name = line.split(" ")[1];
                    scpExts.put(name, exts);
                }
            }
        }

        Files.lines(Paths.get(newLogPath)).forEach(line -> {
            if (line.contains("room template extents:")) {
                String parts[] = line.replaceAll(",", "").split(" ");
                MeshExtents extents = new MeshExtents(
                        Double.parseDouble(parts[4]),
                        0,
                        Double.parseDouble(parts[6]),
                        Double.parseDouble(parts[8]),
                        0,
                        Double.parseDouble(parts[10]));
                newExts.put(parts[3], extents);
            }
        });

        newExts.forEach((name, extents) -> {
            MeshExtents scpTempExts = scpExts.get(name);
            if (scpTempExts == null) {
                System.out.println(name + " -> null");
                return;
            }
            if (extents.minX != scpTempExts.minX)
                System.out.println(name + " minX: " + scpTempExts.minX);
            if (extents.minZ != scpTempExts.minZ)
                System.out.println(name + " minZ: " + scpTempExts.minZ);
            if (extents.maxX != scpTempExts.maxX)
                System.out.println(name + " maxX: " + scpTempExts.maxX);
            if (extents.maxZ != scpTempExts.maxZ)
                System.out.println(name + " maxZ: " + scpTempExts.maxZ);
        });
    }
}
