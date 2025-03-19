package ru.sooslick.scpcb;

import java.util.HashMap;

public class CommandLineArgumentParser {

    public static HashMap<String, String> parse(String[] args) {
        HashMap<String, String> params = new HashMap<>();
        for (String arg : args) {
            String[] parts = arg.split("=", 2);
            String k = parts[0];
            String v = parts.length > 1 ? parts[1] : null;
            params.put(k, v);
        }
        return params;
    }
}
