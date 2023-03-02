package ru.sooslick.scpcb;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class SeedFilter {

    // Utility script to filter TesterSeed output by dictionary from WordLadder

    public static void main(String[] args) {
        String seedTesterOutput = "testme.txt";     // <-- specify your file here
        String dictionaryPath = "WL_DICT.txt";

        try {
            List<String> dictionary = Files.readAllLines(Paths.get(dictionaryPath)).stream()
                    .filter(s -> s.length() == 4)
                    .collect(Collectors.toList());
            Files.readAllLines(Paths.get(seedTesterOutput)).stream()
                    .map(String::toUpperCase)
                    .filter(dictionary::contains)
                    .forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
