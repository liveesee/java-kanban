package kanban.handler;

import com.sun.net.httpserver.HttpExchange;
import kanban.manager.TaskManager;
import kanban.exception.TimeConflictException;
import kanban.exception.NotFoundException;
import kanban.model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String endpoint = getEndpoint(path, method);

            switch (endpoint) {
                case "GET_TASKS":
                    handleGetAllTasks(exchange);
                    break;
                case "GET_TASK{ID}":
                    handleGetTaskByID(exchange);
                    break;
                case "POST_TASK":
                    handlePostTask(exchange);
                    break;
                case "DELETE_TASK{ID}":
                    handleDeleteTask(exchange);
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

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getAllTasks());
        sendText(exchange, response);
    }

    private void handleGetTaskByID(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            throw new NotFoundException("Идентификатор задачи не указан");
        }
        Task task = taskManager.getTaskById(taskIdOptional.get());
        String response = gson.toJson(task);
        sendText(exchange, response);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        Task taskFromJson = readTaskFromJson(exchange);
        int taskId = taskFromJson.getId();
        if (taskId > 0) {
            taskManager.updateTask(taskFromJson);
            String response = gson.toJson(taskFromJson);
            sendText(exchange, response, 201);
            return;
        }
        taskManager.createTask(taskFromJson);
        String response = gson.toJson(taskFromJson);
        sendText(exchange, response, 201);
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            throw new NotFoundException("Идентификатор задачи не указан");
        }
        taskManager.deleteTaskById(taskIdOptional.get());
        sendText(exchange, "{\"message\":\"Задача с идентификатором "
                + taskIdOptional.get() + " удалена\"}");
    }


    private String getEndpoint(String requestPath, String requestMethod) {
        String[] parts = requestPath.split("/");
        if ("GET".equalsIgnoreCase(requestMethod) && parts.length == 2) {
            return "GET_TASKS";
        }
        if ("GET".equalsIgnoreCase(requestMethod) && parts.length == 3) {
            return "GET_TASK{ID}";
        }
        if ("POST".equalsIgnoreCase(requestMethod) && parts.length == 2) {
            return "POST_TASK";
        }
        if ("DELETE".equalsIgnoreCase(requestMethod) && parts.length == 3) {
            return "DELETE_TASK{ID}";
        }
        return "unknown";
    }


    private Task readTaskFromJson(HttpExchange exchange) throws IOException {
        Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Task.class);
        if (task == null) {
            throw new IllegalArgumentException("Не удалось прочитать данные задачи из JSON");
        }
        return task;
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
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
