package ru.sooslick.scpcb;

import ru.sooslick.scpcb.map.Map;

import java.util.function.Function;

/**
 * Utility class for creating SCP:CB v1.3.11 map by given seed
 */
public class SeedGenerator {

    public static final Function<String, Integer> V1311 = SeedGenerator::generateSeedNumber;
    public static final Function<String, Integer> SPEEDRUN_MOD = Integer::parseInt;

    /**
     * Create a vanilla SCP:CB v1.3.11 map
     *
     * @param randomSeed input string
     */
    public static Map generateMap(String randomSeed) {
        return generateMap(randomSeed, V1311);
    }

    /**
     * Create a vanilla SCP:CB v1.3.11 map using direct seeding
     *
     * @param randomSeed seed number
     */
    public static Map generateMap(int randomSeed) {
        return new Map(randomSeed);
    }

    /**
     * Create an SCP:CB v1.3.11 map, using custom Input String -> Seed Number transform
     *
     * @param randomSeed    input string
     * @param seedConverter function to transform input string to seed number
     */
    public static Map generateMap(String randomSeed, Function<String, Integer> seedConverter) {
        int actualSeed = seedConverter.apply(randomSeed);
        return new Map(actualSeed);
    }

    /**
     * Exact function from vanilla SCP:CB that transforms user input to seed number
     *
     * @param seed user input string
     * @return actual seed number
     */
    public static int generateSeedNumber(char[] seed) {
        int tmp = 0;
        int shift = 0;
        for (char c : seed) {
            tmp = tmp ^ (c << shift);
            shift = (shift + 1) % 24;
        }
        return tmp;
    }

    /**
     * transforms user input to 1.3.11 seed number
     *
     * @param seed user input string
     * @return actual seed number
     */
    public static int generateSeedNumber(String seed) {
        return generateSeedNumber(seed.toCharArray());
    }
}
