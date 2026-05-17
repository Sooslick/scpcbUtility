package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpExchange;
import ru.sooslick.scpcb.MapExplorer;
import ru.sooslick.scpcb.SeedGenerator;
import ru.sooslick.scpcb.map.Map;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

public class ScpMapHandler extends AbstractRatedHandler {

    private static final Random random = new Random();

    @Override
    protected void respond(HttpExchange httpExchange) throws IOException {
        HashMap<String, String> queryParams = new HashMap<>();
        String query = httpExchange.getRequestURI().getRawQuery();
        if (query != null) {
            for (String entry : query.split("&")) {
                String[] kv = entry.split("=");
                String k = kv[0].toLowerCase();
                String v = kv.length > 1 ? URLDecoder.decode(kv[1]) : null;
                queryParams.put(k, v);
            }
        }

        String seed = null;
        Function<String, Integer> method = null;
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
            if (random.nextBoolean()) {
                seed = String.valueOf(random.nextInt(Integer.MAX_VALUE));
                method = SeedGenerator.SPEEDRUN_MOD;
            } else {
                seed = randomPrompt();
                method = SeedGenerator.V1311;
            }
        }

        if (seed == null) {
            answer(httpExchange, "No seed passed.", 400);
            return;
        }

        int seedNumber = method.apply(seed);
        System.out.printf("User prompt: %s (%s)%n", seed, seedNumber);
        try {
            Map map = SeedGenerator.generateMap(seed, method);
            MapExplorer pf = new MapExplorer(seed, method.apply(seed), map);
            String out = pf.exportJson();
            answer(httpExchange, out, 200);
        } catch (Exception e) {
            answer(httpExchange, "Map generator failed to create map " + seed, 500);
        }
    }

    private String randomPrompt() {
        StringBuilder sb = new StringBuilder();
        int max = random.nextInt(12) + 4;
        for (int i = 0; i < max; i++)
            sb.append((char) (random.nextInt(96) + 32));
        return sb.toString();
    }

    private boolean validatePrompt(String s) {
        if (s == null || s.isEmpty() || s.length() > 15)
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
}
