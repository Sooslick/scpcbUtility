package ru.sooslick.scpcb;

import ru.sooslick.scpcb.map.Map;
import ru.sooslick.scpcb.pathfinder.CommonStartPathFinder;
import ru.sooslick.scpcb.pathfinder.SSLegacyPathFinder;
import ru.sooslick.scpcb.pathfinder.SSPathFinder;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Utility class for creating SCP:CB v1.3.11 map by given seed
 */
public class SeedGenerator {

    public static final Function<String, Integer> V1311 = (seed) -> generateSeedNumber(seed.toCharArray());
    public static final Function<String, Integer> SPEEDRUN_MOD = Integer::parseInt;

    public static void main(String[] args) {
        HashMap<String, String> params = CommandLineArgumentParser.parse(args);

        // seed printer block
        String targetSeed = params.getOrDefault("--seed", "badsigfile");
        Function<String, Integer> mode = params.containsKey("--modded") ? SPEEDRUN_MOD : V1311;

        System.out.println("Generating a map using the seed '" + targetSeed + "'");
        MapExplorer pf = generateMap(targetSeed, mode);
        pf.printMaze();
        pf.printForest();
        pf.printTunnels();
        System.out.println(pf.exportJson());
        System.out.println();
        System.out.println("SS A1 Route length : " + pf.testRouteLength(new SSPathFinder()));
        System.out.println("SS Legacy Route length : " + pf.testRouteLength(new SSLegacyPathFinder()));
        System.out.println("914 Route length: " + pf.testRouteLength(new CommonStartPathFinder()));
    }

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
