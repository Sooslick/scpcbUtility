package ru.sooslick.scpcb.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {

    public static int SERVER_PORT = 1499;
    public static int MAX_REQ_PER_SEC = 6;
    public static long IDLE_LIMIT = 1800000;
    public static long CHECKUP_INTERVAL = 300000;
    public static boolean FRONTEND_ENABLE = true;
    public static int RANKED_SEARCH_RANGE = 50;
    public static int RANKED_SEARCH_THRESHOLD = 570;

    static {
        try (InputStream is = new FileInputStream("server.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            SERVER_PORT = Integer.parseInt(properties.getProperty("server.port"));
            MAX_REQ_PER_SEC = Integer.parseInt(properties.getProperty("max.requests.per.second"));
            IDLE_LIMIT = Long.parseLong(properties.getProperty("idle.limit.seconds")) * 1000;
            CHECKUP_INTERVAL = Long.parseLong(properties.getProperty("checkup.interval.seconds")) * 1000;
            FRONTEND_ENABLE = Boolean.parseBoolean(properties.getProperty("frontend.enable"));
            RANKED_SEARCH_RANGE = Integer.parseInt(properties.getProperty("ranked.search.range"));
            RANKED_SEARCH_THRESHOLD = Integer.parseInt(properties.getProperty("ranked.search.threshold"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
