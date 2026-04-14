package ru.sooslick.scpcb.server;

import ru.sooslick.scpcb.MapExplorer;
import ru.sooslick.scpcb.SeedGenerator;
import ru.sooslick.scpcb.map.Map;

import java.util.function.Function;

public class RequestQueue {

    public static synchronized String requestMap(String seed, Function<String, Integer> method) {
        Map map = SeedGenerator.generateMap(seed, method);
        MapExplorer pf = new MapExplorer(seed, method.apply(seed), map);
        return pf.exportJson();
    }
}
