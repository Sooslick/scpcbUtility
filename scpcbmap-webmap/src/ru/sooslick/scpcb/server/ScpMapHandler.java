package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.sooslick.scpcb.SeedGenerator;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

public class ScpMapHandler implements HttpHandler {

    private static final Random random = new Random();

    private long lastActivity = 0;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        lastActivity = System.currentTimeMillis();
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
                seed = String.valueOf(random.nextInt(Integer.MAX_VALUE) + 1);
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
            String out = RequestQueue.requestMap(seed, method);
            answer(httpExchange, out, 200);
        } catch (Exception e) {
            answer(httpExchange, "Map generator failed to create map " + seed, 500);
        }
    }

    public long getLastRequestTime() {
        return lastActivity;
    }

    private String randomPrompt() {
        StringBuilder sb = new StringBuilder();
        int max = random.nextInt(12) + 4;
        for (int i = 0; i < max; i++)
            sb.append((char) (random.nextInt(96) + 32));
        return sb.toString();
    }

    private void answer(HttpExchange e, String content, int code) throws IOException {
        byte[] answer = content.getBytes();
        e.sendResponseHeaders(code, answer.length);
        e.getResponseBody().write(answer);
        e.close();
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
}
