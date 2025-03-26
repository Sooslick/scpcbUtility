package ru.sooslick.scpcb.map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.sooslick.scpcb.map.Map.ROOM1;
import static ru.sooslick.scpcb.map.Map.ROOM2;
import static ru.sooslick.scpcb.map.Map.ROOM2C;
import static ru.sooslick.scpcb.map.Map.ROOM3;
import static ru.sooslick.scpcb.map.Map.ROOM4;

public class ScpcbRoomTemplate {
    private static final String ROOMS_INI = "scpcbFiles/rooms.ini";
    private static final Map<String, Map<String, String>> roomsIni = readIniFile();

    public static final List<ScpcbRoomTemplate> roomTemplates = new LinkedList<>();

    private String meshPath;
    public String name;
    public int shape;
    public int[] zone = new int[5];
    public int commonness;
    public boolean disableDecals;
    public boolean disableOverlapCheck;

    private float minX, minY, minZ;
    private float maxX, maxY, maxZ;
    public MeshExtents extents;

    public int lightsAmount = 0;

    static {
        // Function LoadRoomTemplates
        roomsIni.forEach((k, vals) -> {
            String strTemp = vals.get("mesh path");
            ScpcbRoomTemplate rt = createRoomTemplate(k.toLowerCase(), strTemp, getIniBool(k, "disableoverlapcheck"));

            strTemp = vals.get("shape");
            if (strTemp != null)
                strTemp = strTemp.toLowerCase();
            else
                strTemp = "";

            switch (strTemp) {
                case "room1":
                case "1":
                    rt.shape = ROOM1;
                    break;
                case "room2":
                case "2":
                    rt.shape = ROOM2;
                    break;
                case "room2c":
                case "2c":
                    rt.shape = ROOM2C;
                    break;
                case "room3":
                case "3":
                    rt.shape = ROOM3;
                    break;
                case "room4":
                case "4":
                    rt.shape = ROOM4;
                    break;
            }

            for (int i = 0; i <= 4; i++) {
                rt.zone[i] = getIniInt(k, "zone" + (i + 1));
            }

            rt.commonness = getIniInt(k, "commonness");
            rt.disableDecals = getIniBool(k, "disabledecals");

            roomTemplates.add(rt);
        });
    }

