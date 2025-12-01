package kanban.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new GsonAdapters.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new GsonAdapters.DurationAdapter())
            .create();
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        sendText(h, text, 200);
    }

    protected void sendText(HttpExchange h, String text, int rCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        if (resp.length == 0) {
            h.sendResponseHeaders(rCode, -1);
        } else {
            h.sendResponseHeaders(rCode, resp.length);
        }
        if (resp.length > 0) {
            h.getResponseBody().write(resp);
        }
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String message) throws IOException {
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasInteractions(HttpExchange h, String message) throws IOException {
        byte[] resp = message.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}

