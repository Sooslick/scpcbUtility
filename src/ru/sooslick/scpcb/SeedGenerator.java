package ru.sooslick.scpcb;

import ru.sooslick.scpcb.map.Map;

import java.util.function.Function;

/**
 * Utility class for creating SCP:CB v1.3.11 map by given seed
 */
public class SeedGenerator {

    public static final Function<String, Integer> V1311 = (seed) -> generateSeedNumber(seed.toCharArray());
    public static final Function<String, Integer> SPEEDRUN_MOD = Integer::parseInt;

    /**
     * Create a vanilla SCP:CB v1.3.11 map
     *
     * @param randomSeed input string
     */
    public static MapExplorer generateMap(String randomSeed) {
        return generateMap(randomSeed, V1311);
    }

    /**
     * Create an SCP:CB v1.3.11 map, using custom Input String -> Seed Number transform
     *
     * @param randomSeed    input string
     * @param seedConverter function to transform input string to seed number
     */
    public static MapExplorer generateMap(String randomSeed, Function<String, Integer> seedConverter) {
        int actualSeed = seedConverter.apply(randomSeed);
        Map map = new Map(actualSeed);
        return new MapExplorer(randomSeed, actualSeed, map);
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
}
