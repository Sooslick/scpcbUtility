package ru.sooslick.scpcb;

import ru.sooslick.scpcb.pathfinder.AnyPercentPathFinder;

import java.util.Scanner;

public class SeedFinder {

    public static void main(String[] args) {
        int routeLengthThreshold = args.length > 0 ? Integer.parseInt(args[0]) : 50;
        AnyPercentPathFinder pf = new AnyPercentPathFinder();
        Scanner sc = new Scanner(System.in);

        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            MapExplorer map = SeedGenerator.generateMap(String.valueOf(i), SeedGenerator.SPEEDRUN_MOD);
            int routeLength = map.testRouteLength(pf);
            if (routeLength < routeLengthThreshold) {
                map.printMaze();
                System.out.println("Route length: " + routeLength);

                String prompt = sc.nextLine();
                if (prompt.startsWith("q"))
                    break;
            }
        }
    }
}
