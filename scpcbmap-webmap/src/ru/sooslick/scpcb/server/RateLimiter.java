package ru.sooslick.scpcb.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class RateLimiter {
    protected static long lastActivity = 0;

    private static final int rate = ServerProperties.MAX_REQ_PER_SEC;
    private static HashMap<InetSocketAddress, LinkedList<Long>> lastRequests = new HashMap<>();

    private static long lastCleanupTs = System.currentTimeMillis();

    public static boolean checkRate(InetSocketAddress remoteAddr) {
        lastActivity = System.currentTimeMillis();
        cleanup();

        // not enough requests
        long now = System.currentTimeMillis();
        LinkedList<Long> reqTs = lastRequests.computeIfAbsent(remoteAddr, (k) -> new LinkedList<>());
        if (reqTs.size() < rate) {
            reqTs.add(now);
            return true;
        }

        // too many req
        if (now - reqTs.getFirst() < 1000)
            return false;

        reqTs.add(now);
        reqTs.removeFirst();
        return true;
    }

    private static void cleanup() {
        long now = System.currentTimeMillis();
        if (now - lastCleanupTs < 60_000)
            return;
        lastCleanupTs = now;

        lastRequests.keySet().stream()
                .filter(ip -> {
                    LinkedList<Long> reqTs = lastRequests.get(ip);
                    return (reqTs.isEmpty() || reqTs.getFirst() < lastCleanupTs);
                })
                .collect(Collectors.toList())
                .forEach(ip -> lastRequests.remove(ip));
    }
}