    public static ScpcbRoomTemplate findByName(String name) {
        return roomTemplates.stream()
                .filter(rt -> rt.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    private static ScpcbRoomTemplate createRoomTemplate(String name, String meshpath, boolean disableOverlapCheck) {
        ScpcbRoomTemplate rt = new ScpcbRoomTemplate();
        rt.name = name;
        rt.meshPath = meshpath;
        rt.disableOverlapCheck = disableOverlapCheck;

        rt.loadRMesh();
        return rt;
    }

    private static Map<String, Map<String, String>> readIniFile() {
        try {
            List<String> lines = Files.lines(Paths.get(ROOMS_INI))
                    .map(String::trim)
                    .filter(line -> !line.startsWith(";"))
                    .collect(Collectors.toList());
            Map<String, Map<String, String>> result = new LinkedHashMap<>();
            String sectionName = "unknown";
            Map<String, String> section = new LinkedHashMap<>();
            for (String line : lines) {
                if (line.trim().isEmpty())
                    continue;
                if (line.startsWith("[")) {
                    if (section.size() > 0)
                        result.put(sectionName, section);
                    sectionName = line.substring(1, line.length() - 1);
                    section = new HashMap<>();
                } else {
                    String[] kv = line.split("=", 2);
                    section.put(kv[0].trim(), kv[1].trim());
                }
            }
            if (section.size() > 0)
                result.put(sectionName, section);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("error parsing ini file " + ROOMS_INI, e);
        }
    }

    private static int getIniInt(String sectionName, String key) {
        Map<String, String> section = roomsIni.get(sectionName);
        if (section == null)
            return 0;
        String value = section.get(key);
        if (value == null)
            return 0;
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean getIniBool(String sectionName, String key) {
        Map<String, String> section = roomsIni.get(sectionName);
        if (section == null)
            return false;
        String value = section.get(key);
        if (value == null)
            return false;
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return false;
        }
    }

    private void loadRMesh() {
        // read the file
        RMeshReader reader = new RMeshReader(meshPath);

        int i, j, k;
        float x, y, z;

        boolean hasTriggerBox = false;
        String isRMesh = reader.readString();
        if (isRMesh.equals("RoomMesh.HasTriggerBox"))
            hasTriggerBox = true;

        int count2;
        int hasTexture;
        int count = reader.readInt();
        for (i = 1; i <= count; i++) {  // drawn mesh

            // mesh stuff
            for (j = 0; j <= 1; j++) {
                hasTexture = reader.readByte();
                if (hasTexture != 0) {
                    reader.readString();
                    // texture stuff
                }
            }

            count2 = reader.readInt();  // vertices

            for (j = 1; j <= count2; j++) {
                // world coords
                x = reader.readFloat();
                y = reader.readFloat();
                z = reader.readFloat();
                addVertex(x, y, z);

                // tex coords
                for (k = 0; k <= 1; k++) {
                    reader.readFloat();
                    reader.readFloat();
                }

                // colors
                reader.readByte();
                reader.readByte();
                reader.readByte();
            }

            count2 = reader.readInt();  // polys
            for (j = 1; j <= count2; j++) {
                reader.readInt();
                reader.readInt();
                reader.readInt();
                // add triangle
            }
        }

        count = reader.readInt();   // invisible collision mesh
        for (i = 1; i <= count; i++) {
            count2 = reader.readInt();
            for (j = 1; j <= count2; j++) { // vertices
                x = reader.readFloat();
                y = reader.readFloat();
                z = reader.readFloat();
                addVertex(x, y, z);
            }

            count2 = reader.readInt();
            for (j = 1; j <= count2; j++) {     // polys
                reader.readInt();
                reader.readInt();
                reader.readInt();
            }
        }

        // trigger boxes
        if (hasTriggerBox) {
//            System.out.println("TriggerBoxEnable");
            int amount = reader.readInt();
            for (int tb = 0; tb < amount; tb++) {
                count = reader.readInt();
                for (i = 1; i <= count; i++) {
                    count2 = reader.readInt();
                    for (j = 1; j <= count2; j++) {
                        x = reader.readFloat();
                        y = reader.readFloat();
                        z = reader.readFloat();
                        addVertex(x, y, z);
                    }
                    count2 = reader.readInt();
                    for (j = 1; j <= count2; j++) {     // polys
                        reader.readInt();
                        reader.readInt();
                        reader.readInt();
                    }
                }
//                System.out.println("Triggerbox: " + reader.readString());
                reader.readString();
            }
        }

        count = reader.readInt();   // point entities
        String pointEntity;
        for (i = 1; i <= count; i++) {
            pointEntity = reader.readString();
            switch (pointEntity) {
                case "screen":
                case "playerstart":
                    reader.readFloat();
                    reader.readFloat();
                    reader.readFloat();
                    reader.readString();
                    break;
                case "waypoint":
                    reader.readFloat();
                    reader.readFloat();
                    reader.readFloat();
                    break;
                case "light":
                    float f1 = reader.readFloat();
                    float f2 = reader.readFloat();
                    float f3 = reader.readFloat();
                    if (f1 != 0 || f2 != 0 || f3 != 0)
                        lightsAmount++;
                    reader.readFloat();
                    reader.readString();
                    reader.readFloat();
                    break;
                case "spotlight":
                    f1 = reader.readFloat();
                    f2 = reader.readFloat();
                    f3 = reader.readFloat();
                    if (f1 != 0 || f2 != 0 || f3 != 0)
                        lightsAmount++;
                    reader.readFloat();
                    reader.readString();
                    reader.readFloat();
                    reader.readString();
                    reader.readInt();
                    reader.readInt();
                    break;
                case "soundemitter":
                    reader.readFloat();
                    reader.readFloat();
                    reader.readFloat();
                    reader.readInt();
                    reader.readFloat();
                    break;
                case "model":
                    String file = reader.readString();
                    if (file.length() > 0) {
                        // position
                        reader.readFloat();
                        reader.readFloat();
                        reader.readFloat();
                        // rotation
                        reader.readFloat();
                        reader.readFloat();
                        reader.readFloat();
                        // scale
                        reader.readFloat();
                        reader.readFloat();
                        reader.readFloat();
                    } else {
//                        System.out.println("file = 0");
                        reader.readFloat();
                        reader.readFloat();
                        reader.readFloat();
                    }
                    break;
            }
        }

        if (!disableOverlapCheck) {
            extents = new MeshExtents(minX, minY, minZ, maxX, maxY, maxZ);
//            System.out.println("room template extents: " + name + " " + extents);
        }
    }

    private void addVertex(float x, float y, float z) {
        if (x < minX)
            minX = x;
        else if (x > maxX)
            maxX = x;

        if (y < minY)
            minY = y;
        else if (y > maxY)
            maxY = y;

        if (z < minZ)
            minZ = z;
        else if (z > maxZ)
            maxZ = z;
    }

}
