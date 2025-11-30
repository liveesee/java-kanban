package kanban.handler;

import com.google.gson.TypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.manager.TaskManager;
import kanban.exception.TimeConflictException;
import kanban.exception.NotFoundException;
import kanban.model.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
                case "GET_SUBTASKS":
                    handleGetAllSubtasks(exchange);
                    break;
                case "GET_SUBTASK{ID}":
                    handleGetSubtaskByID(exchange);
                    break;
                case "POST_SUBTASK":
                    handlePostSubtask(exchange);
                    break;
                case "DELETE_SUBTASK{ID}":
                    handleDeleteSubtask(exchange);
                    break;
                default:
                    sendNotFound(exchange, "Такого эндпоинта не существует");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TimeConflictException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendText(exchange, "{\"error\":\"" + e.getMessage() + "\"}", 500);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllSubtasks());
        sendText(exchange, response);
    }

    private void handleGetSubtaskByID(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOptional = getSubtaskId(exchange);
        if (subtaskIdOptional.isEmpty()) {
            throw new NotFoundException("Идентификатор подзадачи не указан");
        }
        Subtask subtask = taskManager.getSubtaskById(subtaskIdOptional.get());
        String response = gson.toJson(subtask);
        sendText(exchange, response);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        Subtask subtaskFromJson = readSubtaskFromJson(exchange);
        int subtaskId = subtaskFromJson.getId();
        if (subtaskId > 0) {
            try {
                taskManager.getSubtaskById(subtaskId);
                taskManager.updateSubtask(subtaskFromJson);
                String response = gson.toJson(subtaskFromJson);
                sendText(exchange, response, 201);
                return;
            } catch (NotFoundException e) {
                throw new NotFoundException(e.getMessage());
            }
        }
        taskManager.createSubtask(subtaskFromJson);
        String response = gson.toJson(subtaskFromJson);
        sendText(exchange, response, 201);
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> subtaskIdOptional = getSubtaskId(exchange);
        if (subtaskIdOptional.isEmpty()) {
            throw new NotFoundException("Идентификатор подзадачи не указан");
        }
        Subtask subtask = taskManager.getSubtaskById(subtaskIdOptional.get());
        taskManager.deleteSubtaskById(subtask.getId());
        sendText(exchange, "{\"message\":\"Подзадача с идентификатором "
                + subtaskIdOptional.get() + " удалена\"}");
    }

    private String getEndpoint(String requestPath, String requestMethod) {
        String[] parts = requestPath.split("/");
        if ("GET".equalsIgnoreCase(requestMethod) && parts.length == 2) {
            return "GET_SUBTASKS";
        }
        if ("GET".equalsIgnoreCase(requestMethod) && parts.length == 3) {
            return "GET_SUBTASK{ID}";
        }
        if ("POST".equalsIgnoreCase(requestMethod) && parts.length == 2) {
            return "POST_SUBTASK";
        }
        if ("DELETE".equalsIgnoreCase(requestMethod) && parts.length == 3) {
            return "DELETE_SUBTASK{ID}";
        }
        return "unknown";
    }

    private Subtask readSubtaskFromJson(HttpExchange exchange) throws IOException {
        Subtask subtask = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Subtask.class);
        if (subtask == null) {
            throw new IllegalArgumentException("Не удалось прочитать данные подзадачи из JSON");
        }
        return subtask;
    }

    private Optional<Integer> getSubtaskId(HttpExchange exchange) {
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
