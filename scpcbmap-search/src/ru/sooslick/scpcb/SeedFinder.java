package ru.sooslick.scpcb;

import ru.sooslick.scpcb.map.Map;
import ru.sooslick.scpcb.pathfinder.PathFinder;
import ru.sooslick.scpcb.pathfinder.PathFinderFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Program for brute force search
 */
public class SeedFinder {

    public static void main(String[] args) throws ReflectiveOperationException {
        HashMap<String, String> params = CommandLineArgumentParser.parse(args);
        if (params.containsKey("--list-path-finders")) {
            PathFinderFactory.detectPathFinders();
            return;
        }

        List<PathFinderParams> pfs = parse(params.getOrDefault("--path-finders", "ru.sooslick.scpcb.pathfinder.SSPathFinder:40"));
        int start = Integer.parseInt(params.getOrDefault("--start", "1"));
        int end = Integer.parseInt(params.getOrDefault("--end", "2147483647"));
        boolean printMaze = params.containsKey("--print-maze");

        if (params.containsKey("--seed")) {
            System.out.println("Analyzing single seed");
            start = Integer.parseInt(params.get("--seed"));
            end = start;
            pfs.forEach(pf -> pf.maxLength = Integer.MAX_VALUE);
        }
        search(pfs, start, end, printMaze);
    }

    private static void search(List<PathFinderParams> pathFinders, int start, int end, boolean printMaze) {
        for (int i = start; i <= end; i++) {
            try {
                Map map = SeedGenerator.generateMap(i);
                MapExplorer mapExplorer = new MapExplorer(null, i, map);
                pathFinders.forEach(pfp -> {
                    PathFinder pf = pfp.getPathFinder();
                    int routeLength = mapExplorer.testRouteLength(pf);
                    if (routeLength < pfp.getMaxLength()) {
                        if (printMaze)
                            mapExplorer.printMaze();
                        System.out.println(map.seed + " - " + pf.getName() + ", route length " + routeLength + "  -->  https://sooslick.art/scpcbmap/index?seed=" + map.seed);
                    }
                });
            } catch (Exception e) {
                System.out.println("Error generating seed " + i);
            }
        }
    }

    private static List<PathFinderParams> parse(String arg) throws ReflectiveOperationException {
        List<PathFinderParams> pfList = new LinkedList<>();
        String[] pfstrings  = arg.split(",");
        for (String pfstring : pfstrings) {
            String[] pfp = pfstring.trim().split(":");
            pfList.add(new PathFinderParams(PathFinderFactory.createInstance(pfp[0]), Integer.parseInt(pfp[1])));
        }
        return pfList;
    }

    private static class PathFinderParams {
        PathFinder pathFinder;
        int maxLength;

        public PathFinderParams(PathFinder pathFinder, int maxLength) {
            this.pathFinder = pathFinder;
            this.maxLength = maxLength;
        }

        public PathFinder getPathFinder() {
            return pathFinder;
        }

        public int getMaxLength() {
            return maxLength;
        }
    }
}
