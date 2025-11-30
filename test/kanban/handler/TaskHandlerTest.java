package kanban.handler;

import kanban.model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest extends HttpTaskServerTest {

    @Override
    protected int getPort() {
        return 8080;
    }

    @Test
    void shouldReturnAllTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/task"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description", null, null);
        taskManager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/task/" + task.getId()))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnNotFoundWhenGettingNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/task/999"))
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {
        String taskJson = "{\"title\":\"Test\",\"description\":\"Description\",\"status\":\"NEW\"}";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/task"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(taskJson))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Original", "Description", null, null);
        taskManager.createTask(task);
        String updatedJson = String.format(
            "{\"id\":%d,\"title\":\"Updated\",\"description\":\"Description\",\"status\":\"NEW\"}",
            task.getId()
        );
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/task"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("Updated", taskManager.getTaskById(task.getId()).getTitle());
    }

    @Test
    void shouldReturnTimeConflictWhenCreatingOverlappingTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description", "10:00 01.01.24", "60");
        taskManager.createTask(task1);
        String task2Json = "{\"title\":\"Task 2\",\"description\":\"Description\"," +
                "\"status\":\"NEW\",\"startTime\":\"10:30 01.01.24\",\"duration\":60}";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/task"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(task2Json))
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description", null, null);
        taskManager.createTask(task);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/task/" + task.getId()))
            .DELETE()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/task/999"))
            .DELETE()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}

