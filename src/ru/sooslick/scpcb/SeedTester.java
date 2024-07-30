package ru.sooslick.scpcb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SeedTester {
    public static void main(String[] args) throws IOException {
        int baseSeed = 56141;

        // EXTREMELY UNOPTIMISED CYCLE.
        // OK for small alphabets or seed length < 8 characters, otherwise it will last forever
        // TODO introduce new bruteforce function with binary math that optimized for seed search

        int[] charPositions = {2, 2, 2, 2, 2, 2, 26, 0, 1, 0};    // last saved word
        char[] seed;

        // max possible seed length: 15;
//        BruteForce bf = new BruteForce(BruteForce.ALL_LETTERS, 2, 6);
        BruteForce bf = new BruteForce(BruteForce.CUSTOM, 10, 10, charPositions);
//        SeedPrinter p = new SeedPrinterImpl();
        SeedPrinter p = new DictionarySeedPrinter();

        do {
            seed = bf.next();
            if (SeedGenerator.generateSeedNumber(seed) == baseSeed) {
                p.print(new String(seed));
            }
        } while (!bf.isFinished());
    }

    private static interface SeedPrinter {
        void print(String seed);
    }

    private static class SeedPrinterImpl implements SeedPrinter {
        final int maxCols = 16;
        int col = 0;

        public void print(String seed) {
            System.out.print(seed + " ");
            if (++col > maxCols) {
                System.out.println();
                col = 0;
            }
        }
    }

    private static class DictionarySeedPrinter implements SeedPrinter {
        private final List<String> dictionary = new ArrayList<>(
                Files.readAllLines(Paths.get("WL_DICT.txt")));

        private DictionarySeedPrinter() throws IOException {
        }

        public void print(String seed) {
            if (dictionary.contains(seed.toUpperCase().replaceAll("[A-Z]", "")))
                System.out.println(seed);
        }
    }
}
