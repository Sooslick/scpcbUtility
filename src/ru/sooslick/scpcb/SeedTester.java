package ru.sooslick.scpcb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SeedTester {
    public final static String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public final static String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public final static String NUMBERS = "0123456789";

    int baseSeed = 7839;
    boolean[] controlBytes = new boolean[32];

    String dictionary = UPPERCASE + LOWERCASE;
    int dictionaryLength = dictionary.length();
    int length = 8;

    int[] chars;
    int[] state;
    int[] result;

    SeedPrinter printer = new DictionarySeedPrinter();

    public static void main(String[] args) {
        SeedTester seedTester = new SeedTester();
        if (args.length > 0)
            seedTester.baseSeed = Integer.parseInt(args[0]);
        if (args.length > 1)
            seedTester.dictionary = args[1];
        seedTester.search();
    }

    public void search() {
        calcControlBytes();
        initState();
        calcState(0);
    }

    private void calcControlBytes() {
        for (int i = 0; i < 32; i++)
            controlBytes[i] = testByte(baseSeed, i);
    }

    private boolean testByte(int victim, int position) {
        return (victim & 1 << position) >> position == 1;
    }

    private void initState() {
        chars = new int[dictionaryLength];
        for (int i = 0; i < dictionaryLength; i++)
            chars[i] = dictionary.charAt(i);

        state = new int[length];
        result = new int[length];
    }

    private void calcState(int pointer) {
        if (pointer >= length) {
            if (result[pointer - 1] == baseSeed)
                printSeed(printer);
            return;
        }

        int prevResult = pointer == 0 ? 0 : result[pointer - 1];
        boolean controlByte = controlBytes[pointer];
        while (updateResult(pointer, prevResult)) {
            if (testByte(result[pointer], pointer) == controlByte)
                calcState(pointer + 1);
            state[pointer]++;
        }
    }

    private boolean updateResult(int pointer, int prevResult) {
        if (state[pointer] >= dictionaryLength) {
            state[pointer] = 0;
            return false;
        }

        int currentChar = chars[state[pointer]];
        result[pointer] = prevResult ^ (currentChar << pointer);
        return true;
    }

    private void printSeed(SeedPrinter printer) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append((char) chars[state[i]]);
        printer.print(sb.toString());
    }

    ///////////////////////////////

    private interface SeedPrinter {
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
        private final List<String> dictionary;

        private DictionarySeedPrinter() {
            try {
                dictionary = new ArrayList<>(
                        Files.readAllLines(Paths.get("WL_DICT.txt")));
            } catch (IOException e) {
                throw new RuntimeException("bruh gimme acces to dictionary", e);
            }
        }

        public void print(String seed) {
            if (dictionary.contains(seed.toUpperCase().replaceAll("[^A-Z]", "")))
                System.out.println(seed);
        }
    }
}
