package ru.sooslick.scpcb;

import ru.sooslick.scpcb.pathfinder.PathFinder;
import ru.sooslick.scpcb.pathfinder.PathFinderFactory;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Utility class for brute force search
 */
public class SeedFinder {

    public static void main(String[] args) throws ReflectiveOperationException {
        HashMap<String, String> params = CommandLineArgumentParser.parse(args);

        int routeLengthThreshold = params.containsKey("--max-length") ? Integer.parseInt(params.get("--max-length")) : 30;
        PathFinder pf = PathFinderFactory.createInstance(params.getOrDefault("--path-finder", "ru.sooslick.scpcb.pathfinder.SSA1PathFinder"));
        int start = Integer.parseInt(params.getOrDefault("--start", "1"));
        int end = Integer.parseInt(params.getOrDefault("--end", "2147483647"));
        int mode = Integer.parseInt(params.getOrDefault("--mode", "0"));

        switch (mode) {
            case 0:
            default:
                searchMode0(pf, routeLengthThreshold, start, end);
                break;
            case 1:
                searchMode1(pf, routeLengthThreshold, start, end);
                break;
        }
    }

    /**
     * --mode=0
     * Search will stop after every found seed and wait for user prompt
     */
    private static void searchMode0(PathFinder pf, int maxLength, int start, int end) {
        Scanner sc = new Scanner(System.in);
        for (int i = start; i < end; i++) {
            MapExplorer map = SeedGenerator.generateMap(String.valueOf(i), SeedGenerator.SPEEDRUN_MOD);
            int routeLength = map.testRouteLength(pf);
            if (routeLength < maxLength) {
                map.printMaze();
                System.out.println("Route length: " + routeLength);
                String prompt = sc.nextLine();
                if (prompt.startsWith("q"))
                    break;
            }
        }
    }

    /**
     * --mode=1
     * Search will work quietly and print every found seed to system out
     */
    private static void searchMode1(PathFinder pf, int maxLength, int start, int end) {
        for (int i = start; i < end; i++) {
            MapExplorer map = SeedGenerator.generateMap(String.valueOf(i), SeedGenerator.SPEEDRUN_MOD);
            int routeLength = map.testRouteLength(pf);
            if (routeLength < maxLength) {
                System.out.println(map.seedPrompt + "  -->  https://sooslick.art/scpcbmap/index?seed=" + map.seedPrompt);
            }
        }
    }
}
