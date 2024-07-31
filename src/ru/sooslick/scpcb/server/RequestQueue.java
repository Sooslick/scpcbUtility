package ru.sooslick.scpcb.server;

import ru.sooslick.scpcb.MapExplorer;
import ru.sooslick.scpcb.SeedGenerator;

import java.util.function.Function;

public class RequestQueue {

    public static synchronized String requestMap(String seed, Function<String, Integer> method) {
        MapExplorer pf = SeedGenerator.generateMap(seed, method);
        return pf.exportJson();
    }
}
