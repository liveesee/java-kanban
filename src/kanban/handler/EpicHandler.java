package kanban.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.manager.TaskManager;
import kanban.exception.NotFoundException;
import kanban.model.Epic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .registerTypeAdapter(Duration.class, new DurationAdapter())
        .create();

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(dtf));
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            return LocalDateTime.parse(jsonReader.nextString(), dtf);
        }
    }

    private static class DurationAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(duration.toMinutes());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            long minutes = jsonReader.nextLong();
            return Duration.ofMinutes(minutes);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String endpoint = getEndpoint(path, method);
            switch (endpoint) {
                case "GET_EPICS":
                    handleGetAllEpics(exchange);
                    break;
                case "GET_EPIC{ID}":
                    handleGetEpicByID(exchange);
                    break;
                case "GET_EPIC_SUBTASKS":
                    handleGetEpicSubtasks(exchange);
                    break;
                case "POST_EPIC":
                    handlePostEpic(exchange);
                    break;
                case "DELETE_EPIC{ID}":
                    handleDeleteEpic(exchange);
                    break;
                default:
                    sendNotFound(exchange, "Такого эндпоинта не существует");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllEpics());
        sendText(exchange, response);
    }

    private void handleGetEpicByID(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOptional = getEpicId(exchange);
        if (epicIdOptional.isEmpty()) {
            throw new NotFoundException("Идентификатор эпика не указан");
        }
        Epic epic = taskManager.getEpicById(epicIdOptional.get());
        String response = gson.toJson(epic);
        sendText(exchange, response);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOptional = getEpicIdFromPath(exchange);
        if (epicIdOptional.isEmpty()) {
            throw new NotFoundException("Идентификатор эпика не указан");
        }
        taskManager.getEpicById(epicIdOptional.get());
        String response = gson.toJson(taskManager.getSubtasksByEpicId(epicIdOptional.get()));
        sendText(exchange, response);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        Epic epicFromJson = readEpicFromJson(exchange);
        int epicId = epicFromJson.getId();
        if (epicId > 0) {
            try {
                taskManager.getEpicById(epicId);
                taskManager.updateEpic(epicFromJson);
                String response = gson.toJson(epicFromJson);
                sendText(exchange, response, 201);
                return;
            } catch (NotFoundException e) {
                throw new NotFoundException(e.getMessage());
            }
        }
        taskManager.createEpic(epicFromJson);
        String response = gson.toJson(epicFromJson);
        sendText(exchange, response, 201);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> epicIdOptional = getEpicId(exchange);
        if (epicIdOptional.isEmpty()) {
            throw new NotFoundException("Идентификатор эпика не указан");
        }
        Epic epic = taskManager.getEpicById(epicIdOptional.get());
        taskManager.deleteEpicById(epic.getId());
        sendText(exchange, "{\"message\":\"Эпик с идентификатором "
                + epicIdOptional.get() + " удален\"}");
    }

    private String getEndpoint(String requestPath, String requestMethod) {
        String[] parts = requestPath.split("/");
        if ("GET".equalsIgnoreCase(requestMethod) && parts.length == 2) {
            return "GET_EPICS";
        }
        if ("GET".equalsIgnoreCase(requestMethod) && parts.length == 3) {
            return "GET_EPIC{ID}";
        }
        if ("GET".equalsIgnoreCase(requestMethod) && parts.length == 4 && "subtasks".equals(parts[3])) {
            return "GET_EPIC_SUBTASKS";
        }
        if ("POST".equalsIgnoreCase(requestMethod) && parts.length == 2) {
            return "POST_EPIC";
        }
        if ("DELETE".equalsIgnoreCase(requestMethod) && parts.length == 3) {
            return "DELETE_EPIC{ID}";
        }
        return "unknown";
    }

    private Epic readEpicFromJson(HttpExchange exchange) throws IOException {
        Epic epic = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Epic.class);
        if (epic == null) {
            throw new IllegalArgumentException("Не удалось прочитать данные эпика из JSON");
        }
        return epic;
    }

    private Optional<Integer> getEpicId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (parts.length < 3) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private Optional<Integer> getEpicIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (parts.length < 3) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(parts[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
