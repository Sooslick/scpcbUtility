package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ScpStatusHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange e) throws IOException {
        byte[] answer = "Sooslick was there".getBytes();
        e.sendResponseHeaders(200, answer.length);
        e.getResponseBody().write(answer);
        e.close();
    }
}
