package ru.sooslick.scpcb;

public class BruteForce {
    public final static String ASCII_HALF = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    public final static String HUMAN_READABLE = " -0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public final static String ALL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public final static String CAPS_AND_NUMBERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public final static String CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public final static String NUMBERS = "0123456789";
    public final static String CUSTOM = "@ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final String printable;
    private final int minLen;
    private final int maxLen;
    private final int[] charPositions;

    private final int printableLength;

    private int currentLength;
    private char[] seed;
    private boolean finished = false;

    public BruteForce(String characters) {
        this(characters, 2, 4);
    }

    public BruteForce(String characters, int min, int max) {
        this(characters, min, max, new int[max]);
    }

    public BruteForce(String characters, int min, int max, int[] positions) {
        this(characters, min, max, min, positions);
    }

    public BruteForce(String characters, int min, int max, int currentLength, int[] positions) {
        this.printable = characters;
        this.minLen = min;
        this.maxLen = max;
        this.charPositions = copyPositions(positions, max);

        this.printableLength = printable.length();
        this.currentLength = min;
        this.seed = new char[currentLength];
    }

    public boolean isFinished() {
        return finished;
    }

    public void printState() {
        System.out.print(currentLength + "/ ");
        for (int i : charPositions)
            System.out.print(i + ", ");
        System.out.println();
    }

    public char[] next() {
        if (finished)
            return seed;

        // step 1: generate seed;
        for (int i = 0; i < currentLength; i++)
            seed[i] = printable.charAt(charPositions[i]);

        //step 2: shift characters;
        if (shift(charPositions, 0) >= currentLength) {
            currentLength++;
            seed = new char[currentLength];
            if (currentLength > maxLen)
                finished = true;
        }

        return seed;
    }

    private int shift(int[] charPositions, int currentPosition) {
        if (++charPositions[currentPosition] >= printableLength) {
            charPositions[currentPosition] = 0;
            if (currentPosition + 1 < maxLen)
                return shift(charPositions, currentPosition + 1);
            else
                return currentPosition + 1;
        }
        return currentPosition;
    }

    private int[] copyPositions(int[] original, int maxLength) {
        int[] result = new int[maxLength];
        System.arraycopy(original, 0, result, 0, Math.min(original.length, maxLength));
        return result;
    }
}
