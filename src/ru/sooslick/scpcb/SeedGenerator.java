package ru.sooslick.scpcb;

import ru.sooslick.scpcb.map.Map;
import ru.sooslick.scpcb.pathfinder.AnyPercentPathFinder;
import ru.sooslick.scpcb.pathfinder.CommonStartPathFinder;

import java.util.function.Function;

public class SeedGenerator {

    public static final Function<String, Integer> V1311 = (seed) -> generateSeedNumber(seed.toCharArray());
    public static final Function<String, Integer> SPEEDRUN_MOD = Integer::parseInt;

    public static void main(String[] args) {
        // seed printer block
        String targetSeed = args.length > 0 ? args[0] : "446456054";
        Function<String, Integer> mode = args.length > 1 ? SPEEDRUN_MOD : V1311;
        MapExplorer pf = generateMap(targetSeed, mode);
        pf.printMaze();
        pf.printForest();
        pf.printTunnels();
        System.out.println(pf.exportJson());
        System.out.println("Route full : " + pf.testRouteLength(new AnyPercentPathFinder()));
        System.out.println("Route start: " + pf.testRouteLength(new CommonStartPathFinder()));
    }

    public static MapExplorer generateMap(String randomSeed) {
        return generateMap(randomSeed, V1311);
    }

    public static MapExplorer generateMap(String randomSeed, Function<String, Integer> seedConverter) {
        System.out.println("Generating a map using the seed '" + randomSeed + "'");
        int actualSeed = seedConverter.apply(randomSeed);
        Map map = new Map(actualSeed);
        return new MapExplorer(randomSeed, map);
    }

    // exact function from SCP:CB
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
