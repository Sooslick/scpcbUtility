package ru.sooslick.scpcb;

public class SeedTester {
//    private final static String base = "bmu23i0";
//    private final static String base = "796994829";
    private final static String base = "QZI";

    public static void main(String[] args) {
        int total = 0;
        int baseSeed = generateSeedNumber(base.toCharArray());

        // EXTREMELY UNOPTIMISED CYCLE.
        // OK for small alphabets or seed length < 8 characters, otherwise it will last forever

        int[] charPositions = {23, 6, 8, 39, 17, 51, 9, 5};    // last saved word
        char[] seed;

        // max possible seed length: 15;
        BruteForce bf = new BruteForce(BruteForce.ALL_LETTERS, 2, 4);
//        BruteForce bf = new BruteForce(BruteForce.ALL_LETTERS, 2, 4, charPositions);

        do {
            // test seed
            seed = bf.next();
            int test = generateSeedNumber(seed);
            if (test == baseSeed) {
//                System.out.println(new String(seed));                 // print every seed from new line
                System.out.print(new String(seed) + " ");               // print seeds in columns
                if (total++ % 24 == 0)
                    System.out.println();
            }
        } while (!bf.isFinished());
        System.out.println("\nTotal seeds found: " + total);
    }

    // exact function from SCP:CB
    static int generateSeedNumber(char[] seed) {
        int tmp = 0;
        int shift = 0;
        for (char c : seed) {
            tmp = tmp ^ (c << shift);
            shift = (shift + 1) % 24;
        }
        return tmp;
    }
}
