package ru.sooslick.scpcb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.sooslick.scpcb.SeedGenerator.ROOM1;
import static ru.sooslick.scpcb.SeedGenerator.ROOM2;
import static ru.sooslick.scpcb.SeedGenerator.ROOM2C;
import static ru.sooslick.scpcb.SeedGenerator.ROOM3;
import static ru.sooslick.scpcb.SeedGenerator.ROOM4;

public class ScpcbRoomTemplate {
    private static final String file = "rooms.ini";
    private static final Map<String, Map<String, String>> roomsIni = readIniFile();

    public static final List<ScpcbRoomTemplate> roomTemplates = new LinkedList<>();

    private static int roomTempId = 0;

    private String meshPath;
    private int id;
    public String name;
    public int shape;
    public int[] zone = new int[5];
    public int commonness;
    private int large;
    private int disableDecals;
    private int useLightCones;
    private int disableOverlapCheck;

    static {
        // Function LoadRoomTemplates
        roomsIni.forEach((k, vals) -> {
            if ("room ambience".equals(k))
                return;

            String strTemp = vals.get("mesh path");

            ScpcbRoomTemplate rt = createRoomTemplate(strTemp);
            rt.name = k.toLowerCase();

            strTemp = vals.get("shape");
            if (strTemp != null)
                strTemp = strTemp.toLowerCase();
            else
                strTemp = "";

            switch (strTemp) {
                case "room1": case "1":
                    rt.shape = ROOM1;
                    break;
                case "room2": case "2":
                    rt.shape = ROOM2;
                    break;
                case "room2c": case "2c":
                    rt.shape = ROOM2C;
                    break;
                case "room3": case "3":
                    rt.shape = ROOM3;
                    break;
                case "room4": case "4":
                    rt.shape = ROOM4;
                    break;
            }

            for (int i = 0; i <= 4; i++) {
                rt.zone[i] = getIniInt(k,  "zone" + (i + 1));
            }

            rt.commonness = getIniInt(k, "commonness");
            rt.large = getIniInt(k, "large");
            rt.disableDecals = getIniInt(k, "disabledecals");
            rt.useLightCones = getIniInt(k, "usevolumelighting");
            rt.disableOverlapCheck = getIniInt(k, "disableoverlapcheck");

            roomTemplates.add(rt);
        });
    }

    public static ScpcbRoomTemplate findByName(String name) {
        return roomTemplates.stream()
                .filter(rt -> rt.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    private static ScpcbRoomTemplate createRoomTemplate(String meshpath) {
        ScpcbRoomTemplate rt = new ScpcbRoomTemplate();
        rt.meshPath = meshpath;
        rt.id = roomTempId;
        roomTempId++;
        return rt;
    }

    private static Map<String, Map<String, String>> readIniFile() {
        try {
            List<String> lines = Files.lines(Paths.get(file))
                    .map(String::trim)
                    .filter(line -> !line.startsWith(";"))
                    .collect(Collectors.toList());
            Map<String, Map<String, String>> result = new HashMap<>();
            String sectionName = "unknown";
            Map<String, String> section = new HashMap<>();
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
            throw new RuntimeException("error parsing ini file " + file, e);
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
}
