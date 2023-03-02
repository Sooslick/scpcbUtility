package ru.sooslick.scpcb;

public class SeedTester {
//    final static String printable = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
//    final static String printable = " -0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    final static String printable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
//    final static String printable = "0123456789";
    final static int printableLength = printable.length();
//    final int maxlen = 15;
    final static int maxlen = 6;
//    final static int minlen = 2;
    final static int minlen = 4;
    final static String base = "bmu23i0";

    public static void main(String[] args) {
        int total = 0;
        int characters = minlen;
        int baseSeed = generateSeedNumber(base);

        // EXTREMELY UNOPTIMISED CYCLE.
        // OK for small alphabets or seed length < 8 characters, otherwise it will last forever

        int[] charPositions = new int[maxlen];
        do {
            // test seed
            String seed = createSeed(charPositions, characters);
            int test = generateSeedNumber(seed);
            if (test == baseSeed) {
//                System.out.println(seed);                 // print every seed from new line
                System.out.print(seed + " ");               // print seeds in columns
                if (total++ > 16) {
                    total = 0;
                    System.out.println();
                }
            }
            // get next seed
            if (next(charPositions, 0) >= characters) {
                characters++;
                total = 0;
                if (characters > maxlen)
                    break;
            }
        } while (true);
    }

    // exact function from SCP:CB
    static int generateSeedNumber(String seedStr) {
        char[] chars = seedStr.toCharArray();
        int tmp = 0;
        int shift = 0;
        for (char c : chars) {
            tmp = tmp ^ (c << shift);
            shift = (shift + 1) % 24;
        }
        return tmp;
    }

    // redundant transform bytes to string
    static String createSeed(int[] charPositions, int length) {
        char[] str = new char[length];
        for (int i = 0; i < length; i++)
            str[i] = printable.charAt(charPositions[i]);
        return new String(str);
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
