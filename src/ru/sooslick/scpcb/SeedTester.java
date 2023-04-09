package ru.sooslick.scpcb;

public class SeedTester {
//    final static String printable = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
//    final static String printable = " -0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    final static String printable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//    final static String printable = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//    final static String printable = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//    final static String printable = "0123456789";
    final static int printableLength = printable.length();
//    final int maxlen = 15;
    final static int maxlen = 4;
//    final static int minlen = 2;
    final static int minlen = 4;
    final static String base = "bmu23i0";
//    final static String base = "796994829";

    public static void main(String[] args) {
        int total = 0;
        int characters = minlen;
        int baseSeed = generateSeedNumber(base.toCharArray());

        // EXTREMELY UNOPTIMISED CYCLE.
        // OK for small alphabets or seed length < 8 characters, otherwise it will last forever

        int[] charPositions = new int[maxlen];                // from zeros
//        int[] charPositions = {23, 6, 8, 39, 17, 51, 9, 5};    // from last saved word
        char[] seed = new char[minlen];
        do {
            // test seed
            createSeed(seed, charPositions, characters);
            int test = generateSeedNumber(seed);
            if (test == baseSeed) {
//                System.out.println(new String(seed));                 // print every seed from new line
                System.out.print(new String(seed) + " ");               // print seeds in columns
                if (total++ > 24) {
                    total = 0;
                    System.out.println();
                }
            }
            // get next seed
            if (next(charPositions, 0) >= characters) {
                characters++;
                seed = new char[characters];
                total = 0;
                if (characters > maxlen)
                    break;
            }
        } while (true);
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

    static void createSeed(char[] seed, int[] charPositions, int length) {
        for (int i = 0; i < length; i++)
            seed[i] = printable.charAt(charPositions[i]);
    }

    static int next(int[] charPositions, int currentPosition) {
        if (++charPositions[currentPosition] >= printableLength) {
            charPositions[currentPosition] = 0;
            if (currentPosition + 1 < maxlen)
                return next(charPositions, currentPosition + 1);
            else
                return currentPosition + 1;
        }
        return currentPosition;
    }
}
