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

    static {
        try (InputStream is = new FileInputStream("server.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            SERVER_PORT = Integer.parseInt(properties.getProperty("server.port"));
            MAX_REQ_PER_SEC = Integer.parseInt(properties.getProperty("max.requests.per.second"));
            IDLE_LIMIT = Long.parseLong(properties.getProperty("idle.limit.seconds")) * 1000;
            CHECKUP_INTERVAL = Long.parseLong(properties.getProperty("checkup.interval.seconds")) * 1000;
            FRONTEND_ENABLE = Boolean.parseBoolean(properties.getProperty("frontend.enable"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
