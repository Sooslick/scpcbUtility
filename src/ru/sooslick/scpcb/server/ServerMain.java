package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerMain {

    public static void main(String[] args) {
        System.out.println("Starting HTTP server at port 1499");
        HttpServer server;
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(1499), 8);
        } catch (IOException e) {
            System.out.println("Unable to start HTTP server: " + e.getMessage());
            return;
        }
        ScpMapHandler handler = new ScpMapHandler();
        server.createContext("/map", handler);
        server.createContext("/", new ScpStatusHandler());
        server.createContext("/frontend/", new ScpWebHandler());
        ExecutorService exec = Executors.newFixedThreadPool(8);
        server.setExecutor(exec);
        server.start();

        boolean alive = true;
        while (alive) {
            try {
                TimeUnit.SECONDS.sleep(ServerProperties.CHECKUP_INTERVAL);
            } catch (InterruptedException e) {
                alive = false;
            }
            long lastActivity = handler.getLastRequestTime();
            long idleTime = System.currentTimeMillis() - lastActivity;
            System.out.println(LocalDateTime.now() + " " + idleTime + "ms since last activity");
            if (idleTime > ServerProperties.IDLE_LIMIT)
                alive = false;
        }

        System.out.println(LocalDateTime.now() + " Shutdown");
        server.stop(0);
        exec.shutdownNow();
    }
}
