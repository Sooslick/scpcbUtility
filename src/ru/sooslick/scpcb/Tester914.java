package ru.sooslick.scpcb;

import java.util.HashMap;
import java.util.Map;

public class Tester914 {
    private static final int ACHVS_CURR = 27;
    private static final int ACHVS_MAX = 37;
    private static final int DIFFICULTY_FACTOR = 5; // SAFE = 3, EUCLID = 4, KETER = 5
    private static final int CARDS = 10;
    private static final int ATTEMPTS = 100000;
    private static final int COMP_TIME_LIMIT_MS = 1000;

    public static void main(String[] args) {
        BlitzRandom.bbSeedRnd((int) System.currentTimeMillis());
        Map<Integer, Integer> results = new HashMap<>();
        int range = (ACHVS_MAX - 1) * DIFFICULTY_FACTOR - (ACHVS_CURR - 1) * 3;
        System.out.println("Setup:");
        System.out.println("Range: " + range + ". Random(range) should be 0 for omni spawn");
        System.out.println("Achievements: " + ACHVS_CURR + " / " + ACHVS_MAX);
        System.out.println("Inventory cards: " + CARDS);
        System.out.println("Simulating " + ATTEMPTS + " consecutive runs to get omni (using Blitz3D random function)");

        long ts = System.currentTimeMillis();
        for (int i = 0; i < ATTEMPTS; i++) {
            //time break
            if (System.currentTimeMillis() - ts > COMP_TIME_LIMIT_MS) {
                System.out.println(i + " / " + ATTEMPTS);
                ts = System.currentTimeMillis();
            }

            boolean retry = true;
            Integer attempt = 0;    // number of roll
            while (retry) {
                // rolling cards until omni spawns
                for (int j = 0; j < CARDS; j++) {
                    if (BlitzRandom.bbRand(0, range) == 0) {
                        retry = false;
                        break;
                    }
                }
                attempt++;
            }

            // save run results by attempts count
            Integer result = results.get(attempt);
            result = result == null ? 1 : result + 1;
            results.put(attempt, result);
        }

        // print probabilities
        final int[] sum = {0};
        System.out.println();
        results.keySet().stream().sorted().forEach(k -> {
            sum[0] += results.get(k);
            System.out.println("ROLL #" + k + ": " + (sum[0] * 100.0 / ATTEMPTS) + "%");
        });
    }
}
