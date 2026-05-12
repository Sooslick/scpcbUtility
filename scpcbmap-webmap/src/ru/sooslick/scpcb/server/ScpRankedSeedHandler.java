package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpExchange;
import ru.sooslick.scpcb.MapExplorer;
import ru.sooslick.scpcb.SeedGenerator;
import ru.sooslick.scpcb.map.Map;
import ru.sooslick.scpcb.pathfinder.RankedPathFinder;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

public class ScpRankedSeedHandler extends AbstractRatedHandler {

    private static final Random random = new Random();
    private static final RankedPathFinder rpf = new RankedPathFinder();
    private static final int rankedThreshold = ServerProperties.RANKED_SEARCH_THRESHOLD;
    private static final int rankedSearchRange = ServerProperties.RANKED_SEARCH_RANGE;

    @Override
    protected void respond(HttpExchange httpExchange) throws IOException {
        HashMap<String, String> queryParams = new HashMap<>();
        String query = httpExchange.getRequestURI().getRawQuery();
        if (query != null) {
            for (String entry : query.split("&")) {
                String[] kv = entry.split("=");
                String k = kv[0].toLowerCase();
                String v = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : null;
                queryParams.put(k, v);
            }
        }

        String seed = null;
        Function<String, Integer> method = null;
        boolean searchMode = false;
        // vanilla seed prompt
        if (queryParams.containsKey("prompt")) {
            seed = queryParams.get("prompt");
            method = SeedGenerator.V1311;
            if (!validatePrompt(seed))
                answer(httpExchange, "Bad prompt.", 400);
        }
        // speedrun mod
        else if (queryParams.containsKey("seed")) {
            seed = queryParams.get("seed");
            method = SeedGenerator.SPEEDRUN_MOD;
            if (!validateSeed(seed))
                answer(httpExchange, "Bad seed.", 400);
        }
        // bro gimme cool map
        else if (queryParams.containsKey("random")) {
            seed = String.valueOf(random.nextInt(Integer.MAX_VALUE));
            method = SeedGenerator.SPEEDRUN_MOD;
            searchMode = true;
        }

        if (seed == null) {
            answer(httpExchange, "No seed passed.", 400);
            return;
        }

        try {
            RankedSeed bestSeed = null;

            if (searchMode) {
                int start = random.nextInt(Integer.MAX_VALUE - rankedSearchRange);
                int end = start + rankedSearchRange;

                for (int i = start; i <= end; i++) {
                    try {
                        Map map = SeedGenerator.generateMap(i);
                        MapExplorer mapExplorer = new MapExplorer(null, i, map);
                        int routeLength = rpf.calcRouteLength(mapExplorer);
                        // redflag #1: ranked threshold
                        if (routeLength > rankedThreshold)
                            continue;
                        // redflag #2: no instant PD exit
                        if (map.savedRooms.stream().noneMatch(r -> r.rndInfo != null && r.rndInfo.contains("Pocket Dimension exit")))
                            continue;
                        // redflag #3: room2ccont in close proximity to gate a
                        if (rpf.gateACloseProximity(mapExplorer))
                            continue;
                        // redflag #4: 939 blocker
                        if (rpf.is939blocking(mapExplorer))
                            continue;
                        if (rpf.hasUnbeatableOverlap(mapExplorer))
                            continue;

                        bestSeed = new RankedSeed(i, map, mapExplorer, routeLength);
                        break;
                    } catch (Exception e) {
                        System.out.println("Error generating seed " + i);
                        e.printStackTrace();
                    }
                }
            }

            // failsafe branch, return random seed if search failed (I don't expect any searches with null bestSeed)
            if (bestSeed == null) {
                Map map = SeedGenerator.generateMap(seed, method);
                MapExplorer pf = new MapExplorer(seed, method.apply(seed), map);
                int estimate = pf.testRouteLength(rpf);
                bestSeed = new RankedSeed(map.seed, map, pf, estimate);

                if (pf.findRoom("room2ccont") == null)
                    bestSeed.issues = bestSeed.issues + "cont;";
                if (pf.findRoom("room079") == null)
                    bestSeed.issues = bestSeed.issues + "079;";
                if (map.savedRooms.stream().noneMatch(r -> r.rndInfo != null && r.rndInfo.contains("Pocket Dimension exit")))
                    bestSeed.issues = bestSeed.issues + "PD;";
                if (rpf.gateACloseProximity(pf))
                    bestSeed.issues = bestSeed.issues + "MTF;";
                if (rpf.is939blocking(pf))
                    bestSeed.issues = bestSeed.issues + "939;";
                if (rpf.hasUnbeatableOverlap(pf))
                    bestSeed.issues = bestSeed.issues + "106;";
            }

            String out = buildResponse(bestSeed);
            answer(httpExchange, out, 200);
        } catch (Exception e) {
            answer(httpExchange, "Map generator failed to create map " + seed, 500);
        }
    }

    private boolean validatePrompt(String s) {
        if (s.isEmpty() || s.length() > 15)
            return false;
        for (char c : s.toCharArray())
            if (c < 32 || c >= 128)
                return false;
        return true;
    }

    private boolean validateSeed(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private String buildResponse(RankedSeed rs) {
        System.out.printf("Ranked return: %s (%s)%n", rs.mapExplorer.prompt, rs.seed);
        return "{\"seedString\":\"%s\",\"seedValue\":%d,\"loadingScreen\":\"%s\",\"estimate\":%d,\"issues\":\"%s\"}"
                .formatted(rs.mapExplorer.prompt, rs.seed, rs.map.loadingScreen, rs.rankedEstimate, rs.issues);
    }

    private static class RankedSeed {
        int seed;
        Map map;
        MapExplorer mapExplorer;
        int rankedEstimate;
        String issues = "";

        RankedSeed(int seed, Map map, MapExplorer mapExplorer, int rankedEstimate) {
            this.seed = seed;
            this.map = map;
            this.mapExplorer = mapExplorer;
            this.rankedEstimate = rankedEstimate;
        }
    }
}
