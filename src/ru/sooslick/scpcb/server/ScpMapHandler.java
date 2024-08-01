package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.sooslick.scpcb.MapExplorer;
import ru.sooslick.scpcb.SeedGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

public class ScpMapHandler implements HttpHandler {

    private static final String outputDir = ServerProperties.SAVE_LOCATION;
    private static final Random random = new Random();

    private long lastActivity = 0;
    private boolean saveEnable = true;

    public ScpMapHandler() {
        File d = new File(outputDir);
        d.mkdirs();
        if (!d.exists())
            saveEnable = false;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        lastActivity = System.currentTimeMillis();
        HashMap<String, String> queryParams = new HashMap<>();
        String query = httpExchange.getRequestURI().getQuery();
        if (query != null) {
            for (String entry : query.split("&")) {
                String[] kv = entry.split("=");
                String k = kv[0].toLowerCase();
                String v = kv.length > 1 ? kv[1] : null;
                queryParams.put(k, v);
            }
        }

        String seed = null;
        Function<String, Integer> method = null;
        // vanilla seed prompt
        if (queryParams.containsKey("prompt")) {
            seed = queryParams.get("prompt");
            method = SeedGenerator.V1311;
        }
        // speedrun mod
        else if (queryParams.containsKey("seed")) {
            seed = queryParams.get("seed");
            if (seed.matches("-?[0-9]+")) {
                method = SeedGenerator.SPEEDRUN_MOD;
            } else {
                method = SeedGenerator.V1311;
            }
        }
        // bro gimme cool map
        else if (queryParams.containsKey("random")) {
            if (random.nextBoolean()) {
                seed = String.valueOf(random.nextInt());
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
        MapExplorer pf = SeedGenerator.generateMap(seed, method);
        String out = pf.exportJson();
        if (saveEnable) {
            String filename = outputDir + File.separator + seedNumber + ".json";
            System.out.println("Write file attempt: " + filename);
            try {
                Files.write(Paths.get(filename), out.getBytes(), StandardOpenOption.CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        answer(httpExchange, out, 200);
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

    private void answer(HttpExchange e, String content, int code) {
        byte[] answer = content.getBytes();
        try {
            e.sendResponseHeaders(code, answer.length);
            e.getResponseBody().write(answer);
        } catch (IOException io) {
            System.out.println("unable to answer");
            io.printStackTrace();
        }
        e.close();
    }
}
