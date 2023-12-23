package ru.sooslick.scpcb.map;

/**
 * Make comparisons with two floating point numbers with SCP:CB rooms intersections specific adjustments
 */
public class BlitzFloatMath {

    public static boolean maxXLesserOrEquals(ScpcbRoom r1, ScpcbRoom r2) {
        if (testRuleMaxX(r1, r2))
            return false;
        return r1.maxX <= r2.minX;
    }

    public static boolean minXBiggerOrEquals(ScpcbRoom r1, ScpcbRoom r2) {
        if (testRuleMinX(r1, r2))
            return false;
        return r1.minX >= r2.maxX;
    }

    public static boolean maxZLesserOrEquals(ScpcbRoom r1, ScpcbRoom r2) {
        if (testRuleMaxZ(r1, r2))
            return false;
        return r1.maxZ <= r2.minZ;
    }

    public static boolean minZBiggerOrEquals(ScpcbRoom r1, ScpcbRoom r2) {
        if (testRuleMinZ(r1, r2))
            return false;
        return r1.minZ >= r2.maxZ;
    }

    private static boolean testRuleMinX(ScpcbRoom r1, ScpcbRoom r2) {
        if (r1.x - 8.0 == r2.x) {
            if (IntersectionRules.hasRule(r1.roomTemplate.name, r1.extentsAngle,
                    r2.roomTemplate.name, r2.extentsAngle,
                    IntersectionRules.Direction.RIGHT, r1.x)) {
                System.out.println("r1.minX >= r2.maxX Floating point numbers problem for rooms " + r1.roomTemplate.name + " " + r1.extentsAngle + " / " + r2.roomTemplate.name + " " + r2.extentsAngle);
                return true;
            }
        }
        return false;
    }

    private static boolean testRuleMaxX(ScpcbRoom r1, ScpcbRoom r2) {
        if (r1.x + 8.0 == r2.x) {
            if (IntersectionRules.hasRule(r1.roomTemplate.name, r1.extentsAngle,
                    r2.roomTemplate.name, r2.extentsAngle,
                    IntersectionRules.Direction.LEFT, r1.x)) {
                System.out.println("r1.maxX <= r2.minX Floating point numbers problem for rooms " + r1.roomTemplate.name + " " + r1.extentsAngle + " / " + r2.roomTemplate.name + " " + r2.extentsAngle);
                return true;
            }
        }
        return false;
    }

    private static boolean testRuleMinZ(ScpcbRoom r1, ScpcbRoom r2) {
        if (r1.z - 8.0 == r2.z) {
            if (IntersectionRules.hasRule(r1.roomTemplate.name, r1.extentsAngle,
                    r2.roomTemplate.name, r2.extentsAngle,
                    IntersectionRules.Direction.TOP, r1.z)) {
                System.out.println("r1.minZ >= r2.maxZ Floating point numbers problem for rooms " + r1.roomTemplate.name + " " + r1.extentsAngle + " / " + r2.roomTemplate.name + " " + r2.extentsAngle);
                return true;
            }
        }
        return false;
    }

    private static boolean testRuleMaxZ(ScpcbRoom r1, ScpcbRoom r2) {
        if (r1.z + 8.0 == r2.z) {
            if (IntersectionRules.hasRule(r1.roomTemplate.name, r1.extentsAngle,
                    r2.roomTemplate.name, r2.extentsAngle,
                    IntersectionRules.Direction.BOTTOM, r1.z)) {
                System.out.println("r1.maxZ <= r2.minZ Floating point numbers problem for rooms " + r1.roomTemplate.name + " " + r1.extentsAngle + " / " + r2.roomTemplate.name + " " + r2.extentsAngle);
                return true;
            }
        }
        return false;
    }
}
