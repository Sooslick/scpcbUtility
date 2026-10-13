package ru.sooslick.scpcb;

import ru.sooslick.scpcb.map.Map;

import java.util.HashSet;

/** Run with -ea from the repository root, like MapJsonVerifier. */
public class UeClassicVerifier {
    public static void main(String[] args) throws Exception {
        Map map = SeedGenerator.generateUeMap("446456054", 1);
        assert map.savedRooms.stream().anyMatch(r -> r.roomTemplate.name.equals("room173")) : "UE start room missing";
        assert map.savedRooms.stream().anyMatch(r -> r.roomTemplate.name.equals("room914")) : "UE SCP-914 missing";
        assert map.savedRooms.stream().noneMatch(r -> r.roomTemplate.name.equals("start")) : "Vanilla rooms leaked into UE";
        assert map.savedRooms.stream().anyMatch(r -> r.roomTemplate.name.equals("room009") && r.roomTemplate.shape == Map.ROOM3);
        Map transitionMap = SeedGenerator.generateUeMap("review19", 1);
        var transitionRoom = transitionMap.savedRooms.stream().filter(r -> r.x == 32 && r.z == 48).findFirst().orElseThrow();
        assert transitionRoom.adjDoorBottom == null || transitionRoom.adjDoorBottom.getJsonValue() != 2
                : "UE row 6 uses heavy doors, which cannot auto-close";
        String[] seeds = {"446456054", "990066099", "n790", "bmu23i0", "x9mc", "2001011999", "K", "$", " ", "a=b&c+d", "000000000000000", "abcdefghijklmno"};
        for (String seed : seeds) {
            for (int location = 1; location <= 3; location++) {
                Map first = SeedGenerator.generateUeMap(seed, location);
                String snapshot = snapshot(first);
                SeedGenerator.generateMap("n790");
                Map second = SeedGenerator.generateUeMap(seed, location);
                assert snapshot.equals(snapshot(second)) : "Non-deterministic UE map: " + seed;
                HashSet<String> coords = new HashSet<>();
                for (var room : first.savedRooms) {
                    assert room.roomTemplate.ultimate;
                    assert room.roomTemplate.description != null;
                    assert room.x >= 0 && room.x < Map.MAP_WIDTH * 8;
                    assert room.z >= 0 && room.z < Map.MAP_HEIGHT * 8;
                    assert room.angle >= 0 && room.angle < 360 && room.angle % 90 == 0;
                    assert room.roomTemplate.shape >= 1 && room.roomTemplate.shape <= 5;
                    assert coords.add(room.x + ":" + room.z) : "Duplicate room coordinates";
                    assert room.linkedEventNormal == null && room.linkedEventKeter == null : "Vanilla events leaked into UE";
                }
            }
        }
        assert !snapshot(SeedGenerator.generateUeMap("446456054", 1)).equals(snapshot(SeedGenerator.generateUeMap("446456054", 3))) : "SCP-005 choice must influence the room RNG";
        try {
            SeedGenerator.generateUeMap("test", 4);
            throw new AssertionError("Invalid SCP-005 location accepted");
        } catch (IllegalArgumentException expected) {
            // Invalid pre-seed configuration must fail before generation.
        }
        System.out.println("UE Classic verification passed: 36 seed/location combinations, repeated with vanilla generation between them");
    }

    private static String snapshot(Map map) {
        StringBuilder out = new StringBuilder();
        for (var r : map.savedRooms) {
            out.append(r.roomTemplate.name).append(':').append(r.x).append(':').append(r.z).append(':').append(r.angle);
            out.append(':').append(r.adjDoorRight == null ? "-" : r.adjDoorRight.getJsonValue());
            out.append(':').append(r.adjDoorBottom == null ? "-" : r.adjDoorBottom.getJsonValue());
            out.append(':').append(r.rndInfo).append(';');
        }
        return out.toString();
    }
}
