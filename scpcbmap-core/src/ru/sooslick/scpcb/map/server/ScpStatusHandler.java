package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ScpStatusHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange e) {
        byte[] answer = "Sooslick was there".getBytes();
        try {
            e.sendResponseHeaders(200, answer.length);
            e.getResponseBody().write(answer);
        } catch (IOException io) {
            System.out.println("unable to answer");
            io.printStackTrace();
        }
        e.close();
    }
}
