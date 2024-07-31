package ru.sooslick.scpcb.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ScpWebHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String reqPath = httpExchange.getRequestURI().getPath();
        if ("/frontend/".equals(reqPath))
            reqPath = "/frontend/index.html";

        String fpath = reqPath.substring(1);
        File f = new File(fpath);
        if (!f.exists()) {
            String answer = "404.";
            httpExchange.sendResponseHeaders(404, 4);
            httpExchange.getResponseBody().write(answer.getBytes());
            httpExchange.close();
            return;
        }

        InputStream is = new FileInputStream(fpath);
        byte[] answer = readAllBytes(is);
        httpExchange.sendResponseHeaders(200, answer.length);
        httpExchange.getResponseBody().write(answer);
        httpExchange.close();
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[inputStream.available()];
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        dataInputStream.readFully(bytes);
        return bytes;
    }
}
