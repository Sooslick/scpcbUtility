package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class ScpStatusHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange e) throws IOException {
        byte[] answer = "SCP:CB WebMap version v1.5.2-260616".getBytes();
        e.sendResponseHeaders(200, answer.length);
        e.getResponseBody().write(answer);
        e.close();
    }
}
