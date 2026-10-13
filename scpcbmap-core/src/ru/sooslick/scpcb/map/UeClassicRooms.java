package ru.sooslick.scpcb.map;

import ru.sooslick.scpcb.BlitzRandom;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Room data and FillRoom random draws from CBUE Classic v5.5.4.1. */
final class UeClassicRooms {
    static final List<ScpcbRoomTemplate> TEMPLATES = load();
    static final BufferedImage[] HEIGHTMAPS = loadHeightmaps();

    private static BufferedImage[] loadHeightmaps() {
        BufferedImage[] images = new BufferedImage[6];
        String[] names = {"forest1h.png", "forest2h.png", "forest2Ch.png", "forest3h.png", "forest4h.png"};
        try {
            for (int i = 0; i < names.length; i++) images[i + 1] = ImageIO.read(Path.of("scpcbFiles/ue-classic", names[i]).toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load UE forest heightmaps", e);
        }
        return images;
    }

    private static List<ScpcbRoomTemplate> load() {
        try (var lines = Files.lines(Path.of("scpcbFiles/ue-classic/rooms.tsv"))) {
            return lines.filter(line -> !line.startsWith("#") && !line.isBlank()).map(line -> {
                String[] fields = line.split("\t", -1);
                ScpcbRoomTemplate rt = new ScpcbRoomTemplate();
                rt.ultimate = true;
                rt.name = fields[0];
                rt.shape = Integer.parseInt(fields[1]);
                String[] zones = fields[2].split(",");
                for (int i = 0; i < zones.length; i++) rt.zone[i] = Integer.parseInt(zones[i]);
                rt.commonness = Integer.parseInt(fields[3]);
                rt.disableOverlapCheck = Boolean.parseBoolean(fields[4]);
                rt.lightsAmount = Integer.parseInt(fields[5]);
                rt.extents = new MeshExtents(Double.parseDouble(fields[6]), Double.parseDouble(fields[7]),
                        Double.parseDouble(fields[8]), Double.parseDouble(fields[9]),
                        Double.parseDouble(fields[10]), Double.parseDouble(fields[11]));
                rt.description = fields[12];
                return rt;
            }).toList();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Unable to load UE Classic room metadata", e);
        }
    }

    static void fill(ScpcbRoom room, BlitzRandom rng, int scp005) {
        // Doors, item rotations, decals and lights all advance the same Blitz RNG.
        // Keep conditional draws in source order; fixed draws need no 3D entities.
        int draws = switch (room.roomTemplate.name) {
            case "lockroom", "room205", "room2doors", "lockroom3", "gateaentrance",
                    "room066", "room2cpit", "room2offices4", "room1162" -> 2;
            case "gatea", "room2cafeteria", "room1office", "room650" -> 5;
            case "gateb", "room914", "room008" -> 7;
            case "room372", "room966", "room2testroom" -> 3;
            case "room079" -> 7;
            case "checkpoint1", "checkpoint2" -> room.shape == Map.ROOM1 ? 3 : 2;
            case "room2pit", "room2elevator", "endroom", "room3offices", "room4info", "room2offices5" -> 1;
            case "room2testroom2", "room513", "room895", "medibay", "medibay2" -> 4;
            case "room012" -> 5;
            case "room2storage", "room106" -> 10;
            case "room2sroom" -> 6;
            case "room2shaft" -> 8;
            case "room2poffices" -> 11;
            case "room2poffices2" -> 8;
            case "room2servers2" -> 5;
            case "room2nuke" -> 6;
            case "room2tunnel" -> 5;
            case "room035", "room009" -> 9;
            case "room3storage" -> 12;
            case "room457" -> 17;
            case "room049" -> 14;
            case "room2servers" -> 4;
            case "room3servers2", "room2ccont" -> 2;
            case "room2offices" -> 4;
            case "room173" -> 15;
            case "room2scps" -> 56;
            case "room1123" -> 8;
            case "pocketdimension" -> 9;
            case "room2scps2" -> 8;
            case "room2sl" -> 3;
            case "room2posters" -> 5;
            case "room096" -> 8;
            case "room409" -> scp005 == 3 ? 5 : 4;
            case "room2scps3" -> 15;
            case "room005" -> scp005 == 3 ? 2 : 3;
            case "room447" -> 6;
            case "room3scps" -> 53;
            default -> 0;
        };
        draw(rng, draws);
        switch (room.roomTemplate.name) {
            case "room860" -> {
                draw(rng, 4);
                room.genForestGrid();
                draw(rng, 2);
            }
            case "lockroom2" -> draw(rng, 6 * 18);
            case "room3servers" -> {
                draw(rng, 1);
                if (rng.bbRand(1, 2) == 1) draw(rng, 1);
                if (rng.bbRand(1, 2) == 1) draw(rng, 1);
                draw(rng, 1);
            }
            case "room2closets" -> {
                draw(rng, 3);
                if (rng.bbRand(1, 2) == 1) draw(rng, 1);
                if (rng.bbRand(1, 2) == 1) draw(rng, 1);
                draw(rng, 4);
            }
            case "room2offices2" -> draw(rng, 6);
            case "room2offices3" -> {
                draw(rng, 5);
                int items = rng.bbRand(0, 1) + 1;
                draw(rng, items + 1);
                if (rng.bbRand(1, 2) == 1) draw(rng, 1);
                if (rng.bbRand(1, 2) == 1) draw(rng, 1);
                draw(rng, 1);
            }
            case "room2gw" -> draw(rng, 3);
            case "room2gw_b" -> draw(rng, 2);
            case "room1archive" -> {
                for (int x = 0; x < 2; x++) {
                    for (int y = 0; y < 3; y++) {
                        for (int z = 0; z < 3; z++) {
                            int chance = rng.bbRand(-10, 100);
                            if (chance < 0) break;
                            if (chance < 40) rng.bbRand(1, 32);
                            else if (chance < 45) rng.bbRand(0, 2);
                            else if (chance >= 95) rng.bbRand(1, 3);
                            draw(rng, 2);
                        }
                    }
                }
                draw(rng, 1);
            }
        }
    }

    private static void draw(BlitzRandom rng, int count) {
        for (int i = 0; i < count; i++) rng.bbRand(0, 1);
    }
}
