package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public abstract class AbstractRatedHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            if (!RateLimiter.checkRate(httpExchange.getRemoteAddress())) {
                answer(httpExchange, "Too many requests", 429);
                return;
            }

            respond(httpExchange);
        } catch (Exception e) {
            e.printStackTrace();
            answer(httpExchange, "Internal server error", 500);
        }
    }

    protected abstract void respond(HttpExchange httpExchange) throws IOException;

    protected void answer(HttpExchange e, String content, int code) throws IOException {
        byte[] answer = content.getBytes();
        e.getResponseHeaders().add("Content-Type", code == 200 ? "application/json" : "text/plain");
        e.sendResponseHeaders(code, answer.length);
        e.getResponseBody().write(answer);
        e.close();
    }
}
