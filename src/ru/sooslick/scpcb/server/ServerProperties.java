package ru.sooslick.scpcb.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {

    public static long IDLE_LIMIT = 1800000;
    public static long CHECKUP_INTERVAL = 300;

    static {
        try (InputStream is = new FileInputStream("server.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            IDLE_LIMIT = Long.parseLong(properties.getProperty("idle.limit.seconds")) * 1000;
            CHECKUP_INTERVAL = Long.parseLong(properties.getProperty("checkup.interval.seconds"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
