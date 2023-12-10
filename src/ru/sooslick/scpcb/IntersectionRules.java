package ru.sooslick.scpcb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntersectionRules {
    private final static List<IntersectionRules> rules = new LinkedList<>();

    private final RotatedRoom room;
    private final Map<Direction, List<RotatedRoom>> conflicts = new HashMap<>();

    static {
        // todo fill incomplete list of intersection rules
        //  and double check them
        rules.add(new IntersectionRules("roompj", 90)
                .addConflict("room2testroom2", 270, Direction.LEFT)
                .addConflict("room2tesla_lcz", 270, Direction.LEFT)
                .addConflict("room2scps", 270, Direction.LEFT)
                .addConflict("room2storage", 270, Direction.LEFT)
                .addConflict("room2", 270, Direction.LEFT)
                .addConflict("room2_3", 270, Direction.LEFT)
                .addConflict("room2_4", 270, Direction.LEFT)
                .addConflict("room2doors", 270, Direction.LEFT)
                .addConflict("room2sl", 270, Direction.LEFT)
                .addConflict("room012", 270, Direction.LEFT)
                .addConflict("room2scps2", 270, Direction.LEFT)
                .addConflict("room1123", 270, Direction.LEFT)
                .addConflict("room2elevator", 270, Direction.LEFT)
        );
        rules.add(new IntersectionRules("room2testroom2", 270)
                .addConflict("room2sl", 90, Direction.RIGHT)
                .addConflict("room2scps", 90, Direction.RIGHT)
                .addConflict("room2_5", 90, Direction.RIGHT)
                .addConflict("room2_3", 90, Direction.RIGHT)
                .addConflict("room2doors", 90, Direction.RIGHT)
                .addConflict("room012", 90, Direction.RIGHT)
                .addConflict("room2scps2", 90, Direction.RIGHT)
                .addConflict("room1123", 90, Direction.RIGHT)
                .addConflict("room2_4", 90, Direction.RIGHT)
                .addConflict("room2elevator", 90, Direction.RIGHT)
                .addConflict("room2", 90, Direction.RIGHT)
                .addConflict("tunnel", 90, Direction.RIGHT)
                .addConflict("room2sroom", 90, Direction.RIGHT)
                .addConflict("room2offices4", 90, Direction.RIGHT)
                .addConflict("room860", 90, Direction.RIGHT)
                .addConflict("room2z3", 90, Direction.RIGHT)
                .addConflict("room2poffices", 90, Direction.RIGHT)
                .addConflict("room2tesla", 90, Direction.RIGHT)
                .addConflict("room2offices", 90, Direction.RIGHT)
                .addConflict("room2poffices2", 90, Direction.RIGHT)
                .addConflict("room2toilets", 90, Direction.RIGHT)
                .addConflict("room2offices2", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2testroom2", 90)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room2scps", 270, Direction.LEFT)
                .addConflict("914", 180, Direction.TOP)
                .addConflict("room1archive", 180, Direction.TOP)
                .addConflict("room205", 180, Direction.TOP)
                .addConflict("room1162", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room3_2", 0)
                .addConflict("room012", 90, Direction.RIGHT)
                .addConflict("room513", 90, Direction.RIGHT)
                .addConflict("room3tunnel", 90, Direction.RIGHT)
                .addConflict("room3pit", 90, Direction.RIGHT)
                .addConflict("room106", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2tesla_lcz", 90)
                .addConflict("room2testroom2", 270, Direction.LEFT)
                .addConflict("room2elevator", 270, Direction.RIGHT)
                .addConflict("room1162", 0, Direction.RIGHT)
                .addConflict("room2z3", 270, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2storage", 90)
                .addConflict("room2testroom2", 270, Direction.LEFT)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room2scps", 270, Direction.LEFT)
                .addConflict("room1162", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2sl", 90)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room2scps", 270, Direction.LEFT)
                .addConflict("room012", 270, Direction.LEFT)
                .addConflict("room1162", 0, Direction.RIGHT)
                .addConflict("room2c2", 0, Direction.RIGHT)
                .addConflict("room3tunnel", 90, Direction.RIGHT)
                .addConflict("testroom", 0, Direction.RIGHT)
                .addConflict("room3z3", 90, Direction.RIGHT)
                .addConflict("room2offices", 0, Direction.RIGHT)
                .addConflict("room2offices", 360, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2sl", 270)
                .addConflict("room012", 90, Direction.LEFT)
                .addConflict("room2testroom2", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2scps", 90)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room2scps2", 270, Direction.LEFT)
                .addConflict("room1162", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2_5", 90)
                .addConflict("room1162", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2_3", 90)
                .addConflict("room1162", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2gw_b", 90)
                .addConflict("room1162", 0, Direction.RIGHT)
                .addConflict("room3z2", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2doors", 90)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room2scps", 270, Direction.LEFT)
                .addConflict("room1162", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room012", 90)
                .addConflict("room2scps", 270, Direction.LEFT)
                .addConflict("room2scps2", 270, Direction.LEFT)
                .addConflict("room1162", 0, Direction.RIGHT)
                .addConflict("room860", 270, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room1123", 90)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room2scps", 270, Direction.LEFT)
                .addConflict("room1162", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2_4", 90)
                .addConflict("room1162", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2elevator", 90)
                .addConflict("room1162", 0, Direction.RIGHT)
                .addConflict("room2z3", 270, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2", 90)
                .addConflict("room1162", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room513", 270)
                .addConflict("testroom", 0, Direction.BOTTOM)
                .addConflict("tunnel", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room3pit", 270)
                .addConflict("testroom", 0, Direction.BOTTOM)
                .addConflict("tunnel", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("tunnel", 180)
                .addConflict("room3z2", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room2servers", 180)
                .addConflict("room3z2", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room2servers", 360)
                .addConflict("room3tunnel", 270, Direction.TOP)
        );
        rules.add(new IntersectionRules("room106", 270)
                .addConflict("room513", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("008", 90)
                .addConflict("room3_3", 0, Direction.LEFT)
                .addConflict("room3pit", 0, Direction.LEFT)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room4pit", 0, Direction.LEFT)
                .addConflict("room4tunnels", 0, Direction.LEFT)
        );
        rules.add(new IntersectionRules("room035", 90)
                .addConflict("room3_3", 0, Direction.LEFT)
                .addConflict("room3pit", 0, Direction.LEFT)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room4pit", 0, Direction.LEFT)
        );
        rules.add(new IntersectionRules("coffin", 90)
                .addConflict("room3_3", 0, Direction.LEFT)
                .addConflict("room3pit", 0, Direction.LEFT)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room4pit", 0, Direction.LEFT)
        );
        rules.add(new IntersectionRules("room3tunnel", 270)
                .addConflict("testroom", 0, Direction.BOTTOM)
                .addConflict("tunnel", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room3tunnel", 0)
                .addConflict("tunnel", 90, Direction.RIGHT)
                .addConflict("room2sroom", 90, Direction.RIGHT)
                .addConflict("room2offices4", 90, Direction.RIGHT)
                .addConflict("room860", 90, Direction.RIGHT)
                .addConflict("room2z3", 90, Direction.RIGHT)
                .addConflict("room2poffices", 90, Direction.RIGHT)
                .addConflict("room2tesla", 90, Direction.RIGHT)
                .addConflict("room2offices", 90, Direction.RIGHT)
                .addConflict("room2poffices2", 90, Direction.RIGHT)
                .addConflict("room2toilets", 90, Direction.RIGHT)
                .addConflict("room2offices2", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room3z2", 270)
                .addConflict("testroom", 0, Direction.BOTTOM)
                .addConflict("tunnel", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("008", 270)
                .addConflict("room513", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room035", 270)
                .addConflict("room513", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room3z3", 90)
                .addConflict("room2offices", 180, Direction.BOTTOM)
                .addConflict("tunnel", 90, Direction.LEFT)
                .addConflict("endroom", 180, Direction.TOP)
                .addConflict("room1lifts", 180, Direction.TOP)
        );
        rules.add(new IntersectionRules("room2poffices", 270)
                .addConflict("room106", 90, Direction.LEFT)
        );
        rules.add(new IntersectionRules("room2z3", 270)
                .addConflict("room2pit", 90, Direction.LEFT)
                .addConflict("room2gw_b", 90, Direction.LEFT)
                .addConflict("room2sroom", 90, Direction.LEFT)
                .addConflict("room2z3_2", 90, Direction.LEFT)
        );
        rules.add(new IntersectionRules("room2offices", 0)
                .addConflict("lockroom2", 270, Direction.BOTTOM)
                .addConflict("tunnel", 90, Direction.LEFT)
                .addConflict("room2z3", 90, Direction.LEFT)
                .addConflict("room3z3", 180, Direction.TOP)
        );
        rules.add(new IntersectionRules("room2cafeteria", 0)
                .addConflict("room2servers2", 180, Direction.BOTTOM)
                .addConflict("lockroom2", 270, Direction.BOTTOM)
                .addConflict("room2cz3", 270, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room3servers", 0)
                .addConflict("room2z3", 90, Direction.LEFT)
                .addConflict("endroom", 180, Direction.TOP)
                .addConflict("room860", 90, Direction.RIGHT)
                .addConflict("room2ccont", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room3offices", 0)
                .addConflict("room2z3", 90, Direction.LEFT)
                .addConflict("room2ccont", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2sroom", 270)
                .addConflict("room3offices", 180, Direction.RIGHT)
                .addConflict("room3z3", 180, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2sroom", 90)
                .addConflict("room3_3", 0, Direction.LEFT)
                .addConflict("room3pit", 0, Direction.LEFT)
                .addConflict("room3tunnel", 270, Direction.LEFT)
                .addConflict("room2_3", 270, Direction.LEFT)
                .addConflict("room1123", 270, Direction.LEFT)
                .addConflict("room2tesla_hcz", 270, Direction.LEFT)
                .addConflict("room4info", 0, Direction.LEFT)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room4pit", 0, Direction.LEFT)
        );
        rules.add(new IntersectionRules("endroom", 180)
                .addConflict("room3servers2", 0, Direction.BOTTOM)
                .addConflict("medibay", 90, Direction.BOTTOM)
                .addConflict("room2offices2", 90, Direction.BOTTOM)
                .addConflict("room3z3", 0, Direction.BOTTOM)
                .addConflict("room2offices", 360, Direction.BOTTOM)
                .addConflict("room2offices4", 0, Direction.BOTTOM)
                .addConflict("room2cz3", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room2offices4", 90)
                .addConflict("room3_3", 0, Direction.LEFT)
                .addConflict("room3pit", 0, Direction.LEFT)
                .addConflict("room3tunnel", 270, Direction.LEFT)
                .addConflict("room4z3", 0, Direction.LEFT)
                .addConflict("room2_3", 270, Direction.LEFT)
                .addConflict("room1123", 270, Direction.LEFT)
                .addConflict("room2tesla_hcz", 270, Direction.LEFT)
                .addConflict("room4info", 0, Direction.LEFT)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room4pit", 0, Direction.LEFT)
                .addConflict("room3z3", 0, Direction.RIGHT)
                .addConflict("room2tesla", 270, Direction.RIGHT)
                .addConflict("room2z3", 270, Direction.RIGHT)
                .addConflict("room2cz3", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2z3", 0)
                .addConflict("lockroom2", 270, Direction.BOTTOM)
                .addConflict("room3z3", 180, Direction.TOP)
        );
        rules.add(new IntersectionRules("room2tesla", 0)
                .addConflict("lockroom2", 270, Direction.BOTTOM)
                .addConflict("room3z3", 180, Direction.TOP)
        );
        rules.add(new IntersectionRules("room3z3", 0)
                .addConflict("room2z3", 90, Direction.LEFT)
                .addConflict("room860", 90, Direction.RIGHT)
                .addConflict("room2ccont", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room3z3", 180)
                .addConflict("room2offices", 360, Direction.BOTTOM)
                .addConflict("room2offices4", 0, Direction.BOTTOM)
                .addConflict("room2offices4", 360, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room860", 90)
                .addConflict("room4z3", 0, Direction.LEFT)
                .addConflict("room3servers2", 0, Direction.LEFT)
                .addConflict("room3gw", 0, Direction.LEFT)
                .addConflict("room2cz3", 0, Direction.RIGHT)
                .addConflict("room2z3", 270, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room860", 270)
                .addConflict("room513", 90, Direction.LEFT)
                .addConflict("medibay", 90, Direction.LEFT)
                .addConflict("room3z3", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2poffices", 90)
                .addConflict("room4z3", 0, Direction.LEFT)
                .addConflict("room2_3", 270, Direction.LEFT)
                .addConflict("room1123", 270, Direction.LEFT)
                .addConflict("room2tesla_hcz", 270, Direction.LEFT)
                .addConflict("room3_3", 0, Direction.LEFT)
                .addConflict("room3pit", 0, Direction.LEFT)
                .addConflict("room3tunnel", 270, Direction.LEFT)
                .addConflict("room4info", 0, Direction.LEFT)
        );
        rules.add(new IntersectionRules("room2cafeteria", 90)
                .addConflict("room2offices", 360, Direction.RIGHT)
                .addConflict("room2tesla", 270, Direction.RIGHT)
                .addConflict("room2z3", 270, Direction.RIGHT)
                .addConflict("room2cz3", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2tesla", 90)
                .addConflict("room4z3", 0, Direction.LEFT)
                .addConflict("room2cz3", 0, Direction.RIGHT)
                .addConflict("room2z3", 270, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2z3", 90)
                .addConflict("room4z3", 0, Direction.LEFT)
                .addConflict("room3servers2", 0, Direction.RIGHT)
                .addConflict("room2tesla", 270, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2offices", 90)
                .addConflict("room2servers2", 180, Direction.BOTTOM)
                .addConflict("room4z3", 0, Direction.LEFT)
                .addConflict("room2_3", 270, Direction.LEFT)
                .addConflict("room1123", 270, Direction.LEFT)
                .addConflict("room2tesla_hcz", 270, Direction.LEFT)
                .addConflict("room3_3", 0, Direction.LEFT)
                .addConflict("room3pit", 0, Direction.LEFT)
                .addConflict("room3tunnel", 270, Direction.LEFT)
                .addConflict("room4info", 0, Direction.LEFT)
                .addConflict("room2tesla", 270, Direction.RIGHT)
                .addConflict("room2cz3", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2poffices2", 90)
                .addConflict("room4z3", 0, Direction.LEFT)
                .addConflict("room2_3", 270, Direction.LEFT)
                .addConflict("room1123", 270, Direction.LEFT)
                .addConflict("room2tesla_hcz", 270, Direction.LEFT)
                .addConflict("room3_3", 0, Direction.LEFT)
                .addConflict("room3pit", 0, Direction.LEFT)
                .addConflict("room3tunnel", 270, Direction.LEFT)
                .addConflict("room2closets", 270, Direction.LEFT)
                .addConflict("room4info", 0, Direction.LEFT)
                .addConflict("room3_2", 0, Direction.LEFT)
                .addConflict("room4pit", 0, Direction.LEFT)
                .addConflict("room2tesla", 270, Direction.RIGHT)
                .addConflict("room2cz3", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2toilets", 90)
                .addConflict("room4z3", 0, Direction.LEFT)
                .addConflict("room2tesla", 270, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2offices2", 90)
                .addConflict("room4z3", 0, Direction.LEFT)
                .addConflict("room2_3", 270, Direction.LEFT)
                .addConflict("room1123", 270, Direction.LEFT)
                .addConflict("room2tesla_hcz", 270, Direction.LEFT)
                .addConflict("room3_3", 0, Direction.LEFT)
                .addConflict("room3pit", 0, Direction.LEFT)
                .addConflict("room3tunnel", 270, Direction.LEFT)
                .addConflict("room4info", 0, Direction.LEFT)
                .addConflict("room2z3", 270, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room3servers2", 180)
                .addConflict("room2offices", 360, Direction.BOTTOM)
                .addConflict("room2offices4", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("medibay", 270)
                .addConflict("room2offices", 360, Direction.BOTTOM)
                .addConflict("room2offices4", 0, Direction.BOTTOM)
                .addConflict("checkpoint2", 0, Direction.BOTTOM)
                .addConflict("room2z3", 90, Direction.LEFT)
        );
        rules.add(new IntersectionRules("room2offices2", 270)
                .addConflict("room2offices", 360, Direction.BOTTOM)
                .addConflict("room2offices4", 0, Direction.BOTTOM)
                .addConflict("checkpoint2", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room2servers2", 90)
                .addConflict("room2tesla", 270, Direction.RIGHT)
                .addConflict("room2z3", 270, Direction.RIGHT)
                .addConflict("room2cz3", 0, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room3servers", 180)
                .addConflict("room2offices", 360, Direction.BOTTOM)
                .addConflict("room2offices4", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room1lifts", 180)
                .addConflict("room3servers2", 0, Direction.BOTTOM)
                .addConflict("room2offices", 360, Direction.BOTTOM)
                .addConflict("room2offices4", 0, Direction.BOTTOM)
                .addConflict("room2cz3", 0, Direction.BOTTOM)
                .addConflict("room2offices4", 180, Direction.TOP)
                .addConflict("room2poffices2", 180, Direction.TOP)
                .addConflict("room2offices2", 180, Direction.TOP)
        );
        rules.add(new IntersectionRules("room106", 90)
                .addConflict("room4pit", 0, Direction.LEFT)
        );
        rules.add(new IntersectionRules("lockroom2", 90)
                .addConflict("room3", 0, Direction.LEFT)
                .addConflict("room3z3", 0, Direction.LEFT)
        );
        rules.add(new IntersectionRules("room2cz3", 90)
                .addConflict("room3", 0, Direction.LEFT)
                .addConflict("room3z3", 0, Direction.LEFT)
        );
        rules.add(new IntersectionRules("room3gw", 0)
                .addConflict("room2ccont", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room3servers2", 0)
                .addConflict("room2ccont", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("lockroom2", 270)
                .addConflict("room2z3_2", 0, Direction.TOP)
                .addConflict("room2sroom", 0, Direction.TOP)
                .addConflict("room2servers2", 0, Direction.TOP)
                .addConflict("room2offices4", 0, Direction.TOP)
                .addConflict("room860", 0, Direction.TOP)
                .addConflict("medibay", 0, Direction.TOP)
                .addConflict("room2poffices2", 0, Direction.TOP)
                .addConflict("room2offices2", 0, Direction.TOP)
        );
        rules.add(new IntersectionRules("room2cz3", 270)
                .addConflict("room2z3_2", 0, Direction.TOP)
                .addConflict("room2sroom", 0, Direction.TOP)
        );
        rules.add(new IntersectionRules("medibay", 0)
                .addConflict("room2ccont", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2offices2", 0)
                .addConflict("room3z3", 180, Direction.TOP)
                .addConflict("room2ccont", 90, Direction.RIGHT)
        );
        rules.add(new IntersectionRules("room2offices4", 180)
                .addConflict("room2z3_2", 0, Direction.BOTTOM)
                .addConflict("room2sroom", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room2poffices2", 180)
                .addConflict("room2z3_2", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("room2offices2", 180)
                .addConflict("room2z3_2", 0, Direction.BOTTOM)
        );
        rules.add(new IntersectionRules("medibay", 90)
                .addConflict("room2cz3", 0, Direction.RIGHT)
                .addConflict("room2z3", 270, Direction.RIGHT)
        );
    }

    public static boolean hasRule(String r1, int a1, String r2, int a2, Direction relation) {
        boolean found;
        // first, filter conflicts by room1 with angle1 and check them
        Optional<List<RotatedRoom>> conflicts = rules.stream()
                .filter(rule -> rule.room.name.equals(r1))
                .filter(rule -> rule.room.baseAngle == a1)
                .map(rule -> rule.conflicts.get(relation))
                .filter(Objects::nonNull)
                .findFirst();
        if (conflicts.isPresent()) {
            found = conflicts.get().stream()
                    .filter(room -> room.name.equals(r2))
                    .anyMatch(room -> room.baseAngle == a2);
            if (found)
                return true;
        }

        // then, mirror relationship and filter conflicts by room2 with angle2
        Direction mirrored = relation.mirror();
        conflicts = rules.stream()
                .filter(rule -> rule.room.name.equals(r2))
                .filter(rule -> rule.room.baseAngle == a2)
                .map(rule -> rule.conflicts.get(mirrored))
                .filter(Objects::nonNull)
                .findFirst();
        return conflicts.map(rotatedRooms -> rotatedRooms.stream()
                        .filter(room -> room.name.equals(r1))
                        .anyMatch(room -> room.baseAngle == a1))
                .orElse(false);
    }

    private static void addRule(String r1, int a1, String r2, int a2, Direction direction) {
        Optional<IntersectionRules> probablyRule = findRoomRule(r1, a1);
        if (probablyRule.isPresent()) {
            probablyRule.get().addConflict(r2, a2, direction);
            return;
        }

        probablyRule = findRoomRule(r2, a2);
        if (probablyRule.isPresent()) {
            probablyRule.get().addConflict(r1, a1, direction.mirror());
            return;
        }

        rules.add(new IntersectionRules(r1, a1)
                .addConflict(r2, a2, direction));
    }

    private static Optional<IntersectionRules> findRoomRule(String roomName, int baseAngle) {
        return rules.stream()
                .filter(rule -> rule.room.name.equals(roomName))
                .filter(rule -> rule.room.baseAngle == baseAngle)
                .findFirst();
    }

    /////////////////////////

    private IntersectionRules(String room, int baseAngle) {
        this.room = new RotatedRoom(room, baseAngle);
    }

    private IntersectionRules addConflict(String room, int baseAngle, Direction relation) {
        conflicts.computeIfAbsent(relation, rel -> new LinkedList<>())
                .add(new RotatedRoom(room, baseAngle));
        return this;
    }

    private static class RotatedRoom {
        private final String name;
        private final int baseAngle;

        public RotatedRoom(String room, int baseAngle) {
            this.name = room;
            this.baseAngle = baseAngle;
        }
    }

    public enum Direction {
        LEFT, RIGHT, TOP, BOTTOM;

        private Direction mirror() {
            switch (this) {
                case LEFT:
                    return RIGHT;
                case RIGHT:
                    return LEFT;
                case TOP:
                    return BOTTOM;
                case BOTTOM:
                    return TOP;
            }
            return null;
        }
    }

    // grab floating point problems from scp:cb console output
    public static void main(String[] args) throws IOException {
        String logFilePath = "genlog";
        Pattern pattern = Pattern.compile(".*problem for rooms (.*) =[0-9.]* (\\d*) / (.*) =[0-9.]* (\\d*) /.*");

        Files.lines(new File(logFilePath).toPath())
                .filter(l -> l.contains("Floating point numbers problem"))
                .forEach(line -> {
                    Matcher m = pattern.matcher(line);
                    m.find();
                    String r1 = m.group(1);
                    int a1 = Integer.parseInt(m.group(2));
                    String r2 = m.group(3);
                    int a2 = Integer.parseInt(m.group(4));
                    if (line.startsWith("r1.minX")) {
                        if (!hasRule(r1, a1, r2, a2, Direction.RIGHT)) {
                            addRule(r1, a1, r2, a2, Direction.RIGHT);
                        }
                    } else if (line.startsWith("r1.maxX")) {
                        if (!hasRule(r1, a1, r2, a2, Direction.LEFT)) {
                            addRule(r1, a1, r2, a2, Direction.LEFT);
                        }
                    } else if (line.startsWith("r1.minZ")) {
                        if (!hasRule(r1, a1, r2, a2, Direction.TOP)) {
                            addRule(r1, a1, r2, a2, Direction.TOP);
                        }
                    } else if (line.startsWith("r1.maxZ")) {
                        if (!hasRule(r1, a1, r2, a2, Direction.BOTTOM)) {
                            addRule(r1, a1, r2, a2, Direction.BOTTOM);
                        }
                    }
                });

        rules.forEach(r -> {
            System.out.printf("rules.add(new IntersectionRules(\"%s\", %d)\n", r.room.name, r.room.baseAngle);
            r.conflicts.keySet().forEach(d -> {
                r.conflicts.get(d).forEach(c -> {
                    System.out.printf(".addConflict(\"%s\", %d, Direction.%s)\n", c.name, c.baseAngle, d.name());
                });
            });
            System.out.println(");");
        });
    }
}
